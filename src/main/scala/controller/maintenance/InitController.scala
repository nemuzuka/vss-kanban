package controller.maintenance

import application.MaintenanceService
import controller.ApiController
import scalikejdbc.DB

/**
 * 初期データ作成用Controller.
 */
class InitController extends ApiController {

  override val loginCheck = false
  override val authentications = None

  /**
   * 初期データ作成.
   */
  def index = {
    DB.localTx { implicit session =>
      val maintenanceService = injector.getInstance(classOf[MaintenanceService])
      maintenanceService.createAdminUser()
    }
    createJsonResult("success")
  }

}
