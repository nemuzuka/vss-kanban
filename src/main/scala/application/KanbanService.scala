package application

import domain.kanban.KanbanSearchResult
import domain.user.User
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

  /**
   * かんばん検索.
   * 引数の情報を元に、かんばんを検索します。
   * ただし、登録済みのかんばんも取得する条件が有効になる場合は、
   * ログインユーザがアプリケーション管理者の場合のみです
   * @param viewArchiveKanban アーカイブ済みのかんばんも取得する場合、true
   * @param viewAllKanban 登録済みのかんばんも取得する場合、true
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 検索結果
   */
  def findByParam(
    viewArchiveKanban: Boolean,
    viewAllKanban: Boolean, loginUser: User
  )(implicit session: DBSession): KanbanSearchResult
}
