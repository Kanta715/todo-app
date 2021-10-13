package model.TodoForm

import lib.model.TodoCategory
import model.TodoForm
import play.api.data.Forms._
import play.api.data._

//  新規登録用Formのケースクラスと、マッピングさせた変数
case class TodoData(
     title:        String,
     body:         String,
     categoryId:   TodoCategory.Id
)

object TodoData{
  val todoForm = Form(
    mapping(
      "title"         ->  nonEmptyText,
      "body"          ->  text,
      "categoryId"    ->  longNumber.transform(TodoCategory.Id.apply, TodoCategory.Id.unwrap)
    )(TodoForm.TodoData.apply)(TodoForm.TodoData.unapply)
  )
}
