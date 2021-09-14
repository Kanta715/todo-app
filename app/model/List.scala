package model

// UserListページのviewvalue
case class ViewValueList(
    title:  String,
    cssSrc: Seq[String],
    jsSrc:  Seq[String],
) extends ViewValueCommon