package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class Stage(
  id: Long,
  kanbanId: Long,
  stageName: String,
  archiveStatus: String,
  sortNum: Long,
  completeStage: String
)

object Stage extends SkinnyCRUDMapper[Stage] {
  override lazy val tableName = "stage"
  override lazy val defaultAlias: Alias[Stage] = createAlias("s")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Stage]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Stage]): Stage = new Stage(
    id = rs.get(rn.id),
    kanbanId = rs.get(rn.kanbanId),
    stageName = rs.get(rn.stageName),
    archiveStatus = rs.get(rn.archiveStatus),
    sortNum = rs.get(rn.sortNum),
    completeStage = rs.get(rn.completeStage)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: Stage)(implicit session: DBSession): Long = {
    Stage.createWithAttributes(
      'kanbanId -> entity.kanbanId,
      'stageName -> entity.stageName,
      'archiveStatus -> entity.archiveStatus,
      'sortNum -> entity.sortNum,
      'completeStage -> entity.completeStage
    )
  }

  /**
   * 更新.
   * @param entity 対象Entity
   * @param session Session
   * @return ID
   */
  def update(entity: Stage)(implicit session: DBSession): Long = {
    Stage.updateById(entity.id).withAttributes(
      'stageName -> entity.stageName,
      'archiveStatus -> entity.archiveStatus,
      'sortNum -> entity.sortNum,
      'completeStage -> entity.completeStage
    )
    entity.id
  }

  /**
   * かんばんIDによる取得.
   * ソート順でソートします
   * @param kanbanId かんばんID
   * @param includeArchive Archiveのステージも含める場合、true
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: Long, includeArchive: Boolean)(implicit session: DBSession): Seq[Stage] = {

    val l = Stage.defaultAlias
    withSQL {
      select.from(Stage as l)
        .where(sqls.toAndConditionOpt(
          Option(sqls"1 = 1"),
          if (includeArchive) None else Option(sqls.eq(l.archiveStatus, "0"))
        ))
        .and.eq(l.kanbanId, kanbanId)
        .orderBy(l.sortNum.asc, l.id.asc)
    }.map { rs =>
      Stage.extract(rs, l.resultName)
    }.list.apply()
  }
}
