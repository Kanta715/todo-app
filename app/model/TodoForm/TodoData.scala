package model.TodoForm

import model.TodoForm
import play.api.data.Forms._
import play.api.data._

//  新規登録用Formのケースクラスと、マッピングさせた変数
case class TodoData(
     title:        String,
     body:         String,
     categoryName: String
)

object TodoData{
  val todoForm = Form(
    mapping(
      "title"         ->  nonEmptyText,
      "body"          ->  text,
      "categoryName"  ->  text
    )(TodoForm.TodoData.apply)(TodoForm.TodoData.unapply)
  )
}