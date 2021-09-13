package lib.model

import ixias.model._
import ixias.util.EnumStatus

import java.time.LocalDateTime

// ユーザーを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import UserCategory._
case class UserCategory(
       id:           Option[Id],
       name:         String,
       slug:         String,
       color:        Short,
       updatedAt:    LocalDateTime = NOW,
       createdAt:    LocalDateTime = NOW
       ) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object UserCategory {

  val  Id = the[Identity[Id]]
  type Id = Long @@ UserCategory
  type WithNoId = Entity.WithNoId [Id, UserCategory]
  type EmbeddedId = Entity.EmbeddedId[Id, UserCategory]

  // INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
  def apply(name: String, slug: String, color: Short): WithNoId = {
    new Entity.WithNoId(
      new UserCategory(
        id            = None,
        name          = name,
        slug          = slug,
        color         = color
      )
    )
  }
}