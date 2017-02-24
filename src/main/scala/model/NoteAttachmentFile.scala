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

  /**
   * ふせんIDによる検索.
   * @param noteId ふせんID
   * @param session Session
   * @return 該当データ(_1: ふせん - 添付ファイル _2:添付ファイル
   */
  def findByNoteId(noteId: Long)(implicit session: DBSession): Seq[(NoteAttachmentFile, AttachmentFile)] = {

    val (naf, af) = (NoteAttachmentFile.defaultAlias, AttachmentFile.defaultAlias)
    withSQL {
      select.from(NoteAttachmentFile as naf)
        .innerJoin(AttachmentFile as af).on(naf.attachmentFileId, af.id)
        .where.eq(naf.noteId, noteId).orderBy(naf.id.asc)
    }.map { rs =>
      (NoteAttachmentFile.extract(rs, naf.resultName), AttachmentFile.extract(rs, af.resultName))
    }.list.apply()
  }

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteAttachmentFile)(implicit session: DBSession): Long = {
    NoteAttachmentFile.createWithAttributes(
      'noteId -> entity.noteId,
      'attachmentFileId -> entity.attachmentFileId
    )
  }

  /**
   * ふせんIDによる削除.
   * @param noteId ふせんID
   * @param session Session
   */
  def deleteByKanbanId(noteId: Long)(implicit session: DBSession): Long = {
    NoteAttachmentFile.deleteBy(
      sqls.eq(NoteAttachmentFile.column.noteId, noteId)
    )
  }

  /**
   * ふせんIDによる添付ファイル件数取得.
   * @param noteIds ふせんIDSeq
   * @param session Session
   * @return _1: ふせんID _2: 添付ファイル数 Seq
   */
  def findCountByNoteIds(noteIds: Seq[Long])(implicit session: DBSession): Seq[(Long, Long)] = {
    if (noteIds.isEmpty) Seq() else {
      sql"select note_id, count(*) from note_attachment_file where note_id in (${noteIds}) group by note_id".map { rs =>
        (rs.long(1), rs.long(2))
      }.list().apply()
    }
  }

}
