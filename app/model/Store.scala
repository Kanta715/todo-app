package model

// Storeページのviewvalue
case class ViewValueStore(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon