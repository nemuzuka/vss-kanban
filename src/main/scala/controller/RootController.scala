package controller

class RootController extends ApplicationController {

  override val loginCheck = false
  override val authentications = None

  def index: String = render("/root/index")
}
