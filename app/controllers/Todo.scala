package controllers

import lib.model.Todo.Status.IS_INACTIVE
import lib.persistence.onMySQL._

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model._
import lib.model.Todo
import lib.model.TodoCategory

import scala.concurrent.Future
import scala.util.Success
//import lib.persistence.TodoRepository
import model.TodoData._
import lib.persistence.onMySQL.TodoRepository

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import slick.jdbc.JdbcProfile



@Singleton
class TodoController @Inject()(
val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def todoList() = Action.async { implicit request =>
    for{
      vv        <-  Future{
        ViewValueList(
          title   = "TODO-List",
          cssSrc  = Seq("todo/todoList.css"),
          jsSrc   = Seq("todo/todoList.js")
        )
      }
      todoInfo  <-  TodoRepository.getAll()
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
      todoAll      <- Future.successful(todoGetAll)
      todoInfo     <- todoAll.find(v => v.id.get.toInt == Id)
    } yield {
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
          Future.successful(Ok(views.html.todo.Edit(vv, todo.id.get)))
        case _  => Future.successful(Redirect("todo/todoList"))
      }
    }

  }

  def update(Id:  Int) = Action { implicit request =>
    val allUser    = TodoRepository.getAll()
    val userList   = Await.result(allUser, Duration.Inf)
    val user       = userList.find(v => v.id.get.toInt == Id)
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoData]) => {
        val vv = ViewValueHome(
          title = "TODO登録",
          cssSrc = Seq("todo/edit.css"),
          jsSrc = Seq("todo/todoList.js")
        )
        BadRequest(views.html.error.page404(vv))
      },
      (todoFormData: TodoData) => {
        val cId         = if (todoFormData.categoryName == "フロントエンド") 1 else if (todoFormData.categoryName == "バックエンド") 2 else 3
        val userInfo    = TodoRepository.get(user.get.id.get)
        val userRecord  = Await.result(userInfo, Duration.Inf).get
        val EditUser    = userRecord.map(_.copy(title = todoFormData.title, body = todoFormData.body, category_id = cId))
        TodoRepository.update(EditUser)

        Redirect("/todo/todoList")
      }
    )
  }

  def delete(Id:  Int) = Action { implicit request =>
    val allUser        =  TodoRepository.getAll()
    val userList       =  Await.result(allUser, Duration.Inf)
    val userInfo       =  userList.find(v => v.id.get.toInt == Id)
    userInfo match {
      case None        =>  NotFound("/error/page404")
      case Some(info)  =>
        val deleteUser =   TodoRepository.remove(info.id.get)
        Await.result(deleteUser, Duration.Inf)
        Redirect("/todo/todoList")
    }

  }

}
