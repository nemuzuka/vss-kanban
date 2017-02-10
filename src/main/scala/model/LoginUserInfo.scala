package model

import skinny.orm._
import feature._
import scalikejdbc._
import org.joda.time._

case class LoginUserInfo(
  id: Long,
  loginId: String,
  userName: String,
  applicationAdminFlg: String,
  createAt: DateTime,
  lastUpdateAt: DateTime,
  lockVersion: Long
)

object LoginUserInfo extends SkinnyCRUDMapper[LoginUserInfo] with OptimisticLockWithVersionFeature[LoginUserInfo] {
  override lazy val tableName = "login_user_info"
  override lazy val defaultAlias = createAlias("lui")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserInfo]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserInfo]): LoginUserInfo = new LoginUserInfo(
    id = rs.get(rn.id),
    loginId = rs.get(rn.loginId),
    userName = rs.get(rn.userName),
    applicationAdminFlg = rs.get(rn.applicationAdminFlg),
    createAt = rs.get(rn.createAt),
    lastUpdateAt = rs.get(rn.lastUpdateAt),
    lockVersion = rs.get(rn.lockVersion)
  )

  /**
   * ログインIDによる取得.
   * @param loginId ログインID
   * @param session Session
   * @return 該当データ(存在しない場合、None)
   */
  def findByLoginId(loginId: String)(implicit session: DBSession): Option[LoginUserInfo] = {
    LoginUserInfo.where('loginId -> loginId).apply().headOption
  }

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: LoginUserInfo)(implicit session: DBSession): Long = {
    LoginUserInfo.createWithAttributes(
      'loginId -> entity.loginId,
      'userName -> entity.userName,
      'applicationAdminFlg -> entity.applicationAdminFlg,
      'createAt -> entity.createAt,
      'lastUpdateAt -> entity.lastUpdateAt,
      'lockVersion -> 1L
    )
  }

  /**
   * 更新.
   * @param entity 対象Entity
   * @param session Session
   */
  def update(entity: LoginUserInfo)(implicit session: DBSession): Unit = {
    LoginUserInfo.updateByIdAndVersion(entity.id, entity.lockVersion).withAttributes(
      'userName -> entity.userName,
      'applicationAdminFlg -> entity.applicationAdminFlg,
      'lastUpdateAt -> entity.lastUpdateAt
    )
  }

  /**
   * 登録されているユーザが存在するか?
   * @param session Session
   * @return 1件以上登録されている場合、true
   */
  def existsUser(implicit session: DBSession): Boolean = LoginUserInfo.findAllByWithLimitOffset(sqls"1 = 1", limit = 1, offset = 0).headOption exists { _ => true }

  /**
   * 全件取得.
   * @param session Session
   * @return 該当データ
   */
  def findAll(implicit session: DBSession): Seq[LoginUserInfo] = LoginUserInfo.findAll(Seq(defaultAlias.id.asc))
}
