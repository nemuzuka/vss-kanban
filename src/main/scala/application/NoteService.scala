package application

import domain.ApplicationException
import domain.attachment.AttachmentFileRow
import domain.kanban.{ KanbanId, LaneId, NoteCommentRow, NoteId }
import domain.user.User
import form.kanban.Note
import scalikejdbc.DBSession

/**
 * ふせんに関するApplicationService.
 */
trait NoteService {

  /**
   * ふせん登録・変更取得.
   * ふせん変更時は対象ふせんの担当者でなければ無条件にNoneを返します
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @param noteId ふせんID(新規の場合、None)
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return ふせん登録・変更
   */
  def getForm(kanbanId: KanbanId, laneId: LaneId, noteId: Option[NoteId], loginUser: User)(implicit session: DBSession): Option[NoteEditDetail]

  /**
   * ふせん永続化.
   * [前提条件]
   * かんばんのメンバー or アプリケーション管理者であること
   *
   * [新規登録時]
   * 個別チェックなし
   *
   * [変更時]
   * 対象ふせんの担当者であること
   *
   * 変更時、
   * ・ふせん担当者
   * ・ふせん添付ファイル
   * の情報はdelete&Insertします
   * @param form ふせん登録・変更Form
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return Right:ふせんID, Left:エラー情報
   */
  def storeNote(form: Note, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * ふせん詳細情報取得.
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @param noteId ふせんID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return ふせん詳細情報
   */
  def getDetail(kanbanId: KanbanId, laneId: LaneId, noteId: NoteId, loginUser: User)(implicit session: DBSession): Option[NoteDetail]
}

/**
 * ふせん登録・変更情報.
 * @param form ふせん登録・変更Form
 * @param joinedUsers かんばん参加ユーザSeq
 * @param noteAttachmentFiles ふせん添付ファイルSeq
 */
case class NoteEditDetail(
  form: Note,
  joinedUsers: Seq[JoinedUserDto],
  noteAttachmentFiles: Seq[AttachmentFileRow]
)

/**
 * ふせん詳細情報.
 * @param form ふせん登録・変更Form
 * @param chargedUserNames ふせん担当者名Seq
 * @param noteAttachmentFiles ふせん添付ファイルSeq
 * @param isCharged ふせん担当者か？
 * @param comments ふせんコメントSeq
 */
case class NoteDetail(
  form: Note,
  chargedUserNames: Seq[String],
  noteAttachmentFiles: Seq[AttachmentFileRow],
  isCharged: Boolean,
  comments: Seq[NoteCommentRow]
)