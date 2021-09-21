package model.TodoVV

import model.TodoForm.TodoEditData
import model.ViewValueCommon
import play.api.data.Form

// Storeページのviewvalue
case class ViewValueEdit(
   title:  String,
   cssSrc: Seq[String],
   jsSrc:  Seq[String],
   form:   Form[TodoEditData]
) extends ViewValueCommon