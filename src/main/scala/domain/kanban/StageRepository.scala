package domain.kanban

import domain.Repository
import scalikejdbc.DBSession

/**
 * ステージのリポジトリ.
 */
trait StageRepository extends Repository[Stage] {

  /**
   * ステージ取得.
   * @param stageId ステージID
   * @param session Session
   * @return 該当データ
   */
  def findById(stageId: StageId)(implicit session: DBSession): Option[Stage]

  /**
   * かんばんIDに紐づく全てのステージ取得.
   * ソート順 asc, ステージID asc でソートします
   * @param kanbanId かんばんID
   * @param includeArchive Archiveのステージも含める場合、true
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: KanbanId, includeArchive: Boolean)(implicit session: DBSession): Seq[StageRow]

  /**
   * 永続処理.
   * ステージIDが未設定のものはinsert/存在するものはupdateします。
   * また、既にかんばんIDに紐づく形で登録されているステージの中で今回の更新に含まれていないものは削除します。
   * @param stages ステージドメインSeq
   * @param kanbanId かんばんID
   * @param session Session
   */
  def store(stages: Seq[Stage], kanbanId: KanbanId)(implicit session: DBSession): Unit

}
