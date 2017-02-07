package application.impl

import javax.inject.Inject

import application.MaintenanceService
import domain.user.UserAuthority.ApplicationAdministrator
import domain.user.{ User, UserRepository }
import scalikejdbc.DBSession

/**
 * MaintenanceServiceの実装クラス.
 */
class MaintenanceServiceImpl @Inject() (
    userRepository: UserRepository
) extends MaintenanceService {

  /**
   * @inheritdoc
   */
  override def createAdminUser()(implicit session: DBSession): Unit = {

    if (!userRepository.existsUser) {
      val adminUser = User(
        userId = None,
        loginId = "admin@vss-kanban",
        name = "admin@vss-kanban",
        authority = ApplicationAdministrator
      )
      userRepository.create(adminUser, "vss-kanban-admin")
    }
  }
}
