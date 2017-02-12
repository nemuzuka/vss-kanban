package domain.kanban

import domain.{ ApplicationException, Repository }
import scalikejdbc.DBSession

/**
 * かんばんのリポジトリ.
 */
trait KanbanRepository extends Repository[Kanban] {

  /**
   * かんばん取得.
   * @param kanbanId かんばんID
   * @return 該当データ
   */
  def findById(kanbanId: KanbanId)(implicit session: DBSession): Option[Kanban]

  /**
   * 永続処理.
   * @param kanban かんばんドメイン
   * @return Right:かんばんID, Left:エラー情報
   */
  def store(kanban: Kanban)(implicit session: DBSession): Either[ApplicationException, Long]
}
