package model.TodoForm

import lib.model.{Todo, TodoCategory}
import play.api.data.Forms._
import play.api.data._

//  編集用Formのケースクラスと、マッピングさせた変数
case class TodoEditData(
     title:        String,
     body:         String,
     stateName:    String,
     categoryId:   TodoCategory.Id
)

object TodoEditData{
  val todoEditForm = Form(
    mapping(
      "title"         ->  nonEmptyText,
      "body"          ->  text,
      "stateName"     ->  text,
      "categoryId"    ->  longNumber.transform(TodoCategory.Id.apply, TodoCategory.Id.unwrap)
    )(TodoEditData.apply)(TodoEditData.unapply)
  )
}