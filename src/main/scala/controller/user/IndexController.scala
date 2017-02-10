package controller.user

import controller.ApplicationController
import domain.user.UserAuthority

/**
 * ユーザ管理画面のController.
 */
class IndexController extends ApplicationController {

  override val authentications = Option(Seq(UserAuthority.ApplicationAdministrator))

  def index: String = render("/user/index")

}
