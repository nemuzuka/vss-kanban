package application

import domain.attachment.AttachmentTargetType
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
  def create(fileItems: Seq[FileItem], attachmentTargetType: AttachmentTargetType)(implicit session: DBSession): Seq[AttachmentFileDto]

  /**
   * AttachmentFileDto.
   * @param attachmentFileId 添付ファイルID
   * @param realFileName 実ファイル名
   * @param mimeType mimeType
   * @param attachmentTargetType 添付対象区分
   * @param fileSize ファイルサイズ
   * @param width 横幅
   * @param height 高さ
   * @param thumbnailWidth サムネイル横幅
   * @param thumbnailHeight サムネイル高さ
   */
  case class AttachmentFileDto(
    attachmentFileId: Long,
    realFileName: String,
    mimeType: String,
    attachmentTargetType: String,
    fileSize: Long,
    width: Option[Long],
    height: Option[Long],
    thumbnailWidth: Option[Long],
    thumbnailHeight: Option[Long]
  )
}
