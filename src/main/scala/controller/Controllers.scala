package controller

import skinny._
import skinny.controller.AssetsController
import skinny.micro.routing.Route

object Controllers {

  def mount(ctx: ServletContext): Unit = {
    root.mount(ctx)
    top.mount(ctx)
    user.mount(ctx)
    userListAjax.mount(ctx)
    userEditAjax.mount(ctx)
    kanbanEditAjax.mount(ctx)
    maintenanceInit.mount(ctx)
    login.mount(ctx)
    logout.mount(ctx)
    AssetsController.mount(ctx)
  }

  object root extends RootController with Routes {
    val indexUrl: Route = get("/?")(index).as('index)
  }

  object maintenanceInit extends _root_.controller.maintenance.InitController with Routes {
    val indexUrl: Route = get("/maintenance/?")(create).as('create)
  }

  object login extends _root_.controller.LoginController with Routes {
    val executeUrl: Route = post("/login")(execute).as('execute)
  }

  object logout extends _root_.controller.LogoutController with Routes {
    val logoutUrl: Route = get("/logout")(execute).as('execute)
  }

  object top extends _root_.controller.top.IndexController with Routes {
    val indexUrl: Route = get("/top/?")(index).as('index)
  }

  object user extends _root_.controller.user.IndexController with Routes {
    val indexUrl: Route = get("/user/?")(index).as('index)
  }

  object userListAjax extends _root_.controller.user.ListController with Routes {
    val allUrl: Route = get("/user/all")(all).as('all)
  }

  object userEditAjax extends _root_.controller.user.EditController with Routes {
    val detailUrl: Route = get("/user/detail")(detail).as('detail)
    val storeUrl: Route = post("/user/store")(store).as('store)
    val deleteUrl: Route = post("/user/delete")(delete).as('delete)
  }

  object kanbanEditAjax extends _root_.controller.kanban.EditController with Routes {
    val createUrl: Route = post("/kanban/create")(create).as('create)
  }
}
