package infrastructure.kanban

import domain.ApplicationException
import domain.attachment.{ AttachmentFile, AttachmentFileRow }
import domain.kanban.{ KanbanRow, _ }
import domain.user.{ User, UserAuthority, UserId }
import model.{ KanbanAttachmentFile, KanbanJoinedUser, LoginUserInfo }
import scalikejdbc.DBSession
import util.CurrentDateUtil

/**
 * KanbanRepositoryの実装クラス.
 */
class KanbanRepositoryImpl extends KanbanRepository {

  /**
   * @inheritdoc
   */
  override def findById(kanbanId: KanbanId, loginUser: User)(implicit session: DBSession): Option[Kanban] = {
    val result = for (
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

    //かんばんが参照できるかチェック
    for (
      kanban <- result if kanban.isJoined(loginUser)
    ) yield {
      kanban
    }
  }

  /**
   * @inheritdoc
   */
  override def findRowById(kanbanId: KanbanId, loginUser: User)(implicit session: DBSession): Option[KanbanRow] = {
    for (
      kanban <- findById(kanbanId, loginUser)
    ) yield {
      KanbanRow(
        id = kanban.kanbanId.get.id,
        title = kanban.configuration.title,
        description = kanban.configuration.description,
        archiveStatus = kanban.configuration.kanbanStatus.code,
        lockVersion = kanban.configuration.lockVersion,
        authority = if (kanban.isAdministrator(loginUser)) "1" else "0"
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
   * @inheritdoc
   */
  override def findByParam(param: KanbanSearchParam)(implicit session: DBSession): KanbanSearchResult = {
    val joinedKanbans = model.Kanban.findByloginUserInfoId(param.loginUserId, param.viewArchiveKanban)
    val joinedKanbanIds = joinedKanbans map (_._1.id)
    val allKanbans = if (param.viewAllKanban || joinedKanbanIds.isEmpty) Seq() else model.Kanban.findByNotExistsIds(joinedKanbanIds, param.viewArchiveKanban)
    KanbanSearchResult(
      joinedKanbans = joinedKanbans map { v =>
      KanbanRow(
        id = v._1.id,
        title = v._1.kanbanTitle,
        description = v._1.kanbanDescription,
        archiveStatus = v._1.archiveStatus,
        lockVersion = v._1.lockVersion,
        authority = if (param.userAuthority == UserAuthority.ApplicationAdministrator) "1" else v._2.kanbanAuthority
      )
    },
      otherKanbans = allKanbans map { v =>
      KanbanRow(
        id = v.id,
        title = v.kanbanTitle,
        description = v.kanbanDescription,
        archiveStatus = v.archiveStatus,
        lockVersion = v.lockVersion,
        authority = if (param.userAuthority == UserAuthority.ApplicationAdministrator) "1" else "0"
      )
    }
    )
  }

  /**
   * @inheritdoc
   */
  override def storeKanbanAttachmentFile(kanbanId: Long, attachmentFileIdSeq: Seq[Long])(implicit session: DBSession): Unit = {
    //かんばんIDに紐づく関連を削除
    KanbanAttachmentFile.deleteByKanbanId(kanbanId)
    //引数の情報を登録
    attachmentFileIdSeq foreach (attachmentFileId => {
      KanbanAttachmentFile.create(KanbanAttachmentFile(
        id = -1L,
        kanbanId = kanbanId,
        attachmentFileId = attachmentFileId
      ))
    })
  }

  /**
   * @inheritdoc
   */
  override def findByKanbanId(kanbanId: KanbanId)(implicit session: DBSession): Seq[AttachmentFileRow] = {
    KanbanAttachmentFile.findByKanbanId(kanbanId.id).map { v =>
      AttachmentFile.createAttachmentFileRow(v._2)
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
