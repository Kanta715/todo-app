package controllers

import ixias.model.Entity
import lib.model.Todo.Status.{DONE, IS_ACTIVE, IS_INACTIVE}
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model.TodoForm.TodoEditData._
import model._
import lib.model.{Todo, TodoCategory}
import lib.persistence.onMySQL.TodoCategoryRepository

import scala.concurrent.{Await, ExecutionContext, Future}
import model.TodoForm.TodoData._
import lib.persistence.onMySQL.TodoRepository
import model.TodoForm.TodoEditData
import model.TodoVV._

import scala.concurrent.duration.Duration



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
        title         = "TODO-List",
        cssSrc        = Seq("todo/todoList.css"),
        jsSrc         = Seq("main.js"),
        todoList      = todo,
        categoryList  = category
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
            form    = formWithErrors
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
      todo match {
        case Some(todo@Entity(v)) =>
          val vv = ViewValueEdit(
            title  = "TODO編集",
            cssSrc = Seq("todo/store.css"),
            jsSrc  = Seq("main.js"),
            form   = todoEditForm.fill(TodoForm.TodoEditData(v.title, v.body, v.state, v.category_id))
          )
          Ok(views.html.todo.Edit(vv, todo.id, category))
        case _  => Redirect("/todo/list")
      }
    }

  }

  def update(Id:  Int) = Action.async { implicit request =>
    todoEditForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoForm.TodoEditData]) => {
        val categoryList = TodoCategoryRepository.getAll()
        for {
          list <- categoryList
        } yield {
          val vv = ViewValueEdit(
            title  = "TODO編集",
            cssSrc = Seq("todo/store.css"),
            jsSrc  = Seq("main.js"),
            form = formWithErrors
          )
          BadRequest(views.html.todo.Edit(vv, Todo.Id(Id.toLong), list))
        }
      },
      (todoFormData: TodoForm.TodoEditData) => {
        for{
          todoInfo       <-  TodoRepository.get(Todo.Id(Id.toLong))
          edit           <-  Future.successful {
            todoInfo match {
              case Some(todo@Entity(_)) => {
                Some(todo.map(_.copy(title = todoFormData.title, body = todoFormData.body, state = todoFormData.status, category_id = todoFormData.categoryId)))
              }
              case None => None
            }
          }
          update         <-  edit match {
            case Some(data) =>  TodoRepository.update(data)
            case None       =>  Future.successful(None)
          }
        } yield {
          update match {
            case Some(_) => {
              Redirect("/todo/list")
            }
            case None    => {
              BadRequest(views.html.error.page404(model.ViewValueHome(
                "Error",
                Seq("main.css"),
                Seq("main.js")
              )))
            }
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
        case Some(_)  =>
          Redirect("/todo/list")
      }
    }

  }

}
