package model.TodoVV

import model.ViewValueCommon


// UserListページのviewvalue
case class ViewValueList(
    title:      String,
    cssSrc:     Seq[String],
    jsSrc:      Seq[String]
) extends ViewValueCommon