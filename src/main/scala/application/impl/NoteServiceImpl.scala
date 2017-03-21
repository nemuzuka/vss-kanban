package application.impl

import javax.inject.Inject

import application.{ JoinedUserDto, NoteDetail, NoteEditDetail, NoteService }
import domain.ApplicationException
import domain.attachment.AttachmentFileId
import domain.kanban._
import domain.user.User
import form.kanban.Note
import scalikejdbc.DBSession

/**
 * NoteServiceの実装クラス.
 */
class NoteServiceImpl @Inject() (
    kanbanRepository: KanbanRepository,
    noteRepository: NoteRepository
) extends NoteService {
  /**
   * @inheritdoc
   */
  override def getForm(kanbanId: KanbanId, stageId: StageId, noteId: Option[NoteId], loginUser: User)(implicit session: DBSession): Option[NoteEditDetail] = {
    val result = for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isJoined(loginUser)
    } yield {
      if (noteId.isEmpty) {
        Option(NoteEditDetail(
          form = form.kanban.Note.createInitForm(kanbanId, stageId),
          joinedUsers = JoinedUserDto.toDto(kanban.joinedUsers),
          noteAttachmentFiles = Seq()
        ))
      } else {
        for (
          note <- noteRepository.findById(noteId.get) if note.isCharged(loginUser, kanban)
        ) yield {
          NoteEditDetail(
            form = form.kanban.Note.fromDomain(kanbanId, stageId, note),
            joinedUsers = JoinedUserDto.toDto(kanban.joinedUsers),
            noteAttachmentFiles = noteRepository.findByNoteId(noteId.get)
          )
        }
      }
    }
    result getOrElse None
  }

  /**
   * @inheritdoc
   */
  override def storeNote(form: Note, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val note = form.toDomain(loginUser)
    noteRepository.store(note, form.attachmentFileIds map AttachmentFileId,
      KanbanId(form.kanbanId.toLong), StageId(form.stageId.toLong), loginUser)
  }

  /**
   * @inheritdoc
   */
  override def getDetail(kanbanId: KanbanId, stageId: StageId, noteId: NoteId, loginUser: User)(implicit session: DBSession): Option[NoteDetail] = {
    for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isJoined(loginUser)
      note <- noteRepository.findById(noteId)
    } yield {
      NoteDetail(
        form = form.kanban.Note.fromDomain(kanbanId, stageId, note),
        chargedUserNames = note.chargedUsers map (_.name),
        noteAttachmentFiles = noteRepository.findByNoteId(noteId),
        isCharged = note.isCharged(loginUser, kanban),
        joinedUsers = JoinedUserDto.toDto(kanban.joinedUsers),
        comments = noteRepository.findCommentsByNoteId(noteId)
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def deleteById(kanbanId: KanbanId, noteId: NoteId, lockVersion: Long, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val result = for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isJoined(loginUser)
      note <- noteRepository.findById(noteId) if note.isCharged(loginUser, kanban)
    } yield {
      noteRepository.deleteById(noteId, lockVersion)
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }

  /**
   * @inheritdoc
   */
  override def moveNote(stageId: StageId, noteId: Option[NoteId], noteIds: Seq[Long], loginUser: User)(implicit session: DBSession): Unit = {
    noteId foreach (v => noteRepository.moveStage(v, stageId, loginUser))
    noteRepository.updateSortNum(noteIds map NoteId)
  }
}
