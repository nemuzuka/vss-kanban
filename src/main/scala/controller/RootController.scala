package controller

class RootController extends ApplicationController {

  override val loginCheck = false
  override val authentications = None

  def index = render("/root/index")
}
