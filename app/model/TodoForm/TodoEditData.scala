package model.TodoForm

import lib.model.{Todo, TodoCategory}
import play.api.data.Forms._
import play.api.data._

//  編集用Formのケースクラスと、マッピングさせた変数
case class TodoEditData(
     title:        String,
     body:         String,
     status:       Todo.Status,
     categoryId:   TodoCategory.Id
)

object TodoEditData{
  val todoEditForm = Form(
    mapping(
      "title"         ->  nonEmptyText,
      "body"          ->  text,
      "statusCode"    ->  shortNumber.transform[Todo.Status](shortNum => Todo.Status(shortNum), state => state.code),
      "categoryId"    ->  longNumber.transform[TodoCategory.Id](longNum => TodoCategory.Id(longNum),id => id.toLong)
    )(TodoEditData.apply)(TodoEditData.unapply)
  )

}