package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteWatchUser(
  id: Long,
  noteId: Long,
  loginUserInfoId: Long
)

object NoteWatchUser extends SkinnyCRUDMapper[NoteWatchUser] {
  override lazy val tableName = "note_watch_user"
  override lazy val defaultAlias = createAlias("nwu")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteWatchUser]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteWatchUser]): NoteWatchUser = new NoteWatchUser(
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
  def create(entity: NoteWatchUser)(implicit session: DBSession): Long = {
    NoteWatchUser.createWithAttributes(
      'noteId -> entity.noteId,
      'loginUserInfoId -> entity.loginUserInfoId
    )
  }

  /**
   * ふせんIDとユーザIDによる削除.
   * @param noteId ふせんID
   * @param loginUserInfoId ユーザID
   * @param session Session
   */
  def deleteByNoteIdAndLoginUserInfoId(noteId: Long, loginUserInfoId: Long)(implicit session: DBSession): Unit = {
    NoteWatchUser.deleteBy(
      sqls.eq(NoteWatchUser.column.noteId, noteId).and.eq(NoteWatchUser.column.loginUserInfoId, loginUserInfoId)
    )
  }

  /**
   * ふせんIDによる取得.
   * @param noteId ふせんID
   * @param session Session
   * @return 該当データ
   */
  def findByNoteIdAndLoginUserInfoId(noteId: Long)(implicit session: DBSession): Seq[NoteWatchUser] = {
    NoteWatchUser.where(
      sqls.eq(NoteWatchUser.column.noteId, noteId)
    ).apply()
  }

  /**
   * ウォッチ対象か？
   * @param noteId ふせんID
   * @param loginUserInfoId ユーザID
   * @param session Session
   * @return 指定したふせんを指定したユーザIDがウォッチ対象の場合、true
   */
  def isWatch(noteId: Long, loginUserInfoId: Long)(implicit session: DBSession): Boolean = {
    val list = NoteWatchUser.where(
      sqls.eq(NoteWatchUser.column.noteId, noteId).and.eq(NoteWatchUser.column.loginUserInfoId, loginUserInfoId)
    ).apply()
    if (list.isEmpty) false else true
  }

}
