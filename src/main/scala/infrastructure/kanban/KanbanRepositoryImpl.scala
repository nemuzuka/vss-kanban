package infrastructure.kanban

import domain.ApplicationException
import domain.kanban._
import domain.user.UserId
import model.{ KanbanJoinedUser, LoginUserInfo }
import scalikejdbc.DBSession
import util.CurrentDateUtil

/**
 * KanbanRepositoryの実装クラス.
 */
class KanbanRepositoryImpl extends KanbanRepository {

  /**
   * @inheritdoc
   */
  override def findById(kanbanId: KanbanId)(implicit session: DBSession): Option[Kanban] = {
    for (
      kanban <- model.Kanban.findById(kanbanId.id)
    ) yield {
      val joinedUsers = model.KanbanJoinedUser.findByKanbanId(kanbanId.id)
      Kanban(
        kanbanId = Option(kanbanId),
        configuration = KanbanConfiguration(
          title = kanban.kanbanTitle,
          description = kanban.kanbanDescription,
          kanbanStatus = KanbanStatus.withCode(kanban.archiveStatus).get,
          lockVersion = kanban.lockVersion
        ),
        joinedUsers = joinedUsers map createJoinedUser
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def store(kanban: Kanban)(implicit session: DBSession): Either[ApplicationException, Long] = {
    if (kanban.kanbanId.isEmpty) {
      //新規登録
      val now = CurrentDateUtil.nowDateTime
      val entity = model.Kanban(
        id = -1L,
        kanbanTitle = kanban.configuration.title,
        kanbanDescription = kanban.configuration.description,
        archiveStatus = KanbanStatus.Open.code,
        createAt = now,
        lastUpdateAt = now,
        lockVersion = 1L
      )
      val kanbanId = model.Kanban.create(entity)
      kanban.joinedUsers foreach { v =>
        val entity = KanbanJoinedUser(
          id = -1L,
          kanbanId = kanbanId,
          loginUserInfoId = v.userId.id,
          kanbanAuthority = v.authority.code
        )
        KanbanJoinedUser.create(entity)
      }
      Right(kanbanId)
    } else {
      //TODO ひとまず
      Left(new ApplicationException("", Seq()))
    }
  }

  /**
   * リポジトリ上の全件取得
   *
   * @param session Session
   * @return 該当データ
   */
  override def findAll(implicit session: DBSession): Seq[Kanban] = ???

  /**
   * リポジトリ上の全件削除.
   *
   * @param session Session
   */
  override def deleteAll()(implicit session: DBSession): Unit = ???

  /**
   * かんばん参加ユーザ生成.
   * @param param ユーザ情報(_1:LoginUserInfo, _2:KanbanJoinedUser)
   * @return かんばん参加ユーザ
   */
  private[this] def createJoinedUser(param: (LoginUserInfo, KanbanJoinedUser)) = {
    JoinedUser(
      userId = UserId(param._1.id),
      authority = KanbanAuthority.withCode(param._2.kanbanAuthority).get,
      name = param._1.userName
    )
  }
}
