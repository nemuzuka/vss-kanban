package application.impl

import javax.inject.Inject

import application.AttachmentFileService
import domain.attachment.{ AttachmentFile, AttachmentFileRow, AttachmentFileRepository, AttachmentTargetType }
import scalikejdbc.DBSession
import skinny.micro.multipart.FileItem

/**
 * AttachmentFileServiceの実装クラス.
 */
class AttachmentFileServiceImpl @Inject() (
    attachmentFileRepository: AttachmentFileRepository
) extends AttachmentFileService {

  /**
   * @inheritdoc
   */
  override def create(fileItems: Seq[FileItem], attachmentTargetType: AttachmentTargetType)(implicit session: DBSession): Seq[AttachmentFileRow] = {
    fileItems.filter(p => p.size > 0) map { v =>
      createAttachmentFile(v, attachmentTargetType)
    }
  }

  /**
   * AttachmentFile登録.
   * 登録後、AttachmentFileDtoを生成して返却します。
   * @param fileItem 対象ファイル情報
   * @param attachmentTargetType 添付対象区分
   * @param session Session
   * @return AttachmentFileDto
   */
  private[this] def createAttachmentFile(fileItem: FileItem, attachmentTargetType: AttachmentTargetType)(implicit session: DBSession): AttachmentFileRow = {
    val (attachmentFile, file, thumbnail) = AttachmentFile.createAttachmentFile(fileItem, attachmentTargetType)
    val attachmentFileId = attachmentFileRepository.create(attachmentFile, file, thumbnail)
    AttachmentFile.createAttachmentFileRow(attachmentFileId, attachmentFile)
  }

}
