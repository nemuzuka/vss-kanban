package application

import domain.attachment.AttachmentFileRow
import domain.kanban.{ KanbanRow, KanbanSearchResult, LaneRow, NoteRow }
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
   * ただし、参加していないかんばんも取得する条件が有効になる場合は、
   * ログインユーザがアプリケーション管理者の場合のみです
   * @param viewArchiveKanban アーカイブ済みのかんばんも取得する場合、true
   * @param viewAllKanban 参加していないかんばんも取得する場合、true
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 検索結果
   */
  def findByParam(
    viewArchiveKanban: Boolean,
    viewAllKanban: Boolean, loginUser: User
  )(implicit session: DBSession): KanbanSearchResult

  /**
   * かんばん詳細情報取得.
   * かんばんが存在しても、ログインユーザが参照可能でない場合、Noneを返します
   * @param kanbanId かんばんID
   * @param includeArchive アーカイブ済みのレーンや付箋を含める場合、true
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 該当データ(存在しない場合、None)
   */
  def findById(kanbanId: Long, includeArchive: Boolean, loginUser: User)(implicit session: DBSession): Option[KanbanDetail]

}

/**
 * かんばん詳細.
 * @param kanban かんばんドメイン
 * @param lanes レーンDtoSeq
 * @param noteMap 付箋DtoMap(key:レーンID value:付箋DtoSeq)
 * @param kanbanAttachmentFiles かんばん添付ファイルSeq
 */
case class KanbanDetail(
  kanban: KanbanRow,
  lanes: Seq[LaneRow],
  noteMap: Map[String, Seq[NoteRow]],
  kanbanAttachmentFiles: Seq[AttachmentFileRow]
)
