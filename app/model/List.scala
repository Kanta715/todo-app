package model

import lib.model.Todo


// UserListページのviewvalue
case class ViewValueList(
    title:      String,
    cssSrc:     Seq[String],
    jsSrc:      Seq[String]
) extends ViewValueCommon