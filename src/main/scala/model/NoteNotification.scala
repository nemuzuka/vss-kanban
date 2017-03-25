package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteNotification(
  id: Long,
  noteId: Long,
  kanbanId: Long,
  loginUserInfoId: Long,
  actionLabel: String,
  createLoginUserInfoId: Long,
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
    kanbanId = rs.get(rn.kanbanId),
    loginUserInfoId = rs.get(rn.loginUserInfoId),
    actionLabel = rs.get(rn.actionLabel),
    createLoginUserInfoId = rs.get(rn.createLoginUserInfoId),
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
      'kanbanId -> entity.kanbanId,
      'loginUserInfoId -> entity.loginUserInfoId,
      'actionLabel -> entity.actionLabel,
      'createLoginUserInfoId -> entity.createLoginUserInfoId,
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
   * @return 該当データSeq _1: NoteNotification _2: Kanban _3: Note _4: ユーザ情報Option
   */
  def findByLoginUserInfoId(loginUserInfoId: Long)(implicit session: DBSession): Seq[(NoteNotification, Kanban, Note, Option[LoginUserInfo])] = {
    val (nn, k, n, lui) = (defaultAlias, Kanban.defaultAlias, Note.defaultAlias, LoginUserInfo.defaultAlias)
    withSQL {
      select.from(NoteNotification as nn).innerJoin(Kanban as k).on(nn.kanbanId, k.id)
        .innerJoin(Note as n).on(nn.noteId, n.id)
        .leftJoin(LoginUserInfo as lui).on(nn.createLoginUserInfoId, lui.id)
        .where.eq(nn.loginUserInfoId, loginUserInfoId).orderBy(nn.id.desc)
    }.map { rs =>
      val logunUserInfo = rs.longOpt(lui.resultName.id) map (_ => Option(LoginUserInfo.extract(rs, lui.resultName))) getOrElse None
      (NoteNotification.extract(rs, nn.resultName), Kanban.extract(rs, k.resultName), Note.extract(rs, n.resultName), logunUserInfo)
    }.list.apply()
  }

  /**
   * ユーザIDに紐づくデータが1件以上存在するか？.
   * @param loginUserInfoId ユーザID
   * @param session Session
   * @return 1件以上存在する場合、true
   */
  def hasUnreadNotification(loginUserInfoId: Long)(implicit session: DBSession): Boolean = {
    val list = NoteNotification.where('loginUserInfoId -> loginUserInfoId).limit(1).apply()
    if (list.length == 1) true else false
  }

  /**
   * かんばん参加者に含まれていないかんばんに紐づくふせんの通知情報削除.
   * @param loginUserInfoId ユーザID
   * @param session Session
   */
  def deleteByNotExistsJoinedUser(loginUserInfoId: Long)(implicit session: DBSession): Unit = {
    val (k, kju) = (Kanban.defaultAlias, KanbanJoinedUser.defaultAlias)
    withSQL {
      delete.from(NoteNotification).where.eq(NoteNotification.column.loginUserInfoId, loginUserInfoId)
        .and.notIn(
          NoteNotification.column.kanbanId,
          select(k.result.id).from(Kanban as k).innerJoin(KanbanJoinedUser as kju).on(k.id, kju.kanbanId).where.eq(kju.loginUserInfoId, loginUserInfoId)
        )
    }.update.apply()
  }
}
