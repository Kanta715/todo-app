package model.TodoVV

import model.ViewValueCommon
import lib.persistence.onMySQL.TodoRepository
import lib.persistence.onMySQL.TodoCategoryRepository

// TodoListページのviewvalue
case class ViewValueList(
    title:        String,
    cssSrc:       Seq[String],
    jsSrc:        Seq[String],
    todoList:     Seq[TodoRepository.EntityEmbeddedId],
    categoryList: Seq[TodoCategoryRepository.EntityEmbeddedId]
) extends ViewValueCommon
