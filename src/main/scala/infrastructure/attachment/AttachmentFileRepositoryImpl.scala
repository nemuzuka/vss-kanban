package infrastructure.attachment

import java.io.File

import domain.attachment._
import model.FileImage
import scalikejdbc.DBSession
import util.CurrentDateUtil

/**
 * AttachmentFileRepositoryの実装クラス.
 */
class AttachmentFileRepositoryImpl extends AttachmentFileRepository {

  /**
   * @inheritdoc
   */
  override def create(attachmentFile: AttachmentFile, file: File, thumbnail: Option[File])(implicit session: DBSession): Long = {
    val attachmentFileId = model.AttachmentFile.create(model.AttachmentFile(
      id = -1L,
      realFileName = attachmentFile.realFileName,
      mimeType = attachmentFile.mimeType,
      attachmentTargetType = attachmentFile.attachmentTargetType.code,
      fileSize = attachmentFile.fileSize,
      width = attachmentFile.width,
      height = attachmentFile.height,
      thumbnailWidth = attachmentFile.thumbnailWidth,
      thumbnailHeight = attachmentFile.thumbnailHeight,
      lastUpdateAt = CurrentDateUtil.nowDateTime
    ))

    //オリジナルのファイルを登録
    FileImage.create(FileImage(
      id = -1L,
      attachmentFileId = attachmentFileId,
      fileImageType = FileImageType.Original.code,
      image = file
    ))
    //サムネイルのファイルを登録
    thumbnail foreach (v =>
      FileImage.create(FileImage(
        id = -1L,
        attachmentFileId = attachmentFileId,
        fileImageType = FileImageType.Thumbnail.code,
        image = v
      )))

    attachmentFileId
  }

  /**
   * @inheritdoc
   */
  override def delete(attachmentId: Long)(implicit session: DBSession): Unit = {
    model.AttachmentFile.delete(attachmentId)
  }

  /**
   * @inheritdoc
   */
  override def findByAttachmentFileId(attachmentFileId: Long, fileImageType: FileImageType)(implicit session: DBSession): Option[File] = {
    FileImage.findByParams(attachmentFileId, fileImageType.code)
  }

  /**
   * @inheritdoc
   */
  override def findById(attachmentFileId: Long)(implicit session: DBSession): Option[AttachmentFile] = {
    for (
      attachmentFile <- model.AttachmentFile.findById(attachmentFileId)
    ) yield {
      AttachmentFile(
        attachmentFileId = Option(AttachmentFileId(attachmentFile.id)),
        realFileName = attachmentFile.realFileName,
        mimeType = attachmentFile.mimeType,
        attachmentTargetType = AttachmentTargetType.withCode(attachmentFile.attachmentTargetType).get,
        fileSize = attachmentFile.fileSize,
        width = attachmentFile.width,
        height = attachmentFile.height,
        thumbnailWidth = attachmentFile.thumbnailWidth,
        thumbnailHeight = attachmentFile.thumbnailHeight
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[AttachmentFile] = throw new RuntimeException("呼んではいけません")

  /**
   * @inheritdoc
   */
  override def deleteAll()(implicit session: DBSession): Unit = throw new RuntimeException("呼んではいけません")
}
