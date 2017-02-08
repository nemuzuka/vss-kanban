package infrastructure.user

import domain.ApplicationException
import domain.user.{ User, UserAuthority, UserId, UserRepository }
import model.{ LoginUserInfo, LoginUserPassword }
import scalikejdbc.DBSession
import util.{ CurrentDateUtil, PasswordDigestUtil }

import scala.util.{ Success, Try }

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
   * @inheritdoc
   */
  override def create(user: User, password: String)(implicit session: DBSession): Either[ApplicationException, Long] = {

    Try {
      val now = CurrentDateUtil.nowDateTime
      val loginUserInfo = LoginUserInfo(
        id = -1L,
        loginId = user.loginId,
        userName = user.name,
        applicationAdminFlg = if (user.authority == UserAuthority.ApplicationAdministrator) "1" else "0",
        createAt = now,
        lastUpdateAt = now,
        lockVersion = -1L
      )
      val loginUserInfoId = LoginUserInfo.create(loginUserInfo)

      //パスワードを登録する
      val loginUserPassword = LoginUserPassword(
        loginUserInfoId = loginUserInfoId,
        password = PasswordDigestUtil.createHashPassword(password, now)
      )
      LoginUserPassword.create(loginUserPassword)
      loginUserInfoId
    } match {
      case Success(id) => Right(id)
      case _ => Left(new ApplicationException("error.duplicate", Seq("loginId")))
    }
  }

  /**
   * @inheritdoc
   */
  override def findByIdWithPassword(id: Long, password: String)(implicit session: DBSession): Option[User] = {
    for (
      loginUserInfo <- LoginUserInfo.findById(id);
      loginUserPassword <- LoginUserPassword.findById(loginUserInfo.id);
      digestPassword <- Option(PasswordDigestUtil.createHashPassword(password, loginUserInfo.createAt)) if digestPassword == loginUserPassword.password
    ) yield {
      createUser(loginUserInfo)
    }
  }

  /**
   * @inheritdoc
   */
  override def findById(id: Long)(implicit session: DBSession): Option[User] = {
    for (
      loginUserInfo <- LoginUserInfo.findById(id)
    ) yield {
      createUser(loginUserInfo)
    }
  }

  /**
   * @inheritdoc
   */
  override def update(user: User, password: Option[String])(implicit session: DBSession): Either[ApplicationException, Long] = {

    Try {
      val now = CurrentDateUtil.nowDateTime
      val loginUserInfo = LoginUserInfo(
        id = user.userId.get.id,
        loginId = user.loginId,
        userName = user.name,
        applicationAdminFlg = if (user.authority == UserAuthority.ApplicationAdministrator) "1" else "0",
        createAt = now,
        lastUpdateAt = now,
        lockVersion = user.lockVersion
      )
      LoginUserInfo.update(loginUserInfo)

      for (
        passwordStr <- password;
        loginUser <- LoginUserInfo.findById(loginUserInfo.id)
      ) yield {
        val passwordDigest = PasswordDigestUtil.createHashPassword(passwordStr, loginUser.createAt)
        LoginUserPassword.update(loginUserInfo.id, passwordDigest)
      }
      loginUserInfo.id
    } match {
      case Success(id) => Right(id)
      case _ => Left(new ApplicationException("error.invalidVersion", Seq()))
    }
  }

  /**
   * @inheritdoc
   */
  override def existsUser(implicit session: DBSession): Boolean = LoginUserInfo.existsUser

  /**
   * @inheritdoc
   */
  override def delete(userId: UserId, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long] = {
    Try {
      LoginUserInfo.deleteByIdAndVersion(userId.id, lockVersion)
      userId.id
    } match {
      case Success(id) => Right(id)
      case _ => Left(new ApplicationException("error.invalidVersion", Seq()))
    }
  }

  /**
   * @inheritdoc
   */
  override def findAll(): Seq[User] = throw new RuntimeException("呼んではいけません")

  /**
   * @inheritdoc
   */
  override def deleteAll(): Unit = throw new RuntimeException("呼んではいけません")

  /**
   * Userドメイン生成.
   * @param loginUserInfo ログインユーザ情報
   * @return Userドメイン
   */
  private def createUser(loginUserInfo: LoginUserInfo): User = {
    User(
      userId = Option(UserId(loginUserInfo.id)),
      loginId = loginUserInfo.loginId,
      name = loginUserInfo.userName,
      authority = if (loginUserInfo.applicationAdminFlg == "1") UserAuthority.ApplicationAdministrator else UserAuthority.Normal,
      lockVersion = loginUserInfo.lockVersion
    )
  }
}
