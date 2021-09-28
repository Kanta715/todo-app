package controllers

import lib.model.TodoCategory
import play.api.i18n.I18nSupport
import play.api.data.Form
import javax.inject._
import play.api.mvc._
import scala.concurrent.Future
import lib.persistence.onMySQL.TodoCategoryRepository
import model.CategoryForm.CategoryData
import model.CategoryForm.CategoryData.categoryForm
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
        val color    = if(categoryData.color == 1) TodoCategory.Color.RED else if(categoryData.color == 2)TodoCategory.Color.BLUE else TodoCategory.Color.GREEN
        val category = TodoCategory(categoryData.name, categoryData.slug, color)
        for{
          _         <- TodoCategoryRepository.add(category)
        } yield {
          Redirect("/category/list")
        }
      }
    )


  }
}
