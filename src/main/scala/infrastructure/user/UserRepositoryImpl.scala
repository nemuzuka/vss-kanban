package infrastructure.user

import domain.ApplicationException
import domain.user.{ User, UserAuthority, UserId, UserRepository }
import model.{ LoginUserAppendix, LoginUserInfo, LoginUserPassword }
import scalikejdbc.DBSession
import skinny.logging.Logger
import util.{ CurrentDateUtil, PasswordDigestUtil }

import scala.util.{ Failure, Success, Try }

/**
 * UserRepositoryの実装クラス.
 */
class UserRepositoryImpl extends UserRepository {

  private[this] val logger = Logger(this.getClass)

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
        applicationAdminFlg = user.authority.code,
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
      case Failure(e) =>
        logger.error(e.getMessage, e)
        Left(new ApplicationException("duplicate", Seq("loginId")))
    }
  }

  /**
   * @inheritdoc
   */
  override def findByIdWithPassword(userId: UserId, password: String)(implicit session: DBSession): Option[User] = {
    for (
      loginUserInfo <- LoginUserInfo.findById(userId.id);
      loginUserPassword <- LoginUserPassword.findById(loginUserInfo.id);
      digestPassword <- Option(PasswordDigestUtil.createHashPassword(password, loginUserInfo.createAt)) if digestPassword == loginUserPassword.password
    ) yield {
      createUser(loginUserInfo)
    }
  }

  /**
   * @inheritdoc
   */
  override def findById(userId: UserId)(implicit session: DBSession): Option[User] = {
    for (
      loginUserInfo <- LoginUserInfo.findById(userId.id)
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
        applicationAdminFlg = user.authority.code,
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
      case Failure(e) =>
        logger.error(e.getMessage, e)
        Left(new ApplicationException("invalidVersion", Seq()))
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
      case _ => Left(new ApplicationException("invalidVersion", Seq()))
    }
  }

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[User] = {
    LoginUserInfo.findAll map createUser
  }

  /**
   * @inheritdoc
   */
  override def updateSortNum(userIds: Seq[UserId])(implicit session: DBSession): Unit = userIds.zipWithIndex foreach (v => LoginUserAppendix.updateSortNum(v._1.id, v._2))

  /**
   * @inheritdoc
   */
  override def deleteAll()(implicit session: DBSession): Unit = throw new RuntimeException("呼んではいけません")

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
      authority = UserAuthority.withCode(loginUserInfo.applicationAdminFlg).get,
      lockVersion = loginUserInfo.lockVersion
    )
  }
}
