package domain.attachment

import java.io.File

import domain.{ ApplicationException, Repository }
import scalikejdbc.DBSession

/**
 * 添付ファイルに対するリポジトリ.
 */
trait AttachmentFileRepository extends Repository[AttachmentFile] {

  /**
   * 永続処理.
   * サムネイル用ファイルがNoneの場合、オリジナルのファイルのみ登録します
   * @param attachmentFile 添付ファイルドメイン
   * @param file 登録対象ファイル
   * @param thumbnail サムネイル用ファイル(存在しない場合、None)
   * @param session Session
   * @return 添付ファイルID
   */
  def create(attachmentFile: AttachmentFile, file: File, thumbnail: Option[File])(implicit session: DBSession): Long

  /**
   * 削除処理.
   * 添付ファイルIDに紐づく情報を削除します。
   * @param attachmentId 添付ファイルID
   * @param session Session
   */
  def delete(attachmentId: Long)(implicit session: DBSession): Unit

  /**
   * 添付ファイルIDとファイルイメージ区分によるFile取得.
   * @param attachmentFileId 添付ファイルID
   * @param fileImageType ファイルイメージ
   * @param session Session
   * @return 該当ファイル(存在しない場合、None)
   */
  def findByAttachmentFileId(attachmentFileId: Long, fileImageType: FileImageType)(implicit session: DBSession): Option[File]
}
