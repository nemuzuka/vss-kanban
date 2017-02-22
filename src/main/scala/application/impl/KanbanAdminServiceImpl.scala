package application.impl

import javax.inject.Inject

import application._
import domain.ApplicationException
import domain.kanban._
import domain.user.User
import form.kanban.{ Edit, JoinedUser, Lane }
import scalikejdbc.DBSession

/**
 * KanbanAdminServiceの実装クラス.
 */
class KanbanAdminServiceImpl @Inject() (
    userSerivce: UserSerivce,
    kanbanRepository: KanbanRepository,
    laneRepository: LaneRepository
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
        joinedUsers = kanban.joinedUsers map (v => {
        JoinedUserDto(
          userId = v.userId.id,
          authority = v.authority.code
        )
      }),
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
  override def getLane(id: Long, loginUser: User)(implicit session: DBSession): Option[LaneTargetDto] = {
    val kanbanId = KanbanId(id)
    for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isAdministrator(loginUser)
    } yield {
      LaneTargetDto(
        id = id,
        lockVersion = kanban.configuration.lockVersion,
        lanes = laneRepository.findByKanbanId(kanbanId, includeArchive = true)
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def updateLane(form: Lane, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val kanbanId = KanbanId(form.id)
    val result = for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isAdministrator(loginUser)
    } yield {

      val updateKanban = kanban.copy(
        configuration = kanban.configuration.copy(lockVersion = form.lockVersion)
      )
      kanbanRepository.store(updateKanban) match {
        case Right(_) =>
          updateLane(kanbanId, form)
        case e => e
      }
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }

  /**
   * レーン情報更新.
   * レーンIDが未設定のものはinsert/存在するものはupdateします。
   * また、既に登録されているレーンの中で今回の更新に含まれていないものは削除します。
   * @param kanbanId かんばんID
   * @param form レーン変更Form
   * @param session Session
   * @return Right:かんばんID, Left:エラー情報
   */
  private[this] def updateLane(kanbanId: KanbanId, form: Lane)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val beforeLaneIdSeq = laneRepository.findByKanbanId(kanbanId, includeArchive = true) map (_.laneId.toLong)
    val afterLaneIdSeq = domain.kanban.Lane.createLanes(form.laneIds, form.laneNames, form.archiveStatuses, form.completeLanes) map { v =>
      laneRepository.store(v, kanbanId).id
    }

    beforeLaneIdSeq diff afterLaneIdSeq foreach { id => laneRepository.deleteById(LaneId(id)) }
    Right(kanbanId.id)
  }

}
