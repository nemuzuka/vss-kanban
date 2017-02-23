package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteChargedUser(
  id: Long,
  noteId: Long,
  loginUserInfoId: Long
)

object NoteChargedUser extends SkinnyCRUDMapper[NoteChargedUser] {
  override lazy val tableName = "note_charged_user"
  override lazy val defaultAlias = createAlias("ncu")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteChargedUser]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteChargedUser]): NoteChargedUser = new NoteChargedUser(
    id = rs.get(rn.id),
    noteId = rs.get(rn.noteId),
    loginUserInfoId = rs.get(rn.loginUserInfoId)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteChargedUser)(implicit session: DBSession): Long = {
    NoteChargedUser.createWithAttributes(
      'noteId -> entity.noteId,
      'loginUserInfoId -> entity.loginUserInfoId
    )
  }

  /**
   * ふせんIDによる削除.
   * @param noteId ふせんID
   * @param session Session
   */
  def deleteByNoteId(noteId: Long)(implicit session: DBSession): Unit = {
    NoteChargedUser.deleteBy(
      sqls.eq(NoteChargedUser.column.noteId, noteId)
    )
  }

}
