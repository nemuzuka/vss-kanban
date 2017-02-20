package controller.kanban.admin

import application.KanbanAdminService
import controller.Keys
import domain.user.User
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

}
