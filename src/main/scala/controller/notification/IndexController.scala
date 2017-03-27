package controller.notification

import controller.ApplicationController
import domain.user.UserAuthority

/**
 * 通知画面のController.
 */
class IndexController extends ApplicationController {

  override val authentications = None

  def index: String = render("/notification/index")

}
