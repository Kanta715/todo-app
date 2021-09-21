package controllers

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
import model._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import model.TodoForm.TodoData._
import lib.persistence.onMySQL.TodoCategoryRepository

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import model.TodoCategoryVV._


@Singleton
class TodoCategoryController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def list() = Action.async { implicit request =>
    for{
      list  <-  TodoCategoryRepository.getAll()
      vv    <-  Future.successful{ViewValueCategoryList(
                  "Category-List",
                  Seq("category/categoryList.css"),
                  Seq("category/categoryList.js")
                )}
    } yield {
      Ok(views.html.category.CategoryList(vv, list))
    }
  }

}
