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
}
