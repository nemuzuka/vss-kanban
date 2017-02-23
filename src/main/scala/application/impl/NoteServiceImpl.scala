package application.impl

import javax.inject.Inject

import application.{ JoinedUserDto, NoteEditDetail, NoteService }
import domain.ApplicationException
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
  override def getForm(kanbanId: KanbanId, laneId: LaneId, noteId: Option[NoteId], loginUser: User)(implicit session: DBSession): Option[NoteEditDetail] = {
    val result = for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isJoined(loginUser)
    } yield {
      if (noteId.isEmpty) {
        Option(NoteEditDetail(
          form = form.kanban.Note.createInitForm(kanbanId, laneId),
          joinedUsers = JoinedUserDto.toDto(kanban.joinedUsers),
          noteAttachmentFiles = Seq()
        ))
      } else {
        //変更用のForm作成
        None
      }
    }
    result getOrElse None
  }

  /**
   * @inheritdoc
   */
  override def storeNote(form: Note, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val note = form.toDomain(loginUser)
    noteRepository.store(note, form.attachmentFileIds,
      KanbanId(form.kanbanId.toLong), LaneId(form.laneId.toLong), loginUser)
  }
}
