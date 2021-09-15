/**
 *
 * to do sample project
 *
 */

package controllers

import lib.model.User.Status.IS_INACTIVE
import lib.persistence.onMySQL._

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model._
import lib.model.User
import lib.model.UserCategory
import model.TodoData._

import scala.concurrent.Await



@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents)
  extends BaseController with I18nSupport {

  def index() = Action { implicit req =>
    val vv = ViewValueHome(
      title  = "Home",
      cssSrc = Seq("main.css"),
      jsSrc  = Seq("main.js")
    )
    Ok(views.html.Home(vv))
  }

  def userList() = Action { implicit request =>
    val vv = ViewValueList(
      title   = "TODO-List",
      cssSrc  = Seq("userList.css"),
      jsSrc   = Seq("userList.js")
    )
    val userTableInfo     = UserRepository.getAll()
    val userInfo          = Await.result(userTableInfo, Duration.Inf)

    val userCategoryInfo  = UserCategoryRepository.getAll()
    val userCategory  = Await.result(userCategoryInfo, Duration.Inf)
    Ok(views.html.UserList(vv, userInfo,  userCategory))
  }

  def register() = Action { implicit request =>
    val vv = ViewValueStore(
      title   = "TODO登録",
      cssSrc  = Seq("store.css"),
      jsSrc   = Seq("userList.js")
    )
    Ok(views.html.Store(vv, todoForm))
  }

  def store() = Action { implicit request =>
    val vv = ViewValueStore(
      title   = "TODO登録",
      cssSrc  = Seq("store.css"),
      jsSrc   = Seq("userList.js")
    )
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoData]) => {
        BadRequest(views.html.Store(vv, formWithErrors))
      },
      (todoFormData: TodoData) => {
        val cId = if(todoFormData.categoryName == "フロントエンド") 1 else if(todoFormData.categoryName == "バックエンド") 2 else 3
        val todoTable: User#WithNoId = User(cId,  todoFormData.title,   todoFormData.body,  IS_INACTIVE)
        UserRepository.add(todoTable)

        val slug = if(todoFormData.categoryName == "フロントエンド") "front" else if(todoFormData.categoryName == "バックエンド") "back" else "infra"
        val categoryTable: UserCategory#WithNoId = UserCategory(todoFormData.categoryName, slug, cId.toShort)
        UserCategoryRepository.add(categoryTable)
        Redirect("/userList")
      }
    )
  }

  def edit(Id:  Int) = Action { implicit request =>
    val vv = ViewValueStore(
      title   = "TODO編集",
      cssSrc  = Seq("store.css"),
      jsSrc   = Seq("userList.js")
    )
    val allUser    = UserRepository.getAll()
    val userList   = Await.result(allUser, Duration.Inf)
    val user       = userList.find(v => v.id.get.toInt == Id)
    user match {
      case None   =>  Ok("/userList")
      case Some(info)  =>
        val category  = {
        val categoryId  = info.category_id
        categoryId match  {
          case  1 =>  "フロントエンド"
          case  2 =>  "バックエンド"
          case  3 =>  "インフラ"
        }
      }
        val getUser  = user.get
        Ok(views.html.Edit(vv, getUser.id.get, todoForm.fill(TodoData(getUser.title,  getUser.body,  category))))
    }
  }

  def update(Id:  Int) = Action { implicit request =>
    val vv = ViewValueStore(
      title = "TODO登録",
      cssSrc = Seq("edit.css"),
      jsSrc = Seq("userList.js")
    )
    val allUser    = UserRepository.getAll()
    val userList   = Await.result(allUser, Duration.Inf)
    val user       = userList.find(v => v.id.get.toInt == Id)
    todoForm.bindFromRequest().fold(
      (formWithErrors: Form[TodoData]) => {
        BadRequest(views.html.Edit(vv, user.get.id.get, formWithErrors))
      },
      (todoFormData: TodoData) => {
        val cId         = if (todoFormData.categoryName == "フロントエンド") 1 else if (todoFormData.categoryName == "バックエンド") 2 else 3
        val userInfo    = UserRepository.get(user.get.id.get)
        val userRecord  = Await.result(userInfo, Duration.Inf).get
        val EditUser    = userRecord.map(_.copy(title = todoFormData.title, body = todoFormData.body, category_id = cId))
        UserRepository.update(EditUser)

        Redirect("/userList")
      }
    )
  }


}
