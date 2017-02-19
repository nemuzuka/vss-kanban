package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class KanbanAttachmentFile(
  id: Long,
  kanbanId: Long,
  attachmentFileId: Long
)

object KanbanAttachmentFile extends SkinnyCRUDMapper[KanbanAttachmentFile] {
  override lazy val tableName = "kanban_attachment_file"
  override lazy val defaultAlias = createAlias("kaf")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[KanbanAttachmentFile]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[KanbanAttachmentFile]): KanbanAttachmentFile = new KanbanAttachmentFile(
    id = rs.get(rn.id),
    kanbanId = rs.get(rn.kanbanId),
    attachmentFileId = rs.get(rn.attachmentFileId)
  )

  /**
   * かんばんIDによる検索.
   * @param kanbanId かんばんID
   * @param session Session
   * @return 該当データ
   */
  def findByKanbanId(kanbanId: Long)(implicit session: DBSession): Seq[KanbanAttachmentFile] = {
    KanbanAttachmentFile.where('kanbanId -> kanbanId).orderBy(defaultAlias.id.asc).apply()
  }

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: KanbanAttachmentFile)(implicit session: DBSession): Long = {
    KanbanAttachmentFile.createWithAttributes(
      'kanbanId -> entity.kanbanId,
      'attachmentFileId -> entity.attachmentFileId
    )
  }

  /**
   * かんばんIDによる削除.
   * @param kanbanId かんばんID
   * @param session Session
   */
  def deleteByKanbanId(kanbanId: Long)(implicit session: DBSession): Long = {
    KanbanAttachmentFile.deleteBy(
      sqls.eq(KanbanAttachmentFile.column.kanbanId, kanbanId)
    )
  }

}
