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
   * @return 該当データ(_1: かんばん - 添付ファイル _2:添付ファイル
   */
  def findByKanbanId(kanbanId: Long)(implicit session: DBSession): Seq[(KanbanAttachmentFile, AttachmentFile)] = {

    val (kaf, af) = (KanbanAttachmentFile.defaultAlias, AttachmentFile.defaultAlias)
    withSQL {
      select.from(KanbanAttachmentFile as kaf)
        .innerJoin(AttachmentFile as af).on(kaf.attachmentFileId, af.id)
        .where.eq(kaf.kanbanId, kanbanId).orderBy(kaf.id.asc)
    }.map { rs =>
      (KanbanAttachmentFile.extract(rs, kaf.resultName), AttachmentFile.extract(rs, af.resultName))
    }.list.apply()
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
