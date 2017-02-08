package controller

import domain.user.UserAuthority

/**
 * ログアウト用Controller.
 */
class LogoutController extends ApiController {
  override val loginCheck = false
  override val authentications: Option[Seq[UserAuthority]] = None

  /**
   * ログアウト.
   */
  def execute: String = {
    session.invalidate()
    createJsonResult("success")
  }
}
