package controller

import skinny._
import skinny.controller.AssetsController
import skinny.micro.context.RichServletContext
import skinny.micro.routing.Route

object Controllers {

  def mount(ctx: ServletContext): Unit = {

    RichServletContext(ctx).mount(attachmentFileAjax, "/attachment/file")

    root.mount(ctx)
    top.mount(ctx)
    user.mount(ctx)
    kanban.mount(ctx)
    notification.mount(ctx)
    userListAjax.mount(ctx)
    userEditAjax.mount(ctx)
    kanbanEditAjax.mount(ctx)
    kanbanAdminEditAjax.mount(ctx)
    noteEditAjax.mount(ctx)
    kanbanListAjax.mount(ctx)
    notificationAjax.mount(ctx)
    maintenanceInit.mount(ctx)
    login.mount(ctx)
    logout.mount(ctx)
    attachmentFileDownload.mount(ctx)
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
    val updateSortNumUrl: Route = post("/user/update/sort")(updateSortNum()).as('updateSortNum)
  }

  object kanban extends _root_.controller.kanban.IndexController with Routes {
    val indexUrl: Route = get("/d/kanban/:kanbanId/note/:noteId")(index).as('index)
    val kanbanOnlyUrl: Route = get("/d/kanban/:kanbanId")(kanbanOnly).as('kanbanOnly)
  }

  object kanbanEditAjax extends _root_.controller.kanban.EditController with Routes {
    val detailUrl: Route = get("/kanban/detail")(detail).as('detail)
    val createUrl: Route = post("/kanban/create")(create).as('create)
    val attachmentUrl: Route = post("/kanban/attachmentFile")(attachmentFile).as('attachmentFile)
  }

  object kanbanAdminEditAjax extends _root_.controller.kanban.admin.EditController with Routes {
    val baseUrl: Route = get("/kanban/admin/base")(base).as('base)
    val updateBaseUrl: Route = post("/kanban/admin/updateBase")(updateBase()).as('updateBase)
    val deleteUrl: Route = post("/kanban/admin/delete")(delete).as('delete)
    val joinedUsersUrl: Route = get("/kanban/admin/joinedUsers")(joinedUsers).as('joinedUsers)
    val updateJoinedUsersUrl: Route = post("/kanban/admin/updateJoinedUsers")(updateJoinedUsers()).as('updateJoinedUsers)
    val stagesUrl: Route = get("/kanban/admin/stages")(stages).as('stages)
    val updateStagesUrl: Route = post("/kanban/admin/updateStages")(updateStages()).as('updateStages)
  }

  object noteEditAjax extends _root_.controller.kanban.NoteEditController with Routes {
    val formUrl: Route = get("/kanban/note/form")(form).as('form)
    val storeUrl: Route = post("/kanban/note/store")(store).as('store)
    val detailUrl: Route = get("/kanban/note/detail")(detail).as('detail)
    val deleteUrl: Route = post("/kanban/note/delete")(delete).as('delete)
    val commentStoreUrl: Route = post("/kanban/note/comment/store")(commentStore()).as('commentStore)
    val moveUrl: Route = post("/kanban/note/move")(move).as('move)

    val watchUrl: Route = post("/kanban/note/:noteId/watch")(watch).as('watch)
    val unwatchUrl: Route = post("/kanban/note/:noteId/unwatch")(unwatch).as('unwatch)

    val stageDetailUrl: Route = get("/kanban/:kanbanId/stage/:noteId")(stageDetail).as('stageDetail)
  }

  object kanbanListAjax extends _root_.controller.kanban.ListConstoller with Routes {
    val listUrl: Route = get("/kanban/list")(list).as('list)
  }

  object attachmentFileAjax extends _root_.controller.attachment.UploadController with Routes {
    val kanbanUrl: Route = post("/kanban")(kanban).as('kanban)
  }

  object attachmentFileDownload extends _root_.controller.attachment.DownloadController with Routes {
    val executeUrl: Route = get("/attachment/dl")(execute).as('execute)
    val executePostUrl: Route = post("/attachment/dl")(execute).as('execute)
  }

  object notificationAjax extends _root_.controller.kanban.NotificationController with Routes {
    val hasUnreadUrl: Route = get("/notification/hasUnread")(hasUnread).as('hasUnread)
    val listUrl: Route = get("/notification/list")(list).as('list)
    val allReadUrl: Route = post("/notification/allRead")(allRead).as('allRead)
  }

  object notification extends _root_.controller.notification.IndexController with Routes {
    val indexUrl: Route = get("/notification/")(index).as('index)
  }

}
