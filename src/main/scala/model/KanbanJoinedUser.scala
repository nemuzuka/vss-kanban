package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class KanbanJoinedUser(
  id: Long,
  kanbanId: Long,
  loginUserInfoId: Long,
  kanbanAuthority: String
)

object KanbanJoinedUser extends SkinnyCRUDMapper[KanbanJoinedUser] {
  override lazy val tableName = "kanban_joined_user"
  override lazy val defaultAlias = createAlias("kju")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[KanbanJoinedUser]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[KanbanJoinedUser]): KanbanJoinedUser = new KanbanJoinedUser(
    id = rs.get(rn.id),
    kanbanId = rs.get(rn.kanbanId),
    loginUserInfoId = rs.get(rn.loginUserInfoId),
    kanbanAuthority = rs.get(rn.kanbanAuthority)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: KanbanJoinedUser)(implicit session: DBSession): Long = {
    KanbanJoinedUser.createWithAttributes(
      'kanbanId -> entity.kanbanId,
      'loginUserInfoId -> entity.loginUserInfoId,
      'kanbanAuthority -> entity.kanbanAuthority
    )
  }

  /**
   * かんばんIDによる取得.
   * ソート順は、ソート順 asc, id ascです
   * @param kanbanId かんばんID
   * @param session Session
   * @return 該当データ(_1: LoginUserInfo, _2:KanbanJoinedUser)
   */
  def findByKanbanId(kanbanId: Long)(implicit session: DBSession): Seq[(LoginUserInfo, KanbanJoinedUser)] = {
    val (lui, kju, lua) = (LoginUserInfo.defaultAlias, KanbanJoinedUser.defaultAlias, LoginUserAppendix.defaultAlias)
    withSQL {
      select.from(LoginUserInfo as lui)
        .innerJoin(LoginUserAppendix as lua).on(lui.id, lua.loginUserInfoId)
        .innerJoin(KanbanJoinedUser as kju).on(lui.id, kju.loginUserInfoId)
        .where.eq(kju.kanbanId, kanbanId).orderBy(lua.sortNum.asc, kju.id.asc)
    }.map { rs =>
      (LoginUserInfo.extract(rs, lui.resultName), KanbanJoinedUser.extract(rs, kju.resultName))
    }.list.apply()
  }

  /**
   * かんばんIDによる削除.
   * @param kanbanId かんばんID
   * @param session Session
   */
  def deleteByKanbanId(kanbanId: Long)(implicit session: DBSession): Unit = {
    KanbanJoinedUser.deleteBy(
      sqls.eq(KanbanJoinedUser.column.kanbanId, kanbanId)
    )
  }

}
