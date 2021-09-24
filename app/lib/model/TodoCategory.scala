package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// カテゴリを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import TodoCategory._
case class TodoCategory(
       id:           Option[Id],
       name:         String,
       slug:         String,
       color:        Short,
       updatedAt:    LocalDateTime = NOW,
       createdAt:    LocalDateTime = NOW
       ) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object TodoCategory {
  //  TodoCategoryの型を引き継がせないと生成やマッピング等ができないため、型を代入する
  val  Id = the[Identity[Id]]
  type Id = Long @@ TodoCategory
  type WithNoId = Entity.WithNoId [Id, TodoCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, TodoCategory]

  // name, slug, color のみインスタンスの生成に必要 (idは自分でNoneにしないとWithNoIdで生成できない)
  def apply(name: String, slug: String, color: Short): WithNoId = {
    new Entity.WithNoId(
      new TodoCategory(
        id            = None,
        name          = name,
        slug          = slug,
        color         = color
      )
    )
  }
}