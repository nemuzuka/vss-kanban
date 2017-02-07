package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class LoginUserPassword(
  loginUserInfoId: Long,
  password: String
)

object LoginUserPassword extends SkinnyCRUDMapper[LoginUserPassword] {
  override lazy val tableName = "login_user_password"
  override lazy val defaultAlias = createAlias("lup")
  override lazy val primaryKeyFieldName = "loginUserInfoId"

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserPassword]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserPassword]): LoginUserPassword = new LoginUserPassword(
    loginUserInfoId = rs.get(rn.loginUserInfoId),
    password = rs.get(rn.password)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   */
  def create(entity: LoginUserPassword)(implicit session: DBSession): Unit = {
    LoginUserPassword.createWithAttributes(
      'loginUserInfoId -> entity.loginUserInfoId,
      'password -> entity.password
    )
  }

  /**
   * 更新.
   * @param loginUserInfoId ログインユーザ情報ID
   * @param password 暗号化パスワード
   * @param session Session
   */
  def update(loginUserInfoId: Long, password: String)(implicit session: DBSession): Unit = {
    LoginUserPassword.updateById(loginUserInfoId).withAttributes(
      'password -> password
    )
  }
}
