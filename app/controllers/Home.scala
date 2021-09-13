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

import javax.inject._
import play.api.mvc._
import model.ViewValueHome
import model.ViewValueList
import lib.model.User

import scala.concurrent.Await

@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

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
      jsSrc   = Seq("list.js")
    )
    val userTableInfo     = UserRepository.getAll()
    val userInfo          = Await.result(userTableInfo, Duration.Inf)

    val userCategoryInfo  = UserCategoryRepository.getAll()
    val userCategory  = Await.result(userCategoryInfo, Duration.Inf)
    Ok(views.html.UserList(vv, userInfo,  userCategory))
  }
}
