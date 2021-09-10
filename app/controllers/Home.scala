/**
 *
 * to do sample project
 *
 */

package controllers

import ixias.model.Entity.WithNoId
import lib.model.User.Status.IS_INACTIVE
import lib.persistence.UserRepository

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

  def list() = Action { implicit request =>
    val vv = ViewValueList(
      title   = "TODO-List",
      cssSrc  = Seq("main.css"),
      jsSrc   = Seq("main.js")
    )
    val user: User#WithNoId = User( 5, "nextbeat", "nextbeat-identity", IS_INACTIVE)
    Ok(views.html.List(vv, user))
  }
}
