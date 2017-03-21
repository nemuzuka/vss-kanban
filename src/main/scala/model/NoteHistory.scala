package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteHistory(
  id: Long,
  noteId: Long,
  stageId: Long,
  lastUpdateLoginUserInfoId: Long,
  lastUpdateAt: DateTime
)

object NoteHistory extends SkinnyCRUDMapper[NoteHistory] {
  override lazy val tableName = "note_history"
  override lazy val defaultAlias = createAlias("nh")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteHistory]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteHistory]): NoteHistory = new NoteHistory(
    id = rs.get(rn.id),
    noteId = rs.get(rn.noteId),
    stageId = rs.get(rn.stageId),
    lastUpdateLoginUserInfoId = rs.get(rn.lastUpdateLoginUserInfoId),
    lastUpdateAt = rs.get(rn.lastUpdateAt)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteHistory)(implicit session: DBSession): Long = {
    NoteHistory.createWithAttributes(
      'noteId -> entity.noteId,
      'stageId -> entity.stageId,
      'lastUpdateLoginUserInfoId -> entity.lastUpdateLoginUserInfoId,
      'lastUpdateAt -> entity.lastUpdateAt
    )
  }

}
