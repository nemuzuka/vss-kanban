package domain.attachment

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io._
import javax.imageio.ImageIO

import domain.{ Entity, Enum, EnumEntry }
import net.coobird.thumbnailator.Thumbnails
import org.apache.commons.io.{ FileUtils, IOUtils }
import skinny.micro.multipart.FileItem
import util.{ CurrentDateUtil, LoanSupport }

import scala.util.{ Success, Try }

/**
 * 添付ファイルドメイン.
 * @param attachmentFileId 添付ファイルID
 * @param realFileName 実ファイル名
 * @param mimeType MimeType
 * @param attachmentTargetType 添付対象区分
 * @param fileSize ファイルサイズ
 * @param width 横幅
 * @param height 高さ
 * @param thumbnailWidth 横幅(サムネイル)
 * @param thumbnailHeight 高さ(サムネイル)
 */
case class AttachmentFile(
    attachmentFileId: Option[AttachmentFileId],
    realFileName: String,
    mimeType: String,
    attachmentTargetType: AttachmentTargetType,
    fileSize: Long,
    width: Option[Long] = None,
    height: Option[Long] = None,
    thumbnailWidth: Option[Long] = None,
    thumbnailHeight: Option[Long] = None
) extends Entity[AttachmentFile] {
  /**
   * @inheritdoc
   */
  override def sameIdentityAs(other: AttachmentFile): Boolean = (for {
    thisId <- this.attachmentFileId
    otherId <- other.attachmentFileId
  } yield thisId.sameValueAs(otherId)) getOrElse false
}

/**
 * 添付ファイルに関するObject.
 */
object AttachmentFile extends LoanSupport {

  private[this] val ThumbnaiFileSize = 200

  /**
   * 添付ファイルドメイン生成.
   * @param fileItem ファイル
   * @param attachmentTargetType 添付対象区分
   * @return _1:添付ファイルドメイン _2:オリジナルファイル _3:サムネイルファイル
   */
  def createAttachmentFile(fileItem: FileItem, attachmentTargetType: AttachmentTargetType): (AttachmentFile, File, Option[File]) = {

    //基本ファイル情報書き込み
    val baseFile = java.io.File.createTempFile(CurrentDateUtil.nowDateTime.toString("yyyyMMddHHmmss-"), ".tmp")
    val is = fileItem.getInputStream
    FileUtils.copyInputStreamToFile(is, baseFile)

    val baseAttachmentFile = AttachmentFile(
      attachmentFileId = None,
      realFileName = fileItem.name,
      mimeType = fileItem.contentType.getOrElse("application/octet-stream"),
      attachmentTargetType = attachmentTargetType,
      fileSize = fileItem.size
    )

    //サムネイル作成
    createThumbnaiFile(baseFile, baseAttachmentFile) match {
      case Success((attachmentFile, file, thumbnaiFile)) =>
        (attachmentFile, file, Option(thumbnaiFile))
      case _ =>
        (baseAttachmentFile, baseFile, None)
    }
  }

  /**
   * サムネイル生成.
   * ベースファイルが画像ファイルの場合、サムネイルを作成します。
   * 画像ファイルでない場合、FailureとしてExceptionを返します。
   * オリジナルファイルが画像ファイルの場合、本ファイルは削除されます。戻り値のオリジナルファイルを使用して下さい
   * @param originalFile オリジナルファイル
   * @param baseAttachmentFile ベース添付ファイルドメイン
   * @return _1:サムネイル情報を設定した添付ファイルドメイン _2:オリジナルファイル(Exifを見て、画像を回転させたもの) _3:サムネイルファイル
   */
  private[this] def createThumbnaiFile(originalFile: File, baseAttachmentFile: AttachmentFile): Try[(AttachmentFile, File, File)] = {
    Try {
      val baseFile = executeRotatedMatrix(originalFile)
      val ext = getExt(baseFile)
      var image = createBufferedImage(baseFile)

      val width = image.getWidth
      val height = image.getHeight

      val target = if (width < height) height else width

      val (attachmentFile, thumbnaiFile) = if (target > ThumbnaiFileSize) {
        //指定サイズより大きい場合、圧縮
        val rate = ThumbnaiFileSize.toDouble / target.toDouble
        val thumbnailWidth = (width.toDouble * rate).toInt
        val thumbnailHeight = (height.toDouble * rate).toInt
        image = reduce(image, thumbnailWidth, thumbnailHeight)
        (baseAttachmentFile.copy(
          width = Option(width),
          height = Option(height),
          thumbnailWidth = Option(thumbnailWidth),
          thumbnailHeight = Option(thumbnailHeight)
        ), writeImage(image, ext))
      } else {
        //指定ファイルサイズより小さい場合、ファイルコピー
        val file = java.io.File.createTempFile(CurrentDateUtil.nowDateTime.toString("yyyyMMddHHmmss-"), ".tmp")
        FileUtils.copyFile(baseFile, file)
        (baseAttachmentFile.copy(
          width = Option(width),
          height = Option(height),
          thumbnailWidth = Option(width),
          thumbnailHeight = Option(height)
        ), file)
      }
      (attachmentFile, baseFile, thumbnaiFile)
    }
  }

