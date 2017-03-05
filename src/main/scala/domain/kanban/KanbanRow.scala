package domain.kanban

import domain.ValueObject
import domain.attachment.AttachmentFileRow

/**
 * かんばん一覧表示用ValueObject.
 * @param id かんばんID
 * @param title かんばんタイトル
 * @param description かんばん説明
 * @param archiveStatus アーカイブステータス
 * @param lockVersion バージョンNo
 * @param authority かんばんの権限(1:管理者、0:一般)
 */
case class KanbanRow(
    id: Long,
    title: String,
    description: String,
    archiveStatus: String,
    lockVersion: Long,
    authority: String
) extends ValueObject[KanbanRow] {
  override def sameValueAs(other: KanbanRow): Boolean = this.id == other.id
}

/**
 * ステージ表示用ValueObject.
 * @param stageId ステージID
 * @param stageName ステージ名
 * @param archiveStatus アーカイブステータス(0:Open 1:Archive)
 * @param completeStage 完了扱いのステージの場合、true
 */
case class StageRow(
    stageId: String,
    stageName: String,
    archiveStatus: String,
    completeStage: Boolean
) extends ValueObject[StageRow] {
  override def sameValueAs(other: StageRow): Boolean = this.stageId == other.stageId
}

/**
 * 付箋表示用ValueObject.
 * @param stageId ステージID
 * @param noteId 付箋ID
 * @param noteTitle 付箋タイトル
 * @param noteDescription 付箋説明
 * @param archiveStatus アーカイブステータス(0:Open 1:Archive)
 * @param fixDate 期日(yyyyMMdd形式)
 * @param hasAttachmentFile 添付ファイルが存在する場合、true
 * @param chargedUsers 担当者ID情報
 * @param lastCommentAt 最終コメント時刻
 */
case class NoteRow(
    stageId: Long,
    noteId: Long,
    noteTitle: String,
    noteDescription: String,
    archiveStatus: String,
    fixDate: String,
    hasAttachmentFile: Boolean,
    chargedUsers: Seq[String],
    lastCommentAt: String
) extends ValueObject[NoteRow] {
  override def sameValueAs(other: NoteRow): Boolean = this.noteId == other.noteId
}

/**
 * ふせんコメント表示用ValueObject
 * @param noteCommentId ふせんコメントID
 * @param noteId ふせんID
 * @param comment コメント
 * @param createUserName 登録者
 * @param createUserId 登録ユーザID
 * @param createAt 登録日時
 * @param attachmentFiles ふせんコメント添付ファイルSeq
 */
case class NoteCommentRow(
    noteCommentId: Long,
    noteId: Long,
    comment: String,
    createUserName: String,
    createUserId: Long,
    createAt: String,
    attachmentFiles: Seq[AttachmentFileRow]
) extends ValueObject[NoteCommentRow] {
  override def sameValueAs(other: NoteCommentRow): Boolean = this.noteCommentId == other.noteCommentId
}