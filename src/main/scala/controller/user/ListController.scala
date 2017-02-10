package controller.user

import application.UserSerivce
import controller.ApiController
import domain.user.UserAuthority
import scalikejdbc.DB

/**
 * ユーザ一覧のController.
 */
class ListController extends ApiController {
  override val authentications = Option(Seq(UserAuthority.ApplicationAdministrator))

  /**
   * 全ユーザ取得.
   */
  def all: String = {
    val list = DB localTx { implicit session =>
      val userService = injector.getInstance(classOf[UserSerivce])
      userService.findAll
    }
    createJsonResult(list)
  }
}
