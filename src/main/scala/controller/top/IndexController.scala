package controller.top

import controller.ApplicationController

class IndexController extends ApplicationController {

  override val authentications = None

  def index: String = render("/top/index")
}
