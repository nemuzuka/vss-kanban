package domain.kanban

import domain.Repository

/**
 * レーンのリポジトリ.
 */
trait LaneRepository extends Repository[Lane] {

  /**
   * レーン取得.
   * @param laneId レーンID
   * @return 該当データ
   */
  def findById(laneId: LaneId): Option[Lane]

  /**
   * かんばんIDに紐づく全てのレーン取得.
   * ソート順 asc, レーンID asc でソートします
   * @param kanbanId かんばんID
   * @param includeArchive Archiveのレーンも含める場合、true
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: KanbanId, includeArchive: Boolean): Seq[Lane]

  /**
   * 永続処理.
   * @param lane レーンドメイン
   * @param kanbanId かんばんID
   * @return レーンID(永続化失敗時、None)
   */
  def store(lane: Lane, kanbanId: KanbanId): Option[LaneId]
}
