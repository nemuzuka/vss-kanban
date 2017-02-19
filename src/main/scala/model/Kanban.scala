package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class Kanban(
  id: Long,
  kanbanTitle: String,
  kanbanDescription: String,
  archiveStatus: String,
  createAt: DateTime,
  lastUpdateAt: DateTime,
  lockVersion: Long
)

object Kanban extends SkinnyCRUDMapper[Kanban] with OptimisticLockWithVersionFeature[Kanban] {
  override lazy val tableName = "kanban"
  override lazy val defaultAlias = createAlias("k")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Kanban]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Kanban]): Kanban = new Kanban(
    id = rs.get(rn.id),
    kanbanTitle = rs.get(rn.kanbanTitle),
    kanbanDescription = rs.get(rn.kanbanDescription),
    archiveStatus = rs.get(rn.archiveStatus),
    createAt = rs.get(rn.createAt),
    lastUpdateAt = rs.get(rn.lastUpdateAt),
    lockVersion = rs.get(rn.lockVersion)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: Kanban)(implicit session: DBSession): Long = {
    Kanban.createWithAttributes(
      'kanbanTitle -> entity.kanbanTitle,
      'kanbanDescription -> entity.kanbanDescription,
      'archiveStatus -> entity.archiveStatus,
      'createAt -> entity.createAt,
      'lastUpdateAt -> entity.lastUpdateAt,
      'lockVersion -> 1L
    )
  }

  /**
   * ユーザIDによるかんばん一覧取得.
   * 指定したユーザIDが担当者に含まれているかんばん一覧を取得します
   * @param loginUserInfoId ユーザID
   * @param viewArchiveKanban アーカイブ済みのかんばんも取得する場合、true
   * @param session Session
   */
  def findByloginUserInfoId(loginUserInfoId: Long, viewArchiveKanban: Boolean)(implicit session: DBSession): Seq[(Kanban, KanbanJoinedUser)] = {

    val (k, kju) = (Kanban.defaultAlias, KanbanJoinedUser.defaultAlias)

    withSQL {
      select.from(Kanban as k)
        .innerJoin(KanbanJoinedUser as kju).on(k.id, kju.kanbanId)
        .where(sqls.toAndConditionOpt(
          Option(sqls"1 = 1"),
          if (viewArchiveKanban) None else Option(sqls.eq(k.archiveStatus, "0"))
        ))
        .and.eq(kju.loginUserInfoId, loginUserInfoId)
        .orderBy(k.archiveStatus.desc, k.id.desc)
    }.map { rs =>
      (Kanban.extract(rs, k.resultName), KanbanJoinedUser.extract(rs, kju.resultName))
    }.list.apply()
  }

  /**
   * 指定ID以外のかんばん一覧取得.
   * 指定したかんばんID以外のかんばん一覧を取得します。
   * @param ids 除外かんばんIDSeq
   * @param viewArchiveKanban アーカイブ済みのかんばんも取得する場合、true
   * @param session Session
   */
  def findByNotExistsIds(ids: Seq[Long], viewArchiveKanban: Boolean)(implicit session: DBSession): Seq[Kanban] = {
    val k = Kanban.defaultAlias
    withSQL {
      select.from(Kanban as k)
        .where(sqls.toAndConditionOpt(
          Option(sqls"1 = 1"),
          if (viewArchiveKanban) None else Option(sqls.eq(k.archiveStatus, "0"))
        ))
        .and.notIn(k.id, ids)
        .orderBy(k.archiveStatus.desc, k.id.desc)
    }.map { rs =>
      Kanban.extract(rs, k.resultName)
    }.list.apply()
  }

}
