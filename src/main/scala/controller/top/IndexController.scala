package controller.top

import controller.ApplicationController

/**
 * TOP画面のController.
 */
class IndexController extends ApplicationController {

  override val authentications = None

  def index: String = render("/top/index")
}
