package controller.attachment

import java.io.File

import controller.ApplicationController
import domain.attachment.{ AttachmentFileRepository, FileImageType }
import scalikejdbc.DB

/**
 * 添付ファイルダウンロード用Controller.
 */
class DownloadController extends ApplicationController {

  protectFromForgery()
  override val authentications = None

  /**
   * ダウンロード処理.
   */
  def execute: File = {
    val attachmentFileRepository = injector.getInstance(classOf[AttachmentFileRepository])
    val attachmentFileId: Long = params.getAs[Long]("attachmentFileId").getOrElse(-1)
    val fileImageType: FileImageType = FileImageType.withCode(params.getAs[String]("fileImageType").getOrElse("")).get

    val result = DB.localTx { implicit session =>
      for {
        attachmentFile <- attachmentFileRepository.findById(attachmentFileId)
        file <- attachmentFileRepository.findByAttachmentFileId(attachmentFileId, fileImageType)
      } yield {
        (attachmentFile, file)
      }
    }
    result match {
      case Some((attachmentFile, file)) =>
        writeHeader(attachmentFile.realFileName, attachmentFile.mimeType)
        file
      case _ =>
        writeHeader("noImage", "application/octet-stream")
        java.io.File.createTempFile("noImage-", ".tmp")
    }
  }
}
