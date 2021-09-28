package controllers

import ixias.model.Entity
import lib.model.Todo.Status.{DONE, IS_ACTIVE, IS_INACTIVE}
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model.TodoForm.TodoEditData._
import model._
import lib.model.Todo
import lib.persistence.onMySQL.TodoCategoryRepository

import scala.concurrent.Future
import model.TodoForm.TodoData._
import lib.persistence.onMySQL.TodoRepository
import model.TodoVV.{ViewValueEdit, ViewValueList, ViewValueStore}

import scala.concurrent.ExecutionContext



@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def list() = Action.async { implicit request =>
      val todoInfo     =  TodoRepository.getAll()
      val categoryInfo =  TodoCategoryRepository.getAll()
    for {
      todo      <-  todoInfo
      category  <-  categoryInfo
    } yield {
      val vv = ViewValueList(
        todo,
        category
      )
      Ok(views.html.todo.TodoList(vv))
    }
  }

  def register() = Action.async { implicit request =>
    val todoList    =  TodoRepository.getAll()
    val categoryList =  TodoCategoryRepository.getAll()
    for{
      todo     <- todoList
      category <- categoryList
    } yield {
      val vv = ViewValueStore(
        title   = "TODO登録",
        cssSrc  = Seq("todo/store.css"),
        jsSrc   = Seq("main.js"),
        form    = todoForm
      )
      Ok(views.html.todo.Store(vv, todo, category))
    }
  }

  def store() = Action.async { implicit request =>
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoForm.TodoData]) => {
        val todoList    =  TodoRepository.getAll()
        val categoryList =  TodoCategoryRepository.getAll()
        for{
          todo     <- todoList
          category <- categoryList
        } yield {
          val vv = ViewValueStore(
            title   = "TODO登録",
            cssSrc  = Seq("todo/store.css"),
            jsSrc   = Seq("main.js"),
            form    = todoForm
          )
          BadRequest(views.html.todo.Store(vv, todo, category))
        }
      },
      (todoFormData: TodoForm.TodoData) => {
        val todoTable = Todo(todoFormData.categoryId,  todoFormData.title,   todoFormData.body,  IS_INACTIVE)
        for {
          _         <- TodoRepository.add(todoTable)
        } yield {
          Redirect("/todo/list")
        }
      }
    )
  }

  def edit(Id:  Int) = Action.async { implicit request =>
    val categoryList = TodoCategoryRepository.getAll()
    val todoInfo     = TodoRepository.get(Todo.Id(Id.toLong))
    for{
      todo         <- todoInfo
      category     <- categoryList
    } yield {
      val categoryName  = for (name <- category.map(_.name)) yield name
      val nameList      = categoryName.foldLeft(List(): List[String])((x,y) => if(x.contains(y)) x else x :+ y)
      todo match {
        case Some(Entity(v)) =>
          val vv = ViewValueEdit(
            title  = "TODO編集",
            cssSrc = Seq("todo/store.css"),
            jsSrc  = Seq("main.js"),
            form   = todoEditForm.fill(TodoForm.TodoEditData(v.title, v.body, v.state.name, v.category_id))
          )
          Ok(views.html.todo.Edit(vv, v.id.get, nameList, category))
        case _  => Redirect("/todo/list")
      }
    }

  }

  def update(Id:  Int) = Action.async { implicit request =>
    todoEditForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoForm.TodoEditData]) => {
        val vv = ViewValueHome(
            title  = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc  = Seq("main.js")
        )
        Future.successful(BadRequest(views.html.error.page404(vv)))
      },
      (todoFormData: TodoForm.TodoEditData) => {
        for{
          todoInfo       <-  TodoRepository.get(Todo.Id(Id.toLong))
          editedTodo     = {
            val state = if (todoFormData.stateName == "TODO") IS_INACTIVE else if (todoFormData.stateName == "実行中") IS_ACTIVE else DONE
            todoInfo.get.map(_.copy(title = todoFormData.title, body = todoFormData.body, state = state, category_id = todoFormData.categoryId))
          }
          updateTodo     <- TodoRepository.update(editedTodo)
        } yield {
          val vv = ViewValueHome(
            title  = "Home",
            cssSrc = Seq("main.css"),
            jsSrc  = Seq("main.js")
          )
          updateTodo match {
            case Some(t) =>  Redirect("/todo/list")
            case None    =>  Ok(views.html.error.page404(vv))
          }
        }
      }
    )
  }

  def delete(Id:  Int) = Action.async { implicit request =>
    for{
      todo     <-  TodoRepository.remove(Todo.Id(Id.toLong))
    } yield {
      todo match {
        case None         =>
          val vv = ViewValueHome(
            title  = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc  = Seq("main.js")
          )
          BadRequest(views.html.error.page404(vv))
        case Some(value)  =>
          Redirect("/todo/list")
      }
    }

  }

}
