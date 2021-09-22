package controllers

import lib.model.TodoCategory

import scala.concurrent.duration.Duration
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._
<<<<<<< HEAD

import scala.concurrent.Future
=======
import model._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import model.TodoForm.TodoData._
>>>>>>> 5ea6e3f (Merge pull request #2 from Kanta715/develop)
import lib.persistence.onMySQL.TodoCategoryRepository
import model.CategoryForm.CategoryData
import model.CategoryForm.CategoryData.categoryForm

<<<<<<< HEAD
=======
import scala.concurrent.Await
>>>>>>> 5ea6e3f (Merge pull request #2 from Kanta715/develop)
import scala.concurrent.ExecutionContext
import model.TodoCategoryVV._


@Singleton
class TodoCategoryController @Inject()(val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext)
  extends BaseController with I18nSupport {

  def list() = Action.async { implicit request =>
    for{
      list  <-  TodoCategoryRepository.getAll()
    } yield {
      val vv = ViewValueCategoryList(
        "Category-List",
        Seq("category/categoryList.css"),
        Seq("category/categoryList.js")
      )
      Ok(views.html.category.CategoryList(vv, list))
    }
  }

  def register() = Action { implicit request =>
    val vv = ViewValueCategoryStore(
      "Category登録",
      Seq("category/categoryList.css"),
      Seq("category/categoryList.js"),
      categoryForm
    )
    Ok(views.html.category.Store(vv))
  }

  def store() = Action.async { implicit request =>
    categoryForm.bindFromRequest().fold(
      (formWithErrors: Form[CategoryData]) => {
        val vv = ViewValueCategoryStore(
          "TODO登録",
          Seq("todo/store.css"),
          Seq("todo/todoList.js"),
          formWithErrors
        )
        Future.successful(BadRequest(views.html.category.Store(vv)))
      },
      (categoryData: CategoryData) => {
        for{
          color     <- Future(if(categoryData.color == "赤") 1 else if(categoryData.color == "青") 2 else 3)
          category  <- Future(TodoCategory(categoryData.name, categoryData.slug, color.toShort))
          _         <- TodoCategoryRepository.add(category)
        } yield {
          Redirect("/category/list")
        }
      }
    )


  }
}
