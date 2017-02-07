package controller

import skinny._
import skinny.controller.AssetsController
import skinny.micro.routing.Route

object Controllers {

  def mount(ctx: ServletContext): Unit = {
    root.mount(ctx)
    maintenanceInit.mount(ctx)
    login.mount(ctx)
    AssetsController.mount(ctx)
  }

  object root extends RootController with Routes {
    val indexUrl: Route = get("/?")(index).as('index)
  }

  object maintenanceInit extends _root_.controller.maintenance.InitController with Routes {
    val indexUrl: Route = get("/maintenance/?")(index).as('index)
  }

  object login extends _root_.controller.LoginController with Routes {
    val executeUrl: Route = post("/login/execute")(execute).as('execute)
  }

}
