package model.TodoForm

import play.api.data.Forms._
import play.api.data._

//  編集用Formのケースクラスと、マッピングさせた変数
case class TodoEditData(
     title:        String,
     body:         String,
     stateName:    String,
     categoryName: String
)

object TodoEditData{
  val todoEditForm = Form(
    mapping(
      "title"         ->  nonEmptyText,
      "body"          ->  text,
      "stateName"     ->  text,
      "categoryName"  ->  text
    )(TodoEditData.apply)(TodoEditData.unapply)
  )
}