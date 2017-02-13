package application.impl

import javax.inject.Inject

import application.KanbanService
import domain.kanban._
import domain.user.{ User, UserAuthority, UserId }
import scalikejdbc.DBSession

/**
 * KanbanServiceの実装クラス.
 */
class KanbanServiceImpl @Inject() (
    kanbanRepository: KanbanRepository
) extends KanbanService {
  /**
   * @inheritdoc
   */
  override def create(kabanTitle: String, kanbanDescription: String, loginUserId: Long)(implicit session: DBSession): Long = {
    val kanban = Kanban(
      kanbanId = None,
      configuration = KanbanConfiguration(
        title = kabanTitle,
        description = kanbanDescription,
        kanbanStatus = KanbanStatus.Open
      ),
      joinedUsers = Seq(
        JoinedUser(
          userId = UserId(loginUserId),
          authority = KanbanAuthority.Administrator,
          name = ""
        )
      )
    )
    val kanbanId: Long = kanbanRepository.store(kanban) match {
      case Right(id) => id
      case Left(e) => throw e
    }

    val lanes = Seq(
      model.Lane(
        id = -1L,
        kanbanId = kanbanId,
        laneName = "未実施",
        archiveStatus = LaneStatus.Open.code,
        sortNum = Long.MaxValue,
        completeLane = "0"
      ),
      model.Lane(
        id = -1L,
        kanbanId = kanbanId,
        laneName = "実施中",
        archiveStatus = LaneStatus.Open.code,
        sortNum = Long.MaxValue,
        completeLane = "0"
      ),
      model.Lane(
        id = -1L,
        kanbanId = kanbanId,
        laneName = "完了",
        archiveStatus = LaneStatus.Open.code,
        sortNum = Long.MaxValue,
        completeLane = "1"
      )
    )
    lanes foreach model.Lane.create
    kanbanId
  }

  /**
   * @inheritdoc
   */
  override def findByParam(
    viewArchiveKanban: Boolean,
    viewAllKanban: Boolean, loginUser: User
  )(implicit session: DBSession): KanbanSearchResult = {

    val searchParam = KanbanSearchParam(
      viewArchiveKanban = viewArchiveKanban,
      viewAllKanban = if (loginUser.authority == UserAuthority.ApplicationAdministrator && viewAllKanban) viewAllKanban else false,
      loginUserId = loginUser.userId.get.id
    )
    kanbanRepository.findByParam(searchParam)
  }
}
