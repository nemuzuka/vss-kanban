package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class LoginUserAppendix(
  loginUserInfoId: Long,
  sortNum: Long
)

object LoginUserAppendix extends SkinnyCRUDMapper[LoginUserAppendix] {
  override lazy val tableName = "login_user_appendix"
  override lazy val defaultAlias = createAlias("lua")
  override lazy val primaryKeyFieldName = "loginUserInfoId"

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserAppendix]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[LoginUserAppendix]): LoginUserAppendix = new LoginUserAppendix(
    loginUserInfoId = rs.get(rn.loginUserInfoId),
    sortNum = rs.get(rn.sortNum)
  )

  /**
   * ソート順変更.
   * @param loginUserInfoId ログインユーザ情報ID
   * @param sortNum ソート順
   * @param session Session
   */
  def updateSortNum(loginUserInfoId: Long, sortNum: Long)(implicit session: DBSession): Unit = {
    LoginUserAppendix.updateById(loginUserInfoId).withAttributes(
      'sortNum -> sortNum
    )
  }
}
