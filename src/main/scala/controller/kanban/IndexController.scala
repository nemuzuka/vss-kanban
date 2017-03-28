package controller.kanban

import controller.ApplicationController

/**
 * かんばん直接指定のController.
 */
class IndexController extends ApplicationController {

  override val authentications = None

  def index: String = {
    set("kanbanId", params.getAs[Long]("kanbanId").getOrElse(-1L))
    set("noteId", params.getAs[Long]("noteId").getOrElse(-1L))
    render("/kanban/index")
  }
}
