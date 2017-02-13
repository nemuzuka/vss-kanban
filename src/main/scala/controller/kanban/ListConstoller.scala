package controller.kanban

import application.KanbanService
import controller.{ ApiController, Keys }
import domain.user.User
import scalikejdbc.DB

/**
 * かんばん一覧のController.
 */
class ListConstoller extends ApiController {
  override val authentications = None

  /**
   * かんばん一覧.
   */
  def list: String = {
    val result = DB localTx { implicit dbSession =>
      val kanbanService = injector.getInstance(classOf[KanbanService])
      kanbanService.findByParam(
        viewArchiveKanban = params.getAs[Boolean]("viewArchiveKanban").getOrElse(false),
        viewAllKanban = params.getAs[Boolean]("viewAllKanban").getOrElse(false),
        loginUser = session.getAs[User](Keys.Session.UserInfo).get
      )
    }
    createJsonResult(result)
  }

}
