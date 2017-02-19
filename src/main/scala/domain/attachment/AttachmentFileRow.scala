package domain.attachment

import domain.ValueObject

/**
 * AttachmentFileDto.
 *
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
case class AttachmentFileRow(
    attachmentFileId: Long,
    realFileName: String,
    mimeType: String,
    attachmentTargetType: String,
    fileSize: Long,
    width: Option[Long],
    height: Option[Long],
    thumbnailWidth: Option[Long],
    thumbnailHeight: Option[Long]
) extends ValueObject[AttachmentFileRow] {
  override def sameValueAs(other: AttachmentFileRow): Boolean = this.attachmentFileId == other.attachmentFileId
}
