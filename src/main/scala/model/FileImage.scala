package model

import java.io.{ File, FileInputStream, InputStream }
import java.sql.{ PreparedStatement, ResultSet }

import org.apache.commons.io.FileUtils
import scalikejdbc._
import util.CurrentDateUtil

/**
 * ファイルイメージ.
 *
 * @param id ID
 * @param attachmentFileId 添付ファイルID
 * @param fileImageType ファイルイメージ区分
 * @param image イメージ
 */
case class FileImage(
  id: Long,
  attachmentFileId: Long,
  fileImageType: String,
  image: File
)

/**
 * ファイルイメージに対するDao部分
 */
object FileImage {

  /**
   * 登録.
   * byteaカラムの為、InputStreamを使用した登録になります
   * @param entity 対象Entity
   * @param session Session
   */
  def create(entity: FileImage)(implicit session: DBSession): Unit = {
    val in = new FileInputStream(entity.image)
    try {
      val v = ParameterBinder(
        value = in,
        binder = (stmt: PreparedStatement, idx: Int) => {
        stmt.setBinaryStream(idx, in, entity.image.length)
      }
      )
      sql"insert into file_image (attachment_file_id, file_image_type, image) values (${entity.attachmentFileId}, ${entity.fileImageType}, ${v})".update().apply()
    } finally {
      in.close()
    }
  }

  /**
   * 添付ファイルIDとファイルイメージ区分によるファイル取得.
   * @param attachmentFileId 添付ファイルID
   * @param fileImageType ファイルイメージ区分
   * @param session Session
   * @return ファイル(存在しない場合、None)
   */
  def findByParams(attachmentFileId: Long, fileImageType: String)(implicit session: DBSession): Option[File] = {

    //InputStreamからFileを作成するTypeBinder
    implicit val fileTypeBinder: TypeBinder[File] = new TypeBinder[File] {
      override def apply(rs: ResultSet, columnIndex: Int): File = {
        createFile(rs.getBinaryStream(columnIndex))
      }
      override def apply(rs: ResultSet, label: String): File = {
        createFile(rs.getBinaryStream(label))
      }
      /**
       * ファイル作成.
       * InputStreamを元にファイルを生成します。
       * @param is InputStream
       * @return ファイル
       */
      def createFile(is: InputStream): File = {
        val file = File.createTempFile(CurrentDateUtil.nowDateTime.toString("yyyyMMddHHmmss-"), ".tmp")
        FileUtils.copyInputStreamToFile(is, file)
        file
      }
    }
    sql"select image from file_image as image where attachment_file_id = ${attachmentFileId} and file_image_type = ${fileImageType}".map(_.get[File]("image")).first().apply()

  }
}