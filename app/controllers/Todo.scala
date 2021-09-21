package controllers

import lib.model.Todo.Status.IS_INACTIVE

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model._
import lib.model.Todo

import scala.concurrent.Future
import scala.util.{Failure, Success}
import model.TodoData._
import lib.persistence.onMySQL.TodoRepository

import scala.concurrent.Await
import scala.concurrent.ExecutionContext



@Singleton
class TodoController @Inject()(
                                val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def todoList() = Action.async { implicit request =>
    for{
      todoInfo  <-  TodoRepository.getAll()
      vv        <-  Future.successful{
        ViewValueList(
          title   = "TODO-List",
          cssSrc  = Seq("todo/todoList.css"),
          jsSrc   = Seq("todo/todoList.js")
        )
      }
    } yield {
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
      (formWithErrors: Form[TodoData]) => {
        for {
          vv <- Future{ViewValueStore(
            title   = "TODO登録",
            cssSrc  = Seq("todo/store.css"),
            jsSrc   = Seq("todo/todoList.js"),
            form    = formWithErrors
          )
          }
        } yield BadRequest(views.html.todo.Store(vv))
      },
      (todoFormData: TodoData) => {
        for {
          cId       <- Future{
            if(todoFormData.categoryName == "フロントエンド") 1 else if(todoFormData.categoryName == "バックエンド") 2 else 3
          }
          todoTable <- Future.successful(Todo(cId,  todoFormData.title,   todoFormData.body,  IS_INACTIVE))
          _         <- TodoRepository.add(todoTable)
        } yield {
          Redirect("/todo/todoList")
        }
      }
    )
  }

  def edit(Id:  Int) = Action.async { implicit request =>
    for{
      todoGetAll   <- TodoRepository.getAll()
    } yield {
      val todoInfo = todoGetAll.find(v => v.id.get.toInt == Id).get
      todoInfo match {
        case todo: Todo =>
          val c = todo.category_id match {
            case 1 => "フロントエンド"
            case 2 => "バックエンド"
            case 3 => "インフラ"
          }
          val vv  = ViewValueStore(
            title   = "TODO編集",
            cssSrc  = Seq("todo/store.css"),
            jsSrc   = Seq("todo/todoList.js"),
            form    = todoForm.fill(TodoData(todo.title,  todo.body,  c))
          )
          Ok(views.html.todo.Edit(vv, todo.id.get))
        case _  => Redirect("todo/todoList")
      }
    }

  }

  def update(Id:  Int) = Action.async { implicit request =>
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoData]) => {
        for {
          vv <- Future.successful{ViewValueHome(
            title = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc = Seq("todo/todoList.js")
          )}
        } yield {
          BadRequest(views.html.error.page404(vv))
        }
      },
      (todoFormData: TodoData) => {
        for{
          vv <- Future{ViewValueHome(
            title = "TODO登録",
            cssSrc = Seq("todo/edit.css"),
            jsSrc = Seq("todo/todoList.js")
          )}
          todoList       <-  TodoRepository.getAll()
          editedTodo     <-  Future.successful {
            val todo        = todoList.find(v => v.id.get.toInt == Id)
            val cId         = if (todoFormData.categoryName == "フロントエンド") 1 else if (todoFormData.categoryName == "バックエンド") 2 else 3
            val todoInfo    = TodoRepository.get(todo.get.id.get)
            val todoRecord  = Await.ready(todoInfo, Duration.Inf)
            val tdr         = todoRecord.value.get match {
              case Success(value) =>  value.get
            }
            tdr.map(_.copy(title = todoFormData.title, body = todoFormData.body, category_id = cId))
          }
          todo           <- TodoRepository.update(editedTodo)
        } yield {
          todo match {
            case Some(t) =>  Redirect("/todo/todoList")
            case None    =>  Ok(views.html.error.page404(vv))
          }
        }
      }
    )
  }

  def delete(Id:  Int) = Action.async { implicit request =>
    for{
      vv <- Future{ViewValueHome(
        title = "TODO登録",
        cssSrc = Seq("todo/edit.css"),
        jsSrc = Seq("todo/todoList.js")
      )}
      todoList <-  TodoRepository.getAll()
      todo     <-  Future.successful{
        val todoInfo  = todoList.find(v => v.id.get.toInt == Id)
        todoInfo match {
          case None => 1
          case Some(info) =>
            val deleteTodo = TodoRepository.remove(info.id.get)
            Await.ready(deleteTodo, Duration.Inf)
            0
        }
      }
    } yield {
      todo match {
        case 1  =>  BadRequest(views.html.error.page404(vv))
        case 0  =>  Redirect("/todo/todoList")
      }
    }

  }

}
