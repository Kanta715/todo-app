package lib.persistence

import scala.concurrent.Future
import ixias.persistence.SlickRepository
import lib.model
import lib.model.UserCategory
import slick.jdbc.JdbcProfile

// UserRepository: UserTableへのクエリ発行を行うRepository層の定義
//~~~~~~~~~~~~~~~~~~~~~~
case class UserCategoryRepository[P <: JdbcProfile]()(implicit val driver: P)
  extends SlickRepository[model.UserCategory.Id, UserCategory, P]
    with db.SlickResourceProvider[P] {

  import api._

  /**
   * Get User Data
   */
  def get(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserCategoryTable, "slave") { _
      .filter(_.id === id)
      .result.headOption
    }

  def getAll(): Future[List[UserCategory]] =
    RunDBAction(UserCategoryTable, "slave") { _
      .to[List].result
    }

  /**
   * Add User Data
   */
  def add(entity: EntityWithNoId): Future[Id] =
    RunDBAction(UserCategoryTable) { slick =>
      slick returning slick.map(_.id) += entity.v
    }

  /**
   * Update User Data
   */
  def update(entity: EntityEmbeddedId): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserCategoryTable) { slick =>
      val row = slick.filter(_.id === entity.id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.update(entity.v)
        }
      } yield old
    }

  /**
   * Delete User Data
   */
  def remove(id: Id): Future[Option[EntityEmbeddedId]] =
    RunDBAction(UserCategoryTable) { slick =>
      val row = slick.filter(_.id === id)
      for {
        old <- row.result.headOption
        _   <- old match {
          case None    => DBIO.successful(0)
          case Some(_) => row.delete
        }
      } yield old
    }
}