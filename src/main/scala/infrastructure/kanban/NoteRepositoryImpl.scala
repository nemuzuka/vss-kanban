package infrastructure.kanban

import domain.ApplicationException
import domain.attachment.AttachmentFileRow
import domain.kanban._
import domain.user.{ User, UserId }
import model.{ NoteAttachmentFile, NoteChargedUser }
import scalikejdbc.DBSession
import skinny.logging.Logger
import util.CurrentDateUtil

import scala.util.{ Failure, Success, Try }

/**
 * NoteRepositoryの実装クラス.
 */
class NoteRepositoryImpl extends NoteRepository {

  private[this] val logger = Logger(this.getClass)

  /**
   * @inheritdoc
   */
  override def findById(noteId: NoteId)(implicit session: DBSession): Option[Note] = {
    for (
      note <- model.Note.findById(noteId.id)
    ) yield {
      Note(
        noteId = Option(noteId),
        sortNum = note.sortNum,
        noteStatus = NoteStatus.withCode(note.archiveStatus).get,
        title = note.noteTitle,
        description = note.noteDescription,
        fixDate = note.fixDate,
        createUserId = UserId(note.createLoginUserInfoId),
        lockVersion = note.lockVersion,
        chargedUsers = NoteChargedUser.findByNoteId(noteId.id) map { v =>
          ChargedUser.createChargedUser(v._2.loginUserInfoId, v._1.userName)
        }
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def findByCondition(condition: NoteCondition)(implicit session: DBSession): Seq[NoteRow] = {
    val notes = model.Note.findByKanbanId(condition.kanbanId.id, condition.includeArchive)
    val noteIds = notes map { _.id }
    val noteAttachmentFileCountMap: Map[Long, Long] = (NoteAttachmentFile.findCountByNoteIds(noteIds) map { v =>
      v._1 -> v._2
    }).toMap
    val chargedUsers: Map[Long, Seq[String]] = NoteChargedUser.findByNoteIds(noteIds).foldLeft(Map[Long, Seq[String]]()) { (map, value) =>
      {
        val key = value.noteId
        map.updated(key, map.getOrElse(key, Seq()) :+ value.loginUserInfoId.toString)
      }
    }
    notes map { v =>
      NoteRow(
        laneId = v.laneId,
        noteId = v.id,
        noteTitle = v.noteTitle,
        noteDescription = v.noteDescription,
        archiveStatus = v.archiveStatus,
        fixDate = v.fixDate map { _.toString("yyyyMMdd") } getOrElse "",
        hasAttachmentFile = noteAttachmentFileCountMap.get(v.id) exists { value => if (value > 0) true else false },
        chargedUsers = chargedUsers.getOrElse(v.id, Seq())
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def store(note: Note, attachmentFileIds: Seq[Long],
    kanbanId: KanbanId, laneId: LaneId, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {

    val now = CurrentDateUtil.nowDateTime
    val result: Either[ApplicationException, Long] = note.noteId match {
      case Some(noteId) =>
        //変更
        Try {
          val entity = model.Note(
            id = noteId.id,
            laneId = laneId.id,
            kanbanId = kanbanId.id,
            noteTitle = note.title,
            noteDescription = note.description,
            fixDate = note.fixDate,
            sortNum = Long.MaxValue,
            archiveStatus = note.noteStatus.code,
            createLoginUserInfoId = loginUser.userId.get.id,
            createAt = now,
            lastUpdateLoginUserInfoId = loginUser.userId.get.id,
            lastUpdateAt = now,
            lockVersion = note.lockVersion
          )
          model.Note.update(entity)
        } match {
          case Success(id) => Right(id)
          case Failure(e) =>
            logger.error(e.getMessage, e)
            Left(new ApplicationException("invalidVersion", Seq()))
        }

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
          archiveStatus = note.noteStatus.code,
          createLoginUserInfoId = loginUser.userId.get.id,
          createAt = now,
          lastUpdateLoginUserInfoId = loginUser.userId.get.id,
          lastUpdateAt = now,
          lockVersion = 1L
        )
        Right(model.Note.create(entity))
    }

    for {
      noteId <- result.right
    } yield {
      saveChargedUsers(noteId, note.chargedUsers)
      saveAttachmentFiles(noteId, attachmentFileIds)
      noteId
    }
  }

  /**
   * @inheritdoc
   */
  override def findByNoteId(noteId: NoteId)(implicit session: DBSession): Seq[AttachmentFileRow] = {
    NoteAttachmentFile.findByNoteId(noteId.id) map { v =>
      domain.attachment.AttachmentFile.createAttachmentFileRow(v._2)
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
