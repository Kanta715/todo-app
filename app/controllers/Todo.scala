package controllers

import lib.model.Todo.Status.{DONE, IS_ACTIVE, IS_INACTIVE}

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model.TodoForm.TodoEditData._
import model._
import lib.model.Todo

import scala.concurrent.Future
import scala.util.{Failure, Success}
import model.TodoForm.TodoData._
import lib.persistence.onMySQL.TodoRepository
import model.TodoVV.{ViewValueEdit, ViewValueList, ViewValueStore}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext



@Singleton
class TodoController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def list() = Action.async { implicit request =>
    for{
      todoInfo  <-  TodoRepository.getAll()
    } yield {
      val vv = ViewValueList(
        title   = "TODO-List",
        cssSrc  = Seq("todo/todoList.css"),
        jsSrc   = Seq("todo/todoList.js")
      )
      Ok(views.html.todo.TodoList(vv, todoInfo))
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
        for {
          cId       <- Future(if(todoFormData.categoryName == "フロントエンド") 1 else if(todoFormData.categoryName == "バックエンド") 2 else 3)
          todoTable <- Future(Todo(cId,  todoFormData.title,   todoFormData.body,  IS_INACTIVE))
          _         <- TodoRepository.add(todoTable)
        } yield {
          Redirect("/todo/list")
        }
      }
    )
  }

  def edit(Id:  Int) = Action.async { implicit request =>
    for{
      todoList   <- TodoRepository.getAll()
    } yield {
      val todoInfo = todoList.find(v => v.id.getOrElse(0) == Id)
      todoInfo match {
        case Some(todo) =>
          val c = todo.category_id match {
            case 1 => "フロントエンド"
            case 2 => "バックエンド"
            case 3 => "インフラ"
          }
          val vv  = ViewValueEdit(
            title   = "TODO編集",
            cssSrc  = Seq("todo/store.css"),
            jsSrc   = Seq("todo/todoList.js"),
            form    = todoEditForm.fill(TodoForm.TodoEditData(todo.title,  todo.body, todo.state.name,  c))
          )
          Ok(views.html.todo.Edit(vv, todo.id.get))
        case None  => Redirect("/todo/list")
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
          todoList       <-  TodoRepository.getAll()
          todo           <-  Future(todoList.find(v => v.id.getOrElse(0) == Id))
          todoInfo       <-  TodoRepository.get(todo.get.id.get)
          editedTodo     <-  Future{
            val cId = if (todoFormData.categoryName == "フロントエンド") 1 else if (todoFormData.categoryName == "バックエンド") 2 else 3
            val state = if (todoFormData.stateName == "TODO") IS_INACTIVE else if (todoFormData.stateName == "実行中") IS_ACTIVE else DONE
            todoInfo.get.map(_.copy(title = todoFormData.title, body = todoFormData.body, state = state, category_id = cId))
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
      todoList <-  TodoRepository.getAll()
      todo     <-  Future(todoList.find(v => v.id.get.toInt == Id))
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
          TodoRepository.remove(value.id.get)
          Redirect("/todo/list")
      }
    }

  }

}
