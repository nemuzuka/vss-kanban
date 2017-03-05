package application.impl

import javax.inject.Inject

import application._
import domain.ApplicationException
import domain.kanban._
import domain.user.User
import form.kanban.{ Edit, JoinedUser, Stage }
import scalikejdbc.DBSession

/**
 * KanbanAdminServiceの実装クラス.
 */
class KanbanAdminServiceImpl @Inject() (
    userSerivce: UserSerivce,
    kanbanRepository: KanbanRepository,
    stageRepository: StageRepository
) extends KanbanAdminService {

  /**
   * @inheritdoc
   */
  override def getForm(id: Long, loginUser: User)(implicit session: DBSession): Option[Edit] = {
    for (
      kanbanRow <- kanbanRepository.findRowById(KanbanId(id), loginUser)
    ) yield {
      Edit(
        id = kanbanRow.id.toString,
        kanbanTitle = kanbanRow.title,
        kanbanDescription = kanbanRow.description,
        archiveStatus = kanbanRow.archiveStatus,
        lockVersion = kanbanRow.lockVersion.toString
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def updateKanban(form: Edit, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val result = for {
      kanban <- kanbanRepository.findById(KanbanId(form.id.toLong), loginUser) if kanban.isAdministrator(loginUser)
    } yield {
      val updateKanban = kanban.copy(configuration =
        KanbanConfiguration(
          title = form.kanbanTitle,
          description = form.kanbanDescription,
          kanbanStatus = KanbanStatus.withCode(form.archiveStatus).get,
          lockVersion = form.lockVersion.toLong
        ))
      kanbanRepository.store(updateKanban)
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }

  /**
   * @inheritdoc
   */
  override def getJoinedUser(id: Long, loginUser: User)(implicit session: DBSession): Option[JoinedUserTargetDto] = {
    for {
      kanban <- kanbanRepository.findById(KanbanId(id), loginUser) if kanban.isAdministrator(loginUser)
    } yield {
      JoinedUserTargetDto(
        id = id,
        lockVersion = kanban.configuration.lockVersion,
        joinedUsers = JoinedUserDto.toDto(kanban.joinedUsers),
        allUsers = userSerivce.findAll
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def updateJoinedUser(form: JoinedUser, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val result = for {
      kanban <- kanbanRepository.findById(KanbanId(form.id), loginUser) if kanban.isAdministrator(loginUser)
    } yield {

      val joinedUsers = form.joinedUserIds map { userId =>
        domain.kanban.JoinedUser.createJoinedUser(
          userId,
          if (form.adminUserIds.contains(userId)) "1" else "0"
        )
      }

      val updateKanban = kanban.copy(
        configuration = kanban.configuration.copy(lockVersion = form.lockVersion),
        joinedUsers = joinedUsers
      )
      kanbanRepository.store(updateKanban)
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }

  /**
   * @inheritdoc
   */
  override def getStage(id: Long, loginUser: User)(implicit session: DBSession): Option[StageTargetDto] = {
    val kanbanId = KanbanId(id)
    for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isAdministrator(loginUser)
    } yield {
      StageTargetDto(
        id = id,
        lockVersion = kanban.configuration.lockVersion,
        stages = stageRepository.findByKanbanId(kanbanId, includeArchive = true)
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def updateStage(form: Stage, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val kanbanId = KanbanId(form.id)
    val result = for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isAdministrator(loginUser)
    } yield {

      val updateKanban = kanban.copy(
        configuration = kanban.configuration.copy(lockVersion = form.lockVersion)
      )
      kanbanRepository.store(updateKanban) match {
        case Right(id) =>
          stageRepository.store(domain.kanban.Stage.createStages(form.stageIds, form.stageNames, form.archiveStatuses, form.completeStages), kanbanId)
          Right(id)
        case e => e
      }
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }
}
