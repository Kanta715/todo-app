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
       color:        Color,
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

  sealed abstract class Color(val code: Short, val name: String) extends EnumStatus
  object Color extends EnumStatus.Of[Color] {
    case object RED   extends Color(code = 1, name = "赤")
    case object BLUE  extends Color(code = 2, name = "青")
    case object GREEN extends Color(code = 3, name = "緑")
  }

  // name, slug, color のみインスタンスの生成に必要 (idは自分でNoneにしないとWithNoIdで生成できない)
  def apply(name: String, slug: String, color: Color): WithNoId = {
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