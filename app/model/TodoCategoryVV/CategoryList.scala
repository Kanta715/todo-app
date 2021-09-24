package model.TodoCategoryVV

import model.ViewValueCommon


// TodoCategoryListページのviewvalue
case class ViewValueCategoryList(
  title:      String,
  cssSrc:     Seq[String],
  jsSrc:      Seq[String]
) extends ViewValueCommon