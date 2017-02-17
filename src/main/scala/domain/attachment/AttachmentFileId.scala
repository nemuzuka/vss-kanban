package domain.attachment

import domain.ValueObject

/**
 * 添付ファイルID
 * @param id ID値
 */
case class AttachmentFileId(
    id: Long
) extends ValueObject[AttachmentFileId] {
  override def sameValueAs(other: AttachmentFileId): Boolean = this.id == other.id
}
