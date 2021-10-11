package controllers

import ixias.model.{Entity, IdStatus}
import lib.model.TodoCategory
import play.api.i18n.I18nSupport
import play.api.data.Form

import javax.inject._
import play.api.mvc._

import scala.concurrent.Future
import lib.persistence.onMySQL.{TodoCategoryRepository, TodoRepository}
import model.CategoryForm.CategoryData
import model.CategoryForm.CategoryData.categoryForm

import scala.concurrent.ExecutionContext
import model.TodoCategoryVV._
import model.ViewValueHome


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
        Seq("main.js")
      )
      Ok(views.html.category.CategoryList(vv, list))
    }
  }

  def register() = Action { implicit request =>
    val vv = ViewValueCategoryStore(
      "Category登録",
      Seq("todo/store.css"),
      Seq("main.js"),
      categoryForm
    )
    Ok(views.html.category.Store(vv))
  }

  def store() = Action.async { implicit request =>
    categoryForm.bindFromRequest().fold(
      (formWithErrors: Form[CategoryData]) => {
        val vv = ViewValueCategoryStore(
          "Category登録",
          Seq("todo/store.css"),
          Seq("main.js"),
          formWithErrors
        )
        Future.successful(BadRequest(views.html.category.Store(vv)))
      },
      (categoryData: CategoryData) => {
        val category = TodoCategory(categoryData.name, categoryData.slug, categoryData.color)
        for{
          _         <- TodoCategoryRepository.add(category)
        } yield {
          Redirect("/category/list")
        }
      }
    )
  }

  def edit(Id: Int) = Action.async { implicit request =>
    for {
      optCategory <- TodoCategoryRepository.get(TodoCategory.Id(Id.toLong))
    } yield {
      optCategory match {
        case Some(Entity(v)) => {
          val vv = ViewValueCategoryEdit(
            "Category編集",
            Seq("category/edit.css"),
            Seq("main.js"),
            categoryForm.fill(CategoryData(v.name, v.slug, v.color))
          )
          Ok(views.html.category.Edit(vv, TodoCategory.Id(Id.toLong)))
        }
        case _ =>  Redirect("/category/list")
      }

    }
  }

  def update(Id: Int) = Action.async { implicit request =>
    categoryForm.bindFromRequest().fold(
      (formWithError: Form[CategoryData]) => {
        val vv = ViewValueCategoryEdit(
          "Category編集",
          Seq("category/edit.css"),
          Seq("main.js"),
          formWithError
        )
        Future.successful(BadRequest(views.html.category.Edit(vv, TodoCategory.Id(Id.toLong))))
      },
      (categoryData:  CategoryData) => {
        for {
          category <- TodoCategoryRepository.get(TodoCategory.Id(Id.toLong))
          edit     <- Future.successful {
            category match {
              case Some(category@Entity(_)) => {
                Some(category.map(_.copy(name = categoryData.name, slug = categoryData.slug, color = categoryData.color)))
              }
              case None => None
            }
          }
          update   <- edit match {
            case Some(data) =>  TodoCategoryRepository.update(data)
            case None       =>  Future.successful(None)
          }
        } yield {
          update match {
            case Some(_) => {
              Redirect("/category/list")
            }
            case _       => {
              NotFound(views.html.error.page404(model.ViewValueHome(
                "Error",
                Seq("main.css"),
                Seq("main.js")
              )))
            }
          }
        }
      }
    )
  }

  def delete(Id: Int) = Action.async { implicit request =>
    for {
      _  <-  TodoCategoryRepository.remove(TodoCategory.Id(Id.toLong))
    } yield {
      Redirect("/category/list")
    }
  }

}
