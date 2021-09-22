/**
 *
 * to do sample project
 *
 */

package controllers


import play.api.i18n.I18nSupport

import javax.inject._
import play.api.mvc._
import model._



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

}
