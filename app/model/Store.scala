package model

import play.api.data.Form

// Storeページのviewvalue
case class ViewValueStore(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
  form:   Form[TodoData]
) extends ViewValueCommon