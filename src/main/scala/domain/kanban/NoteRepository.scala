package domain.kanban

import domain.attachment.{ AttachmentFileId, AttachmentFileRow }
import domain.user.{ User, UserId }
import domain.{ ApplicationException, Enum, EnumEntry, EnumLabelEntry, Repository }
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
   * ふせんが所属するステージID取得.
   * @param noteId ふせんID
   * @param session Session
   * @return 紐づくステージID
   */
  def findStageIdByNoteId(noteId: NoteId)(implicit session: DBSession): Option[StageId]

  /**
   * 検索条件に紐づく付箋取得.
   * @param condition 検索条件
   * @param session Session
   * @return 該当データ
   */
  def findByCondition(condition: NoteCondition)(implicit session: DBSession): Seq[NoteRow]

  /**
   * 永続処理.
   * 通知データも合わせて作成します。
   * @param note ふせんドメイン
   * @param attachmentFileIds ふせんに紐付ける添付ファイルIDSeq
   * @param kanbanId かんばんID
   * @param stageId ステージID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return Right:ふせんID, Left:エラー情報
   */
  def store(note: Note, attachmentFileIds: Seq[AttachmentFileId],
    kanbanId: KanbanId, stageId: StageId, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * ふせんに紐づく添付ファイル一覧取得.
   * @param noteId ふせんID
   * @param session Session
   * @return 該当データ
   */
  def findByNoteId(noteId: NoteId)(implicit session: DBSession): Seq[AttachmentFileRow]

  /**
   * ふせんコメント永続化.
   * ステージ移動する場合、履歴に登録します
   * 通知データも合わせて作成します。
   * @param stageId ステージID
   * @param noteId ふせんID
   * @param comment コメント文
   * @param attachmentFileIds ふせんコメントに紐付ける添付ファイルIDSeq
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return ふせんコメントID(コメントを登録していない場合、None)
   */
  def store(stageId: StageId, noteId: NoteId, comment: String, attachmentFileIds: Seq[AttachmentFileId], loginUser: User)(implicit session: DBSession): Option[Long]

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

  /**
   * ステージ移動.
   * ステージ移動する場合、履歴に登録します
   * 通知データも合わせて作成します。
   * @param noteId ふせんID
   * @param stageId 移動先ステージID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 移動した場合、移動先のステージID。移動前後が同じステージの場合、None
   */
  def moveStage(noteId: NoteId, stageId: StageId, loginUser: User)(implicit session: DBSession): Option[StageId]

  /**
   * ソート順変更.
   * @param noteIds ふせんID
   * @param session Session
   */
  def updateSortNum(noteIds: Seq[NoteId])(implicit session: DBSession): Unit

  /**
   * ふせんウォッチ.
   * @param noteId ふせんID
   * @param loginUser ログインユーザ情報
   * @param session Session
   */
  def watch(noteId: NoteId, loginUser: User)(implicit session: DBSession): Unit

  /**
   * ふせんウォッチ解除.
   * @param noteId ふせんID
   * @param loginUser ログインユーザ情報
   * @param session Session
   */
  def unWatch(noteId: NoteId, loginUser: User)(implicit session: DBSession): Unit

  /**
   * ふせん通知削除.
   * @param noteId ふせんID
   * @param userId ユーザID
   * @param session Session
   */
  def deleteNotification(noteId: NoteId, userId: UserId)(implicit session: DBSession): Unit

  /**
   * 未確認のふせん通知が存在するか？
   * @param userId ユーザID
   * @param session Session
   * @return ふせん通知が存在する場合、true
   */
  def hasUnreadNotification(userId: UserId)(implicit session: DBSession): Boolean

  /**
   * 未確認のふせん通知取得.
   * 1. 自分がアプリケーション管理者でない場合、自分が参照できないかんばんに紐づくふせんの通知を削除
   * 2. 自分宛の通知を全件取得
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 該当データ
   */
  def findByAllUnreadNotification(loginUser: User)(implicit session: DBSession): Seq[NoteNotificationRow]
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

//ふせん通知に対する区分
sealed abstract class NoteNotificationType(override val code: String, override val label: String) extends EnumLabelEntry
object NoteNotificationType extends Enum[NoteNotificationType] {
  /** ふせん登録. */
  case object CreateNote extends NoteNotificationType("add", "ふせん登録")
  /** ふせん変更. */
  case object UpdateNote extends NoteNotificationType("update", "ふせん変更")
  /** ステージ移動. */
  case object MoveNote extends NoteNotificationType("move", "ステージ移動")
  /** コメント登録. */
  case object CreateNoteComment extends NoteNotificationType("addComment", "コメント登録")
  protected val values = Seq(CreateNote, UpdateNote, MoveNote, CreateNoteComment)
}
