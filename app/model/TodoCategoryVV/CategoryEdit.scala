package model.TodoCategoryVV

import model.CategoryForm.CategoryData
import model.ViewValueCommon
import play.api.data.Form


// TodoCategoryEditページのviewvalue
case class ViewValueCategoryEdit(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  form:       Form[CategoryData]
) extends ViewValueCommon