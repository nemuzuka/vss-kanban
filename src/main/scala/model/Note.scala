package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class Note(
  id: Long,
  laneId: Long,
  kanbanId: Long,
  noteTitle: String,
  noteDescription: String,
  fixDate: Option[LocalDate] = None,
  sortNum: Long,
  createLoginUserInfoId: Long,
  createAt: DateTime,
  lastUpdateLoginUserInfoId: Long,
  lastUpdateAt: DateTime,
  lockVersion: Long
)

object Note extends SkinnyCRUDMapper[Note] {
  override lazy val tableName = "note"
  override lazy val defaultAlias = createAlias("n")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Note]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Note]): Note = new Note(
    id = rs.get(rn.id),
    laneId = rs.get(rn.laneId),
    kanbanId = rs.get(rn.kanbanId),
    noteTitle = rs.get(rn.noteTitle),
    noteDescription = rs.get(rn.noteDescription),
    fixDate = rs.get(rn.fixDate),
    sortNum = rs.get(rn.sortNum),
    createLoginUserInfoId = rs.get(rn.createLoginUserInfoId),
    createAt = rs.get(rn.createAt),
    lastUpdateLoginUserInfoId = rs.get(rn.lastUpdateLoginUserInfoId),
    lastUpdateAt = rs.get(rn.lastUpdateAt),
    lockVersion = rs.get(rn.lockVersion)
  )
}
