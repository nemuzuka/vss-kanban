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
