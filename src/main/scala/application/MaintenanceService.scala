package application

import scalikejdbc.DBSession

/**
 * メンテナンス用のApplicationService.
 */
trait MaintenanceService {

  /**
   * 新規用管理者登録.
   * まだユーザが1件も登録されていない場合のみ
   * ログインID:admin@vss-kanban
   * パスワード:vss-kanban-admin
   * で管理者ユーザを登録します。
   * @param session Session
   */
  def createAdminUser()(implicit session: DBSession): Unit

}
