package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteNotification(
  id: Long,
  noteId: Long,
  loginUserInfoId: Long,
  notificationBody: String,
  notificationUrl: String,
  createAt: DateTime
)

object NoteNotification extends SkinnyCRUDMapper[NoteNotification] {
  override lazy val tableName = "note_notification"
  override lazy val defaultAlias = createAlias("nn")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteNotification]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteNotification]): NoteNotification = new NoteNotification(
    id = rs.get(rn.id),
    noteId = rs.get(rn.noteId),
    loginUserInfoId = rs.get(rn.loginUserInfoId),
    notificationBody = rs.get(rn.notificationBody),
    notificationUrl = rs.get(rn.notificationUrl),
    createAt = rs.get(rn.createAt)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteNotification)(implicit session: DBSession): Long = {
    NoteNotification.createWithAttributes(
      'noteId -> entity.noteId,
      'loginUserInfoId -> entity.loginUserInfoId,
      'notificationBody -> entity.notificationBody,
      'notificationUrl -> entity.notificationUrl,
      'createAt -> entity.createAt
    )
  }

  /**
   * ふせんIDとユーザIDによる削除.
   * @param noteId ふせんID
   * @param loginUserInfoId ユーザID
   * @param session Session
   */
  def deleteByNoteIdAndLoginUserInfoId(noteId: Long, loginUserInfoId: Long)(implicit session: DBSession): Unit = {
    NoteNotification.deleteBy(
      sqls.eq(NoteNotification.column.noteId, noteId).and.eq(NoteNotification.column.loginUserInfoId, loginUserInfoId)
    )
  }

  /**
   * ふせんIDによる削除.
   * @param noteId ふせんID
   * @param session Session
   */
  def deleteByNoteId(noteId: Long)(implicit session: DBSession): Unit = {
    NoteNotification.deleteBy(
      sqls.eq(NoteNotification.column.noteId, noteId)
    )
  }

  /**
   * ユーザIDに紐づく一覧取得.
   * ソート順はidの降順です
   * @param loginUserInfoId ユーザID
   * @param session Session
   * @return 該当データ
   */
  def findByLoginUserInfoId(loginUserInfoId: Long)(implicit session: DBSession): Seq[NoteNotification] = {
    val nn = defaultAlias
    NoteNotification.where('loginUserInfoId -> loginUserInfoId).orderBy(nn.id.desc).apply()
  }

}
