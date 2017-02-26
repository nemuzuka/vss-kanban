package domain.kanban

import domain.attachment.AttachmentFileRow
import domain.user.User
import domain.{ ApplicationException, Repository }
import scalikejdbc.DBSession

/**
 * 付箋のリポジトリ.
 */
trait NoteRepository extends Repository[Note] {

  /**
   * 付箋取得.
   * @param noteId 付箋ID
   * @param session Session
   * @return 該当データ
   */
  def findById(noteId: NoteId)(implicit session: DBSession): Option[Note]
  /**
   * 検索条件に紐づく付箋取得.
   * @param condition 検索条件
   * @param session Session
   * @return 該当データ
   */
  def findByCondition(condition: NoteCondition)(implicit session: DBSession): Seq[NoteRow]

  /**
   * 永続処理.
   * @param note ふせんドメイン
   * @param attachmentFileIds ふせんに紐付ける添付ファイルIDSeq
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return Right:ふせんID, Left:エラー情報
   */
  def store(note: Note, attachmentFileIds: Seq[Long],
    kanbanId: KanbanId, laneId: LaneId, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * ふせんに紐づく添付ファイル一覧取得.
   * @param noteId ふせんID
   * @param session Session
   * @return 該当データ
   */
  def findByNoteId(noteId: NoteId)(implicit session: DBSession): Seq[AttachmentFileRow]

  /**
   * ふせんコメント永続化.
   * @param laneId レーンID
   * @param noteId ふせんID
   * @param comment コメント文
   * @param attachmentFileIds ふせんコメントに紐付ける添付ファイルIDSeq
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return ふせんコメントID(コメントを登録していない場合、None)
   */
  def store(laneId: LaneId, noteId: NoteId, comment: String, attachmentFileIds: Seq[Long], loginUser: User)(implicit session: DBSession): Option[Long]

  /**
   * ふせんコメント一覧取得.
   * @param noteId ふせんID
   * @param session Session
   * @return 該当データ
   */
  def findCommentsByNoteId(noteId: NoteId)(implicit session: DBSession): Seq[NoteCommentRow]

  /**
   * ふせん削除.
   * @param noteId ふせんID
   * @param lockVersion ふせんのバージョンNo
   * @param session Session
   * @return Right:ふせんID, Left:エラー情報
   */
  def deleteById(noteId: NoteId, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long]

}

/**
 * 付箋検索条件.
 * @param kanbanId かんばんID
 * @param includeArchive Archiveの付箋も含める場合、true
 */
case class NoteCondition(
  kanbanId: KanbanId,
  includeArchive: Boolean
)
