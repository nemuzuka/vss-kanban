package application

import domain.ApplicationException
import domain.attachment.AttachmentFileRow
import domain.kanban.{ KanbanId, LaneId, NoteId }
import domain.user.User
import form.kanban.Note
import scalikejdbc.DBSession

/**
 * ふせんに関するApplicationService.
 */
trait NoteService {

  /**
   * ふせん登録・変更Form取得.
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @param noteId ふせんID(新規の場合、None)
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return ふせん詳細情報
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

}

/**
 * ふせん詳細情報.
 * @param form ふせん登録・変更Form
 * @param joinedUsers かんばん参加ユーザSeq
 * @param noteAttachmentFiles ふせん添付ファイルSeq
 */
case class NoteEditDetail(
  form: Note,
  joinedUsers: Seq[JoinedUserDto],
  noteAttachmentFiles: Seq[AttachmentFileRow]
)