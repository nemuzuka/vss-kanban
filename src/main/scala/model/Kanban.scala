package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class Kanban(
  id: Long,
  kanbanTitle: String,
  kanbanDescription: String,
  archiveStatus: String,
  createAt: DateTime,
  lastUpdateAt: DateTime,
  lockVersion: Long
)

object Kanban extends SkinnyCRUDMapper[Kanban] {
  override lazy val tableName = "kanban"
  override lazy val defaultAlias = createAlias("k")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Kanban]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Kanban]): Kanban = new Kanban(
    id = rs.get(rn.id),
    kanbanTitle = rs.get(rn.kanbanTitle),
    kanbanDescription = rs.get(rn.kanbanDescription),
    archiveStatus = rs.get(rn.archiveStatus),
    createAt = rs.get(rn.createAt),
    lastUpdateAt = rs.get(rn.lastUpdateAt),
    lockVersion = rs.get(rn.lockVersion)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: Kanban)(implicit session: DBSession): Long = {
    Kanban.createWithAttributes(
      'kanbanTitle -> entity.kanbanTitle,
      'kanbanDescription -> entity.kanbanDescription,
      'archiveStatus -> entity.archiveStatus,
      'createAt -> entity.createAt,
      'lastUpdateAt -> entity.lastUpdateAt,
      'lockVersion -> 1L
    )
  }

}
