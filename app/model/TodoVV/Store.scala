package model.TodoVV

import model.TodoForm.TodoData
import model.ViewValueCommon
import play.api.data.Form

// Storeページのviewvalue
case class ViewValueStore(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  form:   Form[TodoData]
) extends ViewValueCommon