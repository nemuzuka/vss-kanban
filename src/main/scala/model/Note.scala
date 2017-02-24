package model

import skinny.orm._
import feature._
import scalikejdbc._
import org.joda.time._

case class Note(
  id: Long,
  laneId: Long,
  kanbanId: Long,
  noteTitle: String,
  noteDescription: String,
  fixDate: Option[LocalDate] = None,
  sortNum: Long,
  archiveStatus: String,
  createLoginUserInfoId: Long,
  createAt: DateTime,
  lastUpdateLoginUserInfoId: Long,
  lastUpdateAt: DateTime,
  lockVersion: Long
)

object Note extends SkinnyCRUDMapper[Note] with OptimisticLockWithVersionFeature[Note] {
  override lazy val tableName = "note"
  override lazy val defaultAlias = createAlias("n")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[Note]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[Note]): Note = new Note(
    id = rs.get(rn.id),
    laneId = rs.get(rn.laneId),
    kanbanId = rs.get(rn.kanbanId),
    noteTitle = rs.get(rn.noteTitle),
    noteDescription = rs.get(rn.noteDescription),
    fixDate = rs.get(rn.fixDate),
    sortNum = rs.get(rn.sortNum),
    archiveStatus = rs.get(rn.archiveStatus),
    createLoginUserInfoId = rs.get(rn.createLoginUserInfoId),
    createAt = rs.get(rn.createAt),
    lastUpdateLoginUserInfoId = rs.get(rn.lastUpdateLoginUserInfoId),
    lastUpdateAt = rs.get(rn.lastUpdateAt),
    lockVersion = rs.get(rn.lockVersion)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: Note)(implicit session: DBSession): Long = {
    Note.createWithAttributes(
      'laneId -> entity.laneId,
      'kanbanId -> entity.kanbanId,
      'noteTitle -> entity.noteTitle,
      'noteDescription -> entity.noteDescription,
      'fixDate -> entity.fixDate,
      'sortNum -> Long.MaxValue,
      'archiveStatus -> entity.archiveStatus,
      'createLoginUserInfoId -> entity.createLoginUserInfoId,
      'createAt -> entity.createAt,
      'lastUpdateLoginUserInfoId -> entity.lastUpdateLoginUserInfoId,
      'lastUpdateAt -> entity.lastUpdateAt,
      'lockVersion -> entity.lockVersion
    )
  }

  /**
   * かんばんIDによる取得.
   * ソート順でソートします
   * @param kanbanId かんばんID
   * @param includeArchive Archiveのレーンも含める場合、true
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: Long, includeArchive: Boolean)(implicit session: DBSession): Seq[Note] = {

    val n = Note.defaultAlias
    withSQL {
      select.from(Note as n)
        .where(sqls.toAndConditionOpt(
          Option(sqls"1 = 1"),
          if (includeArchive) None else Option(sqls.eq(n.archiveStatus, "0"))
        ))
        .and.eq(n.kanbanId, kanbanId)
        .orderBy(n.sortNum.asc, n.id.desc)
    }.map { rs =>
      Note.extract(rs, n.resultName)
    }.list.apply()
  }

}
