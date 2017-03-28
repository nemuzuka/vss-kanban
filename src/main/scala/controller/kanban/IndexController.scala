package controller.kanban

import controller.ApplicationController

/**
 * かんばん直接指定のController.
 */
class IndexController extends ApplicationController {

  override val authentications = None

  /**
   * かんばんIDとふせんID指定
   */
  def index: String = {
    set("kanbanId", params.getAs[Long]("kanbanId").getOrElse(-1L))
    set("noteId", params.getAs[String]("noteId").getOrElse(""))
    render("/kanban/index")
  }

  /**
   * かんばんIDのみ指定
   */
  def kanbanOnly: String = {
    set("kanbanId", params.getAs[Long]("kanbanId").getOrElse(-1L))
    set("noteId", "")
    render("/kanban/index")
  }

}
