package model

import skinny.orm._
import feature._
import scalikejdbc._
import org.joda.time._
import util.CurrentDateUtil

case class AttachmentFile(
  id: Long,
  realFileName: String,
  mimeType: String,
  attachmentTargetType: String,
  fileSize: Long,
  width: Option[Long] = None,
  height: Option[Long] = None,
  thumbnailWidth: Option[Long] = None,
  thumbnailHeight: Option[Long] = None,
  lastUpdateAt: DateTime
)

object AttachmentFile extends SkinnyCRUDMapper[AttachmentFile] {
  override lazy val tableName = "attachment_file"
  override lazy val defaultAlias = createAlias("af")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[AttachmentFile]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[AttachmentFile]): AttachmentFile = new AttachmentFile(
    id = rs.get(rn.id),
    realFileName = rs.get(rn.realFileName),
    mimeType = rs.get(rn.mimeType),
    attachmentTargetType = rs.get(rn.attachmentTargetType),
    fileSize = rs.get(rn.fileSize),
    width = rs.get(rn.width),
    height = rs.get(rn.height),
    thumbnailWidth = rs.get(rn.thumbnailWidth),
    thumbnailHeight = rs.get(rn.thumbnailHeight),
    lastUpdateAt = rs.get(rn.lastUpdateAt)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: AttachmentFile)(implicit session: DBSession): Long = {
    AttachmentFile.createWithAttributes(
      'realFileName -> entity.realFileName,
      'mimeType -> entity.mimeType,
      'attachmentTargetType -> entity.attachmentTargetType,
      'fileSize -> entity.fileSize,
      'width -> entity.width,
      'height -> entity.height,
      'thumbnailWidth -> entity.thumbnailWidth,
      'thumbnailHeight -> entity.thumbnailHeight,
      'lastUpdateAt -> entity.lastUpdateAt
    )
  }

  /**
   * 削除.
   * @param id ID
   * @param session Session
   */
  def delete(id: Long)(implicit session: DBSession): Unit = {
    AttachmentFile.deleteById(id)
  }

}
