/**
 *
 * to do sample project
 *
 */

package controllers

import ixias.model.Entity.WithNoId
import lib.model.User.Status.IS_INACTIVE
import lib.persistence.onMySQL._

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form
import play.api.data.Forms._

import javax.inject._
import play.api.mvc._
import model._
import lib.model.User
import lib.model.UserCategory
import model.TodoData._
import views.html.helper.form
import play.api.mvc.Result

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
}
