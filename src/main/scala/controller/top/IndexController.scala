package controller.top

import controller.ApplicationController

class IndexController extends ApplicationController {

  override val authentications = None

  def index = render("/top/index")
}
