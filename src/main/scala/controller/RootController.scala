package controller

import domain.user.UserAuthority
import skinny._

class RootController extends ApplicationController {

  override val loginCheck = false
  override val authentications: Option[Seq[UserAuthority]] = None

  def index = render("/root/index")
}