  /**
   * 圧縮.
   * 指定した画像イメージの横幅と高さに圧縮します。
   * @param image 画像イメージ
   * @param dw 横幅
   * @param dh 高さ
   * @return 処理後画像イメージ
   */
  private[this] def reduce(image: BufferedImage, dw: Int, dh: Int): BufferedImage = {
    val thumb = new BufferedImage(dw, dh, image.getType)
    val g2d = thumb.createGraphics()
    g2d.setRenderingHint(
      RenderingHints.KEY_ALPHA_INTERPOLATION,
      RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_COLOR_RENDERING,
      RenderingHints.VALUE_COLOR_RENDER_QUALITY
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_DITHERING,
      RenderingHints.VALUE_DITHER_ENABLE
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY
    )
    g2d.setRenderingHint(
      RenderingHints.KEY_STROKE_CONTROL,
      RenderingHints.VALUE_STROKE_NORMALIZE
    )
    g2d.drawImage(image, 0, 0, dw, dh, null)
    thumb
  }

  /**
   * イメージデータ作成.
   * @param image 対象イメージデータ.
   * @param ext ファイル形式
   * @return イメージデータ
   */

  private[this] def writeImage(image: BufferedImage, ext: String): File = {
    val file = java.io.File.createTempFile(CurrentDateUtil.nowDateTime.toString("yyyyMMddHHmmss-"), ".tmp")
    using(new FileOutputStream(file)) { os =>
      val bos = new BufferedOutputStream(os)
      ImageIO.write(image, ext, bos)
      bos.flush()
      IOUtils.closeQuietly(bos)
    }
    executeRotatedMatrix(file)
  }

  /**
   * 画像回転.
   * Exifを見て、画像を回転させ、新しいfileを作成します.
   * @param file 対象ファイル
   * @return 回転後ファイル(画像ファイルでない場合、対象ファイルをそのまま返す)
   */
  private[this] def executeRotatedMatrix(file: java.io.File): java.io.File = {
    try {
      val tempFile = java.io.File.createTempFile(CurrentDateUtil.nowDateTime.toString("yyyyMMddHHmmss-"), ".tmp")
      using(new FileOutputStream(tempFile)) { os =>
        val bos = new BufferedOutputStream(os)
        Thumbnails.of(file).scale(1.0).toOutputStream(bos)
        IOUtils.closeQuietly(bos)
      }
      file.delete()
      tempFile
    } catch {
      case _: Throwable =>
        file
    }
  }

  /**
   * ファイルフォーマット名取得.
   * @param file 実ファイル.
   * @return ファイルフォーマット名
   */
  private[this] def getExt(file: File): String = {
    var formatName = ""
    using(new FileInputStream(file)) { is =>
      val iis = ImageIO.createImageInputStream(is)
      val readers = ImageIO.getImageReaders(iis).next()
      formatName = readers.getFormatName
      iis.close()
    }
    formatName
  }

  /**
   * BufferedImage情報取得.
   * @param file ファイル情報
   * @return BufferedImage情報
   */
  private[this] def createBufferedImage(file: File): BufferedImage = {
    var image: BufferedImage = null
    using(new FileInputStream(file)) { is =>
      val bio = new BufferedInputStream(is)
      image = ImageIO.read(bio)
    }
    image
  }
}

//添付対象区分
sealed abstract class AttachmentTargetType(override val code: String) extends EnumEntry
object AttachmentTargetType extends Enum[AttachmentTargetType] {
  /** かんばん. */
  case object Kanban extends AttachmentTargetType("1")
  /** 付箋. */
  case object Note extends AttachmentTargetType("2")
  protected val values = Seq(Kanban, Note)
}

//ファイルイメージ区分
sealed abstract class FileImageType(override val code: String) extends EnumEntry
object FileImageType extends Enum[FileImageType] {
  /** オリジナル. */
  case object Original extends FileImageType("1")
  /** サムネイル. */
  case object Thumbnail extends FileImageType("2")
  protected val values = Seq(Original, Thumbnail)
}

