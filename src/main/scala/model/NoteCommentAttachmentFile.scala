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

  /**
   * ふせんコメントIDによる検索.
   * @param noteCommentIds ふせんコメントIDSeq
   * @param session Session
   * @return 該当データ(_1: ふせんコメント - 添付ファイル _2:添付ファイル
   */
  def findByNoteId(noteCommentIds: Seq[Long])(implicit session: DBSession): Seq[(NoteCommentAttachmentFile, AttachmentFile)] = {

    if (noteCommentIds.isEmpty) Seq() else {
      val (ncaf, af) = (NoteCommentAttachmentFile.defaultAlias, AttachmentFile.defaultAlias)
      withSQL {
        select.from(NoteCommentAttachmentFile as ncaf)
          .innerJoin(AttachmentFile as af).on(ncaf.attachmentFileId, af.id)
          .where.in(ncaf.noteCommentId, noteCommentIds).orderBy(ncaf.id.asc)
      }.map { rs =>
        (NoteCommentAttachmentFile.extract(rs, ncaf.resultName), AttachmentFile.extract(rs, af.resultName))
      }.list.apply()
    }
  }

}
