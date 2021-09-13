package model

// Topページのviewvalue
case class ViewValueList(
    title:  String,
    cssSrc: Seq[String],
    jsSrc:  Seq[String],
) extends ViewValueCommon