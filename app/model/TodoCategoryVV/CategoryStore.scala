package model.TodoCategoryVV

import model.CategoryForm.CategoryData
import model.ViewValueCommon
import play.api.data.Form


// TodoCategoryStoreページのviewvalue
case class ViewValueCategoryStore(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String],
  form:       Form[CategoryData]
) extends ViewValueCommon