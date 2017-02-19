package application

import domain.attachment.{ AttachmentFileRow, AttachmentTargetType }
import scalikejdbc.DBSession
import skinny.micro.multipart.FileItem

/**
 * 添付ファイルに関するApplicationService.
 */
trait AttachmentFileService {

  /**
   * 登録.
   * 指定されたファイルを添付ファイルとして登録します。
   * この段階では、かんばんや付箋の情報とは紐付けません。
   * @param fileItems 対象ファイルSeq
   * @param attachmentTargetType 添付対象区分
   * @param session Session
   * @return AttachmentFileDtoSeq
   */
  def create(fileItems: Seq[FileItem], attachmentTargetType: AttachmentTargetType)(implicit session: DBSession): Seq[AttachmentFileRow]
}
