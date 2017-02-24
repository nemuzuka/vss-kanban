package form.kanban

import domain.kanban._
import domain.user.{ User, UserId }
import util.ConvertUtil

/**
 * ふせん登録・変更Form.
 *
 * @param id ふせんID
 * @param kanbanId かんばんID
 * @param laneId レーンID
 * @param lockVersion バージョンNo
 * @param noteTitle ふせんタイトル
 * @param noteDescription ふせん説明
 * @param archiveStatus アーカイブステータス
 * @param fixDate 期限
 * @param chargedUserIds ふせん担当者IDSeq
 * @param attachmentFileIds 添付ファイルIDSeq
 */
case class Note(
    id: String,
    kanbanId: String,
    laneId: String,
    lockVersion: String,
    noteTitle: String,
    noteDescription: String,
    archiveStatus: String = "0",
    fixDate: String,
    chargedUserIds: Seq[Long],
    attachmentFileIds: Seq[Long]
) {
  /**
   * ふせんドメイン生成.
   * @param loginUser ログインユーザ情報
   * @return ふせんドメイン
   */
  def toDomain(loginUser: User): domain.kanban.Note = {
    domain.kanban.Note(
      noteId = if (id.isEmpty) None else Option(NoteId(id.toLong)),
      sortNum = Long.MaxValue,
      noteStatus = NoteStatus.withCode(archiveStatus).get,
      title = noteTitle,
      description = noteDescription,
      fixDate = ConvertUtil.toDate(fixDate),
      createUserId = loginUser.userId.get,
      lockVersion = lockVersion.toLong,
      chargedUsers = chargedUserIds map (v =>
        ChargedUser(
          userId = UserId(v),
          name = ""
        ))
    )
  }
}

/**
 * ふせんFormObject.
 */
object Note {
  /**
   * 新規登録用Form作成.
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @return 新規登録用Form
   */
  def createInitForm(kanbanId: KanbanId, laneId: LaneId) = Note(
    id = "",
    kanbanId = kanbanId.id.toString,
    laneId = laneId.id.toString,
    lockVersion = "0",
    noteTitle = "",
    noteDescription = "",
    fixDate = "",
    chargedUserIds = Seq(),
    attachmentFileIds = Seq()
  )

  /**
   * ドメインから生成.
   * ふせん担当者IDSeqと添付ファイルIDSeqは空のSeqを設定します
   * @param kanbanId かんばんID
   * @param laneId レーンID
   * @param note ふせんID
   * @return 更新用Form
   */
  def fromDomain(kanbanId: KanbanId, laneId: LaneId, note: domain.kanban.Note) = Note(
    id = note.noteId.get.id.toString,
    kanbanId = kanbanId.id.toString,
    laneId = laneId.id.toString,
    lockVersion = note.lockVersion.toString,
    noteTitle = note.title,
    noteDescription = note.description,
    fixDate = note.fixDate map (_.toString("yyyyMMdd")) getOrElse "",
    chargedUserIds = Seq(),
    attachmentFileIds = Seq()
  )
}