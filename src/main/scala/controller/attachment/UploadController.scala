package controller.attachment

import application.AttachmentFileService
import controller.JsonResult
import domain.attachment.AttachmentTargetType
import scalikejdbc.DB

/**
 * 添付ファイルアップロードController.
 */
class UploadController extends controller.UploadController {

  protectFromForgery()

  /**
   * かんばん用アップロード.
   */
  def kanban: String = {
    upload(AttachmentTargetType.Kanban)
  }

  /**
   * アップロードメイン処理.
   * @param attachmentTargetType 添付対象区分
   */
  private[this] def upload(attachmentTargetType: AttachmentTargetType) = {
    val attachmentFileService = injector.getInstance(classOf[AttachmentFileService])

    val result = DB.localTx { implicit session =>
      for (
        files <- fileMultiParams.get("attachmentFiles")
      ) yield {
        attachmentFileService.create(files, AttachmentTargetType.Kanban)
      }
    }
    toJSONString(JsonResult(
      msgs = Seq(),
      errorMsg = Map(),
      result = result.getOrElse(Seq())
    ), underscoreKeys = false)
  }

}
