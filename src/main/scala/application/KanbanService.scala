package application

import scalikejdbc.DBSession

/**
 * かんばんに関するApplicationSerivce.
 */
trait KanbanService {

  /**
   * かんばん登録.
   * ・「Open」状態
   * ・登録ユーザを管理者
   * ・デフォルトレーン作成(未実施/実施中/完了)
   * のかんばんを登録します
   * @param kanbanTitle かんばんタイトル
   * @param kanbanDescription かんばん説明
   * @param loginUserId 登録ユーザID
   * @param session Session
   * @return 登録かんばんID
   */
  def create(kanbanTitle: String, kanbanDescription: String, loginUserId: Long)(implicit session: DBSession): Long

}
