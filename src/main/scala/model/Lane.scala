package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class Lane(
  id: Long,
  kanbanId: Long,
  laneName: String,
  archiveStatus: String,
  sortNum: Long,
  completeLane: String
)

object Lane extends SkinnyCRUDMapper[Lane] {
  override lazy val tableName = "lane"
  override lazy val defaultAlias: Alias[Lane] = createAlias("l")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Lane]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Lane]): Lane = new Lane(
    id = rs.get(rn.id),
    kanbanId = rs.get(rn.kanbanId),
    laneName = rs.get(rn.laneName),
    archiveStatus = rs.get(rn.archiveStatus),
    sortNum = rs.get(rn.sortNum),
    completeLane = rs.get(rn.completeLane)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: Lane)(implicit session: DBSession): Long = {
    Lane.createWithAttributes(
      'kanbanId -> entity.kanbanId,
      'laneName -> entity.laneName,
      'archiveStatus -> entity.archiveStatus,
      'sortNum -> entity.sortNum,
      'completeLane -> entity.completeLane
    )
  }

  /**
   * 更新.
   * @param entity 対象Entity
   * @param session Session
   * @return ID
   */
  def update(entity: Lane)(implicit session: DBSession): Long = {
    Lane.updateById(entity.id).withAttributes(
      'laneName -> entity.laneName,
      'archiveStatus -> entity.archiveStatus,
      'sortNum -> entity.sortNum,
      'completeLane -> entity.completeLane
    )
    entity.id
  }

  /**
   * かんばんIDによる取得.
   * ソート順でソートします
   * @param kanbanId かんばんID
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: Long)(implicit session: DBSession): Seq[Lane] = {
    Lane.where('kanbanId -> kanbanId).orderBy(defaultAlias.sortNum.asc, defaultAlias.id.asc).apply()
  }
}
