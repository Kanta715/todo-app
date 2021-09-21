package model.CategoryForm

import scala.util.matching.Regex
import play.api.data.Forms._
import play.api.data._

//  新規登録用Formのケースクラスと、マッピングさせた変数
case class CategoryData(
  name:         String,
  slug:         String,
  color:        String
)

object CategoryData{
  val categoryForm = Form(
    mapping(
      "name"        ->  nonEmptyText,
      "slug"        ->  nonEmptyText,
      "color"       ->  nonEmptyText
    )(CategoryData.apply)(CategoryData.unapply)
  )
}
