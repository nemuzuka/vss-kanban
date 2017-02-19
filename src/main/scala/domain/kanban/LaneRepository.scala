package domain.kanban

import domain.Repository
import scalikejdbc.DBSession

/**
 * レーンのリポジトリ.
 */
trait LaneRepository extends Repository[Lane] {

  /**
   * レーン取得.
   * @param laneId レーンID
   * @param session Session
   * @return 該当データ
   */
  def findById(laneId: LaneId)(implicit session: DBSession): Option[Lane]

  /**
   * かんばんIDに紐づく全てのレーン取得.
   * ソート順 asc, レーンID asc でソートします
   * @param kanbanId かんばんID
   * @param includeArchive Archiveのレーンも含める場合、true
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: KanbanId, includeArchive: Boolean)(implicit session: DBSession): Seq[LaneRow]

  /**
   * 永続処理.
   * @param lane レーンドメイン
   * @param kanbanId かんばんID
   * @param session Session
   * @return レーンID(永続化失敗時、None)
   */
  def store(lane: Lane, kanbanId: KanbanId)(implicit session: DBSession): Option[LaneId]
}
