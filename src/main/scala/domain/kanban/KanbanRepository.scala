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
   * @param session Session
   * @return 該当データ
   */
  def findById(kanbanId: KanbanId)(implicit session: DBSession): Option[Kanban]

  /**
   * 永続処理.
   * @param kanban かんばんドメイン
   * @param session Session
   * @return Right:かんばんID, Left:エラー情報
   */
  def store(kanban: Kanban)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * 検索条件に伴う検索.
   * @param param 検索条件
   * @param session Session
   * @return 検索結果
   */
  def findByParam(param: KanbanSearchParam)(implicit session: DBSession): KanbanSearchResult

  /**
   * かんばん - 添付ファイル永続処理.
   * 引数のかんばんIDに紐づく添付ファイルIDを永続化します。
   * 永続化対象外のかんばんIDと添付ファイルIDの関連は削除します
   * @param kanbanId かんばんID
   * @param attachmentFileIdSeq 添付ファイルIDSeq
   * @param session Session
   */
  def storeKanbanAttachmentFile(kanbanId: Long, attachmentFileIdSeq: Seq[Long])(implicit session: DBSession): Unit

}

/**
 * 検索条件.
 * @param loginUserId ログインユーザID
 * @param viewArchiveKanban アーカイブ済みのかんばんも取得する場合、true
 * @param viewAllKanban 登録済みのかんばんも取得する場合、true
 */
case class KanbanSearchParam(
  loginUserId: Long,
  viewArchiveKanban: Boolean,
  viewAllKanban: Boolean
)

/**
 * 検索結果.
 * @param joinedKanbans 自分が担当になっているかんばん一覧
 * @param otherKanbans 自分が担当外のかんばん一覧
 */
case class KanbanSearchResult(
  joinedKanbans: Seq[KanbanRow],
  otherKanbans: Seq[KanbanRow]
)
