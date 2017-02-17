package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteAttachmentFile(
  id: Long,
  noteId: Long,
  attachmentFileId: Long
)

object NoteAttachmentFile extends SkinnyCRUDMapper[NoteAttachmentFile] {
  override lazy val tableName = "note_attachment_file"
  override lazy val defaultAlias = createAlias("naf")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteAttachmentFile]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteAttachmentFile]): NoteAttachmentFile = new NoteAttachmentFile(
    id = rs.get(rn.id),
    noteId = rs.get(rn.noteId),
    attachmentFileId = rs.get(rn.attachmentFileId)
  )
}
