package application.impl

import javax.inject.Inject

import application.KanbanService
import domain.kanban._
import domain.user.{ User, UserAuthority }
import scalikejdbc.DBSession

/**
 * KanbanServiceの実装クラス.
 */
class KanbanServiceImpl @Inject() (
    kanbanRepository: KanbanRepository,
    laneRepository: LaneRepository
) extends KanbanService {
  /**
   * @inheritdoc
   */
  override def create(kanbanTitle: String, kanbanDescription: String, loginUserId: Long)(implicit session: DBSession): Long = {
    val kanban = Kanban.createInitKanban(kanbanTitle, kanbanDescription, loginUserId)

    val kanbanId = kanbanRepository.store(kanban) match {
      case Right(id) => id
      case Left(e) => throw e
    }
    //初期レーンを作成する
    Lane.createInitLane foreach { lane => laneRepository.store(lane, KanbanId(kanbanId)) }
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
