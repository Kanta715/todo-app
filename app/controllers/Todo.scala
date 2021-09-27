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
    for{
      todoInfo     <-  TodoRepository.getAll()
      categoryInfo <-  TodoCategoryRepository.getAll()
    } yield {
      val vv = ViewValueList(
        "TODO-List",
        Seq("todo/todoList.css"),
        Seq("todo/todoList.js"),
        todoInfo,
        categoryInfo
      )
      Ok(views.html.todo.TodoList(vv))
    }
  }

  def register() = Action { implicit request =>
    val vv = ViewValueStore(
      title   = "TODO登録",
      cssSrc  = Seq("todo/store.css"),
      jsSrc   = Seq("todo/todoList.js"),
      form    = todoForm
    )
    Ok(views.html.todo.Store(vv))
  }

  def store() = Action.async { implicit request =>
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoForm.TodoData]) => {
        val vv = ViewValueStore(
          title   = "TODO登録",
          cssSrc  = Seq("todo/store.css"),
          jsSrc   = Seq("todo/todoList.js"),
          form    = formWithErrors
        )
        Future.successful(BadRequest(views.html.todo.Store(vv)))
      },
      (todoFormData: TodoForm.TodoData) => {
        val cId       = if(todoFormData.categoryName == "フロントエンド") 1 else if(todoFormData.categoryName == "バックエンド") 2 else 3
        val todoTable = Todo(cId,  todoFormData.title,   todoFormData.body,  IS_INACTIVE)
        for {
          _         <- TodoRepository.add(todoTable)
        } yield {
          Redirect("/todo/list")
        }
      }
    )
  }

  def edit(Id:  Int) = Action.async { implicit request =>
    for{
      todoInfo     <- TodoRepository.get(Todo.Id(Id.toLong))
      todo         =  todoInfo.get
      categoryList <- TodoCategoryRepository.getAll()
    } yield {
      val categoryName  = for (name <- categoryList.map(_.name)) yield name
      val nameList      = categoryName.foldLeft(List(): List[String])((x,y) => if(x.contains(y)) x else x :+ y)
      todo match {
        case Entity(v) =>
          val vv = ViewValueEdit(
            title = "TODO編集",
            cssSrc = Seq("todo/store.css"),
            jsSrc = Seq("todo/todoList.js"),
            form = todoEditForm.fill(TodoForm.TodoEditData(v.title, v.body, v.state.name, v.category_id))
          )
          Ok(views.html.todo.Edit(vv, v.id.get, nameList))
        case _  => Redirect("/todo/list")
      }
    }

  }

  def update(Id:  Int) = Action.async { implicit request =>
    todoEditForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoForm.TodoEditData]) => {
        val vv = ViewValueHome(
            title = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc = Seq("todo/todoList.js")
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
            title = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc = Seq("todo/todoList.js")
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
            title = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc = Seq("todo/todoList.js")
          )
          BadRequest(views.html.error.page404(vv))
        case Some(value)  =>
          Redirect("/todo/list")
      }
    }

  }

}
