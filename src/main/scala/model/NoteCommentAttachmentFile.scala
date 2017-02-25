package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteCommentAttachmentFile(
  id: Long,
  noteCommentId: Long,
  attachmentFileId: Long
)

object NoteCommentAttachmentFile extends SkinnyCRUDMapper[NoteCommentAttachmentFile] {
  override lazy val tableName = "note_comment_attachment_file"
  override lazy val defaultAlias = createAlias("ncaf")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteCommentAttachmentFile]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteCommentAttachmentFile]): NoteCommentAttachmentFile = new NoteCommentAttachmentFile(
    id = rs.get(rn.id),
    noteCommentId = rs.get(rn.noteCommentId),
    attachmentFileId = rs.get(rn.attachmentFileId)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteCommentAttachmentFile)(implicit session: DBSession): Long = {
    NoteCommentAttachmentFile.createWithAttributes(
      'noteCommentId -> entity.noteCommentId,
      'attachmentFileId -> entity.attachmentFileId
    )
  }
}
