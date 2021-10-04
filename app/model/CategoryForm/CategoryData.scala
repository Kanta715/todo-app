package model.CategoryForm

import lib.model.TodoCategory
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation._

//  新規登録用Formのケースクラスと、マッピングさせた変数
case class CategoryData(
  name:         String,
  slug:         String,
  color:        Int
)

object CategoryData{
  val constraint:Constraint[String] = Constraint("Alphabet or Number"){ slug =>
    val numOrAlp = """\w*""".r
    val matchResult = numOrAlp.findFirstIn(slug)
    matchResult match {
      case None    =>  Invalid(ValidationError("error.alphabetOrNumbers"))
      case Some(s) =>  if(s == slug) Valid else Invalid(ValidationError("error.alphabetOrNumbers"))
    }
  }

  val categoryForm = Form(
    mapping(
      "name"        ->  nonEmptyText,
      "slug"        ->  nonEmptyText.verifying(constraint),
      "color"       ->  number
    )(CategoryData.apply)(CategoryData.unapply)
  )
}
