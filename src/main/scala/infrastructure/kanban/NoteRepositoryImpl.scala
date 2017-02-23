package infrastructure.kanban

import domain.ApplicationException
import domain.kanban._
import domain.user.User
import model.{ NoteAttachmentFile, NoteChargedUser }
import scalikejdbc.DBSession
import util.CurrentDateUtil

/**
 * NoteRepositoryの実装クラス.
 */
class NoteRepositoryImpl extends NoteRepository {
  /**
   * @inheritdoc
   */
  override def findById(noteId: NoteId)(implicit session: DBSession): Option[Note] = ???

  /**
   * @inheritdoc
   */
  override def findByCondition(condition: NoteCondition)(implicit session: DBSession): Seq[NoteRow] = ???

  /**
   * @inheritdoc
   */
  override def store(note: Note, attachmentFileIds: Seq[Long],
    kanbanId: KanbanId, laneId: LaneId, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {

    val now = CurrentDateUtil.nowDateTime
    note.noteId match {
      case Some(_) =>
        Right(1)
      case _ =>
        //新規登録
        val entity = model.Note(
          id = -1L,
          laneId = laneId.id,
          kanbanId = kanbanId.id,
          noteTitle = note.title,
          noteDescription = note.description,
          fixDate = note.fixDate,
          sortNum = Long.MaxValue,
          createLoginUserInfoId = loginUser.userId.get.id,
          createAt = now,
          lastUpdateLoginUserInfoId = loginUser.userId.get.id,
          lastUpdateAt = now,
          lockVersion = 1L
        )
        val noteId = model.Note.create(entity)
        saveChargedUsers(noteId, note.chargedUsers)
        saveAttachmentFiles(noteId, attachmentFileIds)
        Right(noteId)
    }
  }

  /**
   * ふせん添付ファイル登録.
   * ふせんIDに紐づく情報を削除した後、Insertします
   * @param noteId ふせんID
   * @param attachmentFileIds ふせんに紐付ける添付ファイルIDSeq
   * @param session Session
   */
  private[this] def saveAttachmentFiles(noteId: Long, attachmentFileIds: Seq[Long])(implicit session: DBSession): Unit = {
    NoteAttachmentFile.deleteByKanbanId(noteId)
    attachmentFileIds foreach (attachmentFileId => NoteAttachmentFile.create(
      NoteAttachmentFile(
        id = -1L,
        noteId = noteId,
        attachmentFileId = attachmentFileId
      )
    ))
  }

  /**
   * ふせん担当者登録.
   * ふせんIDに紐づく情報を削除した後、Insertします
   * @param noteId ふせんID
   * @param chargedUsers 担当者ユーザSeq
   * @param session Session
   */
  private[this] def saveChargedUsers(noteId: Long, chargedUsers: Seq[ChargedUser])(implicit session: DBSession): Unit = {
    NoteChargedUser.deleteByNoteId(noteId)
    chargedUsers foreach { user =>
      val chargeUser = NoteChargedUser(
        id = -1L,
        noteId = noteId,
        loginUserInfoId = user.userId.id
      )
      NoteChargedUser.create(chargeUser)
    }
  }

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[Note] = ???

  /**
   * @inheritdoc
   */
  override def deleteAll()(implicit session: DBSession): Unit = ???
}
