package domain.kanban

import domain.ValueObject

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
 * レーン表示用ValueObject.
 * @param laneId レーンID
 * @param laneName レーン名
 * @param archiveStatus アーカイブステータス(0:Open 1:Archive)
 * @param completeLane 完了扱いのレーンの場合、true
 */
case class LaneRow(
    laneId: String,
    laneName: String,
    archiveStatus: String,
    completeLane: Boolean
) extends ValueObject[LaneRow] {
  override def sameValueAs(other: LaneRow): Boolean = this.laneId == other.laneId
}

/**
 * 付箋表示用ValueObject.
 * @param laneId レーンID
 * @param noteId 付箋ID
 * @param noteTitle 付箋タイトル
 * @param noteDescription 付箋説明
 * @param archiveStatus アーカイブステータス(0:Open 1:Archive)
 * @param fixDate 期日(yyyyMMdd形式)
 * @param hasAttachmentFile 添付ファイルが存在する場合、true
 * @param chargedUsers 担当者ID情報
 */
case class NoteRow(
    laneId: Long,
    noteId: Long,
    noteTitle: String,
    noteDescription: String,
    archiveStatus: String,
    fixDate: String,
    hasAttachmentFile: Boolean,
    chargedUsers: Seq[String]
) extends ValueObject[NoteRow] {
  override def sameValueAs(other: NoteRow): Boolean = this.noteId == other.noteId
}