package application.impl

import javax.inject.Inject

import application.{ JoinedUserDto, KanbanDetail, KanbanService }
import domain.kanban._
import domain.user.{ User, UserAuthority }
import scalikejdbc.DBSession

/**
 * KanbanServiceの実装クラス.
 */
class KanbanServiceImpl @Inject() (
    kanbanRepository: KanbanRepository,
    stageRepository: StageRepository,
    noteRepository: NoteRepository
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
    //初期ステージを作成する
    stageRepository.store(Stage.createInitStage, KanbanId(kanbanId))
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
      loginUserId = loginUser.userId.get.id,
      userAuthority = loginUser.authority
    )
    kanbanRepository.findByParam(searchParam)
  }

  /**
   * @inheritdoc
   */
  override def findById(id: Long, includeArchive: Boolean, loginUser: User)(implicit session: DBSession): Option[KanbanDetail] = {
    val kanbanId = KanbanId(id)
    for {
      kanban <- kanbanRepository.findById(kanbanId, loginUser) if kanban.isJoined(loginUser)
    } yield {

      val noteMap = noteRepository.findByCondition(NoteCondition(
        kanbanId = kanban.kanbanId.get,
        includeArchive = includeArchive
      )).foldLeft(Map[String, Seq[NoteRow]]()) { (map, value) =>
        {
          val key = value.stageId.toString
          map.updated(key, map.getOrElse(key, Seq()) :+ value)
        }
      }

      KanbanDetail(
        kanban = kanban.toKanbanRow(loginUser),
        stages = stageRepository.findByKanbanId(kanbanId, includeArchive),
        noteMap = noteMap,
        kanbanAttachmentFiles = kanbanRepository.findByKanbanId(kanbanId),
        joinedUserMap = (kanban.joinedUsers map { v =>
          v.userId.id.toString -> JoinedUserDto(
            userId = v.userId.id,
            name = v.name,
            authority = v.authority.code
          )
        }).toMap
      )
    }
  }
}
