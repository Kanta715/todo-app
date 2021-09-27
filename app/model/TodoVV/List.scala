package model.TodoVV

import lib.model.{Todo, TodoCategory}
import model.ViewValueCommon


// TodoListページのviewvalue
case class ViewValueList(
    title:        String,
    cssSrc:       Seq[String],
    jsSrc:        Seq[String],
    todoList:     List[Todo],
    categoryList: List[TodoCategory]
) extends ViewValueCommon