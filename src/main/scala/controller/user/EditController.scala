package controller.user

import application.UserSerivce
import controller.{ ApiController, Keys }
import domain.user.UserAuthority
import scalikejdbc.DB

/**
 * ユーザ登録・変更・削除のController.
 */
class EditController extends ApiController {
  protectFromForgery()
  override val authentications = Option(Seq(UserAuthority.ApplicationAdministrator))

  /**
   * Form情報取得.
   */
  def detail: String = {
    DB localTx { implicit session =>
      val userService = injector.getInstance(classOf[UserSerivce])
      userService.getForm(params.getAs[Long]("userId"))
    } match {
      case Some(form) =>
        createJsonResult(form)
      case _ =>
        val map = Map(Keys.ErrMsg.Key -> Seq(messages.get("noData").get))
        createJsonResult(map)
    }
  }
}
