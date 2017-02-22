package controller.kanban.admin

import application.KanbanAdminService
import controller.Keys
import domain.kanban.{ KanbanId, KanbanRepository }
import domain.user.User
import form.kanban.{ JoinedUser, Lane }
import scalikejdbc.DB

/**
 * かんばん管理者用のcontroller.
 */
class EditController extends controller.kanban.EditController {

  /**
   * 基本情報取得.
   */
  def base: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
      kanbanAdminService.getForm(kanbanId, userInfo)
    } match {
      case Some(form) =>
        createJsonResult(form)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 基本情報変更.
   */
  def updateBase(): String = {
    validateAndCreateForm match {
      case Right(form) =>
        val userInfo = session.getAs[User](Keys.Session.UserInfo).get

        import scalikejdbc.TxBoundary.Either._
        DB.localTx { implicit session =>
          val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
          kanbanAdminService.updateKanban(form, userInfo)
        } match {
          case Right(_) =>
            createJsonResult("success")
          case Left(exception) =>
            val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
            createJsonResult(errorMsg)
        }

      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * 削除.
   */
  def delete: String = {
    val kanbanId = params.getAs[Long]("id").getOrElse(-1L)
    val lockVersion = params.getAs[Long]("lockVersion").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    import scalikejdbc.TxBoundary.Either._
    DB.localTx { implicit session =>
      val kanbanRepository = injector.getInstance(classOf[KanbanRepository])
      kanbanRepository.deleteById(KanbanId(kanbanId), lockVersion, userInfo)
    } match {
      case Right(_) =>
        createJsonResult("success")
      case Left(exception) =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
        createJsonResult(errorMsg)
    }
  }

  /**
   * 参加者取得.
   */
  def joinedUsers: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
      kanbanAdminService.getJoinedUser(kanbanId, userInfo)
    } match {
      case Some(joinedUser) =>
        createJsonResult(joinedUser)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 参加者変更.
   */
  def updateJoinedUsers(): String = {
    val form = JoinedUser(
      id = params.getAs[Long]("id").getOrElse(-1L),
      lockVersion = params.getAs[Long]("lockVersion").getOrElse(-1L),
      joinedUserIds = multiParams.getAs[Long]("joinedUserIds").getOrElse(Seq()),
      adminUserIds = multiParams.getAs[Long]("adminUserIds").getOrElse(Seq())
    )
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    import scalikejdbc.TxBoundary.Either._
    DB localTx { implicit session =>
      val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
      kanbanAdminService.updateJoinedUser(form, userInfo)
    } match {
      case Right(_) =>
        createJsonResult("success")
      case Left(exception) =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
        createJsonResult(errorMsg)
    }
  }

  /**
   * レーン取得.
   */
  def lanes: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
      kanbanAdminService.getLane(kanbanId, userInfo)
    } match {
      case Some(lane) =>
        createJsonResult(lane)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * レーン変更.
   */
  def updateLanes(): String = {
    val form = Lane(
      id = params.getAs[Long]("id").getOrElse(-1L),
      lockVersion = params.getAs[Long]("lockVersion").getOrElse(-1L),
      laneIds = multiParams.getAs[String]("laneIds").getOrElse(Seq()),
      laneNames = multiParams.getAs[String]("laneNames").getOrElse(Seq()),
      archiveStatuses = multiParams.getAs[String]("archiveStatuses").getOrElse(Seq()),
      completeLanes = multiParams.getAs[Boolean]("completeLanes").getOrElse(Seq())
    )
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    import scalikejdbc.TxBoundary.Either._
    DB localTx { implicit session =>
      val kanbanAdminService = injector.getInstance(classOf[KanbanAdminService])
      kanbanAdminService.updateLane(form, userInfo)
    } match {
      case Right(_) =>
        createJsonResult("success")
      case Left(exception) =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
        createJsonResult(errorMsg)
    }
  }

}
