package form.kanban

import domain.kanban._
import domain.user.{ User, UserId }
import util.ConvertUtil

/**
 * ふせん登録・変更Form.
 *
 * @param id ふせんID
 * @param kanbanId かんばんID
 * @param stageId ステージID
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
    stageId: String,
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
   * @param stageId ステージID
   * @return 新規登録用Form
   */
  def createInitForm(kanbanId: KanbanId, stageId: StageId) = Note(
    id = "",
    kanbanId = kanbanId.id.toString,
    stageId = stageId.id.toString,
    lockVersion = "0",
    noteTitle = "",
    noteDescription = "",
    fixDate = "",
    chargedUserIds = Seq(),
    attachmentFileIds = Seq()
  )

  /**
   * ドメインから生成.
   * 添付ファイルIDSeqは空のSeqを設定します
   * @param kanbanId かんばんID
   * @param stageId ステージID
   * @param note ふせんID
   * @return 更新用Form
   */
  def fromDomain(kanbanId: KanbanId, stageId: StageId, note: domain.kanban.Note) = Note(
    id = note.noteId.get.id.toString,
    kanbanId = kanbanId.id.toString,
    stageId = stageId.id.toString,
    lockVersion = note.lockVersion.toString,
    noteTitle = note.title,
    noteDescription = note.description,
    archiveStatus = note.noteStatus.code,
    fixDate = note.fixDate map (_.toString("yyyyMMdd")) getOrElse "",
    chargedUserIds = note.chargedUsers map (_.userId.id),
    attachmentFileIds = Seq()
  )
}