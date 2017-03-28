package controller.kanban

import controller.{ ApiController, Keys }
import domain.kanban.NoteRepository
import domain.user.User
import scalikejdbc.DB

/**
 * 通知に関するController.
 */
class NotificationController extends ApiController {

  override val authentications = None

  /**
   * 未読の通知が存在するか.
   */
  def hasUnread: String = {
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get
    DB localTx { implicit session =>
      val noteRepository = injector.getInstance(classOf[NoteRepository])
      createJsonResult(Result(noteRepository.hasUnreadNotification(userInfo.userId.get)))
    }
  }

  /**
   * 未読の通知一覧取得.
   */
  def list: String = {
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get
    DB localTx { implicit session =>
      val noteRepository = injector.getInstance(classOf[NoteRepository])
      createJsonResult(noteRepository.findByAllUnreadNotification(userInfo))
    }
  }

  /**
   * 全て既読にする.
   */
  def allRead: String = {
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get
    DB localTx { implicit session =>
      val noteRepository = injector.getInstance(classOf[NoteRepository])
      noteRepository.deleteNotification(userInfo.userId.get)
    }
    createJsonResult("success")
  }

  /**
   * JSON戻り値.
   * @param hasUnread 未読がある場合、true
   */
  case class Result(hasUnread: Boolean)

}
