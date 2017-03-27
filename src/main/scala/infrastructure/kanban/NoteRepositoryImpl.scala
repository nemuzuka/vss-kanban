package infrastructure.kanban

import domain.ApplicationException
import domain.attachment.{ AttachmentFile, AttachmentFileId, AttachmentFileRow }
import domain.kanban._
import domain.user.{ User, UserAuthority, UserId }
import model.{ Note => _, _ }
import org.joda.time.DateTime
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
        },
        watchUsers = NoteWatchUser.findByNoteIdAndLoginUserInfoId(noteId.id) map { v => UserId(v.loginUserInfoId) }
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def findStageIdByNoteId(noteId: NoteId)(implicit session: DBSession): Option[StageId] = {
    for (
      note <- model.Note.findById(noteId.id)
    ) yield {
      StageId(note.stageId)
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
    val noteLastCreateCommentMap: Map[Long, String] = (NoteComment.findLastCreateCommentByNoteIds(noteIds) map (result => result._1 -> result._2.toString("yyyyMMddHHmmss"))).toMap

    notes map { v =>
      NoteRow(
        stageId = v.stageId,
        noteId = v.id,
        noteTitle = v.noteTitle,
        noteDescription = v.noteDescription,
        archiveStatus = v.archiveStatus,
        fixDate = v.fixDate map { _.toString("yyyyMMdd") } getOrElse "",
        hasAttachmentFile = noteAttachmentFileCountMap.get(v.id) exists { value => if (value > 0) true else false },
        chargedUsers = chargedUsers.getOrElse(v.id, Seq()),
        lastCommentAt = noteLastCreateCommentMap.getOrElse(v.id, "")
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def store(note: Note, attachmentFileIds: Seq[AttachmentFileId],
    kanbanId: KanbanId, stageId: StageId, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {

    val now = CurrentDateUtil.nowDateTime
    val result: Either[ApplicationException, Long] = note.noteId match {
      case Some(noteId) =>
        //変更
        Try {

          //更新前のステージIDと異なる場合、履歴に追加
          for {
            note <- model.Note.findById(noteId.id) if note.stageId != stageId.id
          } yield {
            saveNoteHistory(noteId.id, stageId.id, loginUser.userId.get.id, now)
          }

          val entity = model.Note(
            id = noteId.id,
            stageId = stageId.id,
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
          stageId = stageId.id,
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

        val noteId = model.Note.create(entity)
        //履歴登録
        saveNoteHistory(noteId, entity.stageId, entity.createLoginUserInfoId, now)
        Right(noteId)
    }

    for {
      noteId <- result.right
    } yield {
      saveChargedUsers(noteId, note.chargedUsers)
      saveAttachmentFiles(noteId, attachmentFileIds)
      saveNoteNotification(kanbanId, NoteId(noteId),
        if (note.noteId.isDefined) NoteNotificationType.UpdateNote else NoteNotificationType.CreateNote,
        loginUser)
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
   * @inheritdoc
   */
  override def store(stageId: StageId, noteId: NoteId, comment: String, attachmentFileIds: Seq[AttachmentFileId], loginUser: User)(implicit session: DBSession): Option[Long] = {

    val moveResult = moveStage(noteId, stageId, loginUser)

    if (comment.isEmpty && attachmentFileIds.isEmpty) {
      None
    } else {
      val noteCommentId = NoteComment.create(NoteComment(
        id = -1L,
        noteId = noteId.id,
        commentText = comment,
        createAt = CurrentDateUtil.nowDateTime,
        createLoginUserInfoId = loginUser.userId.get.id
      ))

      attachmentFileIds foreach (attachmentFileId => NoteCommentAttachmentFile.create(
        NoteCommentAttachmentFile(
          id = 1L,
          noteCommentId = noteCommentId,
          attachmentFileId = attachmentFileId.id
        )
      ))

      //移動しなかった場合、コメント登録されたことを通知する
      if (moveResult.isEmpty) {
        model.Note.findById(noteId.id) foreach { note =>
          saveNoteNotification(KanbanId(note.kanbanId), noteId, NoteNotificationType.CreateNoteComment, loginUser)
        }
      }

      Option(noteCommentId)
    }
  }

  /**
   * @inheritdoc
   */
  override def findCommentsByNoteId(noteId: NoteId)(implicit session: DBSession): Seq[NoteCommentRow] = {
    val noteComments = NoteComment.findByNoteId(noteId.id)
    val noteCommentIds = noteComments map { v => v._1.id }
    val noteCommentAttachmentFiles = NoteCommentAttachmentFile.findByNoteId(noteCommentIds)
    val noteCommentAttachmentFileMap = noteCommentAttachmentFiles.foldLeft(Map[Long, Seq[AttachmentFileRow]]()) { (map, value) =>
      {
        val noteCommentAttachmentFile = value._1
        val key = noteCommentAttachmentFile.noteCommentId
        map.updated(key, map.getOrElse(key, Seq()) :+ AttachmentFile.createAttachmentFileRow(value._2))
      }
    }

    noteComments map { noteComment =>
      NoteCommentRow(
        noteCommentId = noteComment._1.id,
        noteId = noteComment._1.noteId,
        comment = noteComment._1.commentText,
        createUserName = noteComment._2 map { _.userName } getOrElse "",
        createUserId = noteComment._1.createLoginUserInfoId,
        createAt = noteComment._1.createAt.toString("yyyyMMddHHmmss"),
        attachmentFiles = noteCommentAttachmentFileMap.getOrElse(noteComment._1.id, Seq())
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def deleteById(noteId: NoteId, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long] = {
    Try {
      model.Note.deleteByIdAndVersion(noteId.id, lockVersion)
      noteId.id
    } match {
      case Success(id) => Right(id)
      case Failure(e) =>
        logger.error(e.getMessage, e)
        Left(new ApplicationException("invalidVersion", Seq()))
    }
  }

  /**
   * @inheritdoc
   */
  override def moveStage(noteId: NoteId, stageId: StageId, loginUser: User)(implicit session: DBSession): Option[StageId] = {
    for {
      note <- model.Note.findById(noteId.id) if note.stageId != stageId.id
    } yield {
      model.Note.updateByStage(noteId.id, stageId.id)
      saveNoteHistory(noteId.id, stageId.id, loginUser.userId.get.id, CurrentDateUtil.nowDateTime)
      saveNoteNotification(KanbanId(note.kanbanId), noteId, NoteNotificationType.MoveNote, loginUser)
      stageId
    }
  }

  /**
   * @inheritdoc
   */
  override def updateSortNum(noteIds: Seq[NoteId])(implicit session: DBSession): Unit = {
    noteIds.zipWithIndex foreach (v => model.Note.updateSortNum(v._1.id, v._2))
  }

  /**
   * @inheritdoc
   */
  override def watch(noteId: NoteId, loginUser: User)(implicit session: DBSession): Unit = {
    model.Note.findById(noteId.id) foreach (_ =>
      NoteWatchUser.create(NoteWatchUser(
        id = -1L,
        noteId = noteId.id,
        loginUserInfoId = loginUser.userId.get.id
      )))
  }

  /**
   * @inheritdoc
   */
  override def unWatch(noteId: NoteId, loginUser: User)(implicit session: DBSession): Unit = {
    model.Note.findById(noteId.id) foreach (_ =>
      NoteWatchUser.deleteByNoteIdAndLoginUserInfoId(noteId.id, loginUser.userId.get.id))
  }

  /**
   * @inheritdoc
   */
  override def deleteNotification(noteId: NoteId, userId: UserId)(implicit session: DBSession): Unit = {
    NoteNotification.deleteByNoteIdAndLoginUserInfoId(noteId.id, userId.id)
  }

  /**
   * @inheritdoc
   */
  override def hasUnreadNotification(userId: UserId)(implicit session: DBSession): Boolean = NoteNotification.hasUnreadNotification(userId.id)

  /**
   * @inheritdoc
   */
  override def findByAllUnreadNotification(loginUser: User)(implicit session: DBSession): Seq[NoteNotificationRow] = {

    if (loginUser.authority != UserAuthority.ApplicationAdministrator) NoteNotification.deleteByNotExistsJoinedUser(loginUser.userId.get.id)

    NoteNotification.findByLoginUserInfoId(loginUser.userId.get.id) map { v =>
      val noteNotification = v._1
      val kanban = v._2
      val note = v._3
      val loginUserInfoOpt = v._4
      NoteNotificationRow(
        id = noteNotification.id,
        noteId = note.id,
        noteTitle = note.noteTitle,
        kanbanId = kanban.id,
        kanbanTitle = kanban.kanbanTitle,
        actionLabel = noteNotification.actionLabel,
        createUserName = loginUserInfoOpt map { v => v.userName } getOrElse "",
        createAt = noteNotification.createAt.toString("yyyyMMddHHmmss")
      )
    }
  }

  /**
   * ふせん添付ファイル登録.
   * ふせんIDに紐づく情報を削除した後、Insertします
   * @param noteId ふせんID
   * @param attachmentFileIds ふせんに紐付ける添付ファイルIDSeq
   * @param session Session
   */
  private[this] def saveAttachmentFiles(noteId: Long, attachmentFileIds: Seq[AttachmentFileId])(implicit session: DBSession): Unit = {
    NoteAttachmentFile.deleteByKanbanId(noteId)
    attachmentFileIds foreach (attachmentFileId => NoteAttachmentFile.create(
      NoteAttachmentFile(
        id = -1L,
        noteId = noteId,
        attachmentFileId = attachmentFileId.id
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
   * ふせん - 履歴登録.
   * @param noteId ふせんID
   * @param stageId ステージID
   * @param userId 更新ユーザID
   * @param now 現在時刻
   * @param session Session
   */
  private[this] def saveNoteHistory(noteId: Long, stageId: Long, userId: Long, now: DateTime)(implicit session: DBSession): Unit = {
    model.NoteHistory.create(
      NoteHistory(
        id = -1L,
        noteId = noteId,
        stageId = stageId,
        lastUpdateAt = now,
        lastUpdateLoginUserInfoId = userId
      )
    )
  }

  /**
   * ふせん通知登録.
   * 1. 対象のふせんに対して既に登録されている通知を削除
   * 2. ふせんに紐づく担当者、ウォッチユーザを取得
   * 3. 対象のふせんに対する通知対象者に対して通知データを登録(操作者は除く)
   * @param kanbanId かんばんID
   * @param noteId ふせんID
   * @param notificationType ふせん通知区分
   * @param loginUser ログインユーザ情報
   * @param session Session
   */
  private[this] def saveNoteNotification(kanbanId: KanbanId, noteId: NoteId,
    notificationType: NoteNotificationType, loginUser: User)(implicit session: DBSession): Unit = {

    //ふせんに紐づく担当者、ウォッチユーザを取得
    for {
      note <- findById(noteId)
    } yield {
      NoteNotification.deleteByNoteId(noteId.id)

      val userIds = ((note.watchUsers map (_.id)) ++ (note.chargedUsers map (_.userId.id))).distinct
      val targetIds = userIds diff Seq(loginUser.userId.get.id)
      val baseNotification = NoteNotification(
        id = -1L,
        noteId = noteId.id,
        kanbanId = kanbanId.id,
        loginUserInfoId = -1L,
        actionLabel = notificationType.label,
        createLoginUserInfoId = loginUser.userId.get.id,
        createAt = CurrentDateUtil.nowDateTime
      )
      targetIds foreach { v =>
        NoteNotification.create(baseNotification.copy(loginUserInfoId = v))
      }
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
