package domain.kanban

import domain.Repository

/**
 * 付箋のリポジトリ.
 */
trait NoteRepository extends Repository[Note] {

  /**
   * 付箋取得.
   * @param noteId 付箋ID
   * @return 該当データ
   */
  def findById(noteId: NoteId): Option[Note]
  /**
   * 検索条件に紐づく付箋取得.
   * @param condition 検索条件
   * @return 該当データ
   */
  def findByCondition(condition: NoteCondition): Seq[NoteRow]

  /**
   * 永続処理.
   * @param note 付箋ドメイン
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @return 付箋ID(永続化失敗時、None)
   */
  def store(note: Note, kanbanId: KanbanId, laneId: LaneId): Option[NoteId]

  /**
   * 付箋検索条件.
   * @param kanbanId かんばんID
   * @param laneIds レーンIDSeq
   * @param includeArchive Archiveの付箋も含める場合、true
   */
  case class NoteCondition(
    kanbanId: KanbanId,
    laneIds: Seq[LaneId],
    includeArchive: Boolean
  )
}
