package infrastructure.user

import domain.user.{ User, UserAuthority, UserId, UserRepository }
import model.{ LoginUserInfo, LoginUserPassword }
import scalikejdbc.DBSession
import util.PasswordDigestUtil

/**
 * UserRepositoryの実装クラス.
 */
class UserRepositoryImpl extends UserRepository {

  /**
   * @inheritdoc
   */
  override def findByLoginId(loginId: String, password: String)(implicit session: DBSession): Option[User] = {
    for (
      loginUserInfo <- LoginUserInfo.findByLoginId(loginId);
      loginUserPassword <- LoginUserPassword.findById(loginUserInfo.id);
      digestPassword <- Option(PasswordDigestUtil.createHashPassword(password, loginUserInfo.createAt)) if digestPassword == loginUserPassword.password
    ) yield {
      createUser(loginUserInfo)
    }
  }

  /**
   * Userドメイン生成.
   * @param loginUserInfo ログインユーザ情報
   * @return Userドメイン
   */
  private def createUser(loginUserInfo: LoginUserInfo): User = {
    User(
      userId = Option(UserId(loginUserInfo.id)),
      name = loginUserInfo.userName,
      authority = if (loginUserInfo.applicationAdminFlg == "1") UserAuthority.ApplicationAdministrator else UserAuthority.Normal,
      lockVersion = loginUserInfo.lockVersion
    )
  }

}
