package controller.kanban

import application.KanbanService
import controller.{ ApiController, Keys }
import domain.kanban.{ KanbanRepository, KanbanStatus }
import domain.user.User
import form.kanban.Edit
import scalikejdbc.DB
import skinny.validator._

/**
 * かんばん登録・変更・削除のController.
 */
class EditController extends ApiController {
  protectFromForgery()
  override val authentications = None

  /**
   * 詳細情報取得.
   */
  def detail: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val includeArchive = params.getAs[Boolean]("includeArchive").getOrElse(false)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val kanbanService = injector.getInstance(classOf[KanbanService])
      kanbanService.findById(kanbanId, includeArchive, userInfo)
    } match {
      case Some(detail) =>
        createJsonResult(detail)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 登録.
   */
  def create: String = {
    validateAndCreateForm match {
      case Right(form) =>
        val userInfo = session.getAs[User](Keys.Session.UserInfo).get
        val kanbanId = DB.localTx { implicit session =>
          val kanbanService = injector.getInstance(classOf[KanbanService])
          kanbanService.create(form.kanbanTitle, form.kanbanDescription, userInfo.userId.get.id)
        }
        createJsonResult("success", Result(kanbanId = kanbanId))

      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * 添付ファイル紐付け.
   */
  def attachmentFile: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val attachmentFileIds = multiParams.getAs[Long]("attachmentFileIds").getOrElse(Seq())
    DB.localTx { implicit session =>
      val kanbanRepository = injector.getInstance(classOf[KanbanRepository])
      kanbanRepository.storeKanbanAttachmentFile(kanbanId, attachmentFileIds)
    }
    createJsonResult("success")
  }

  /**
   * validate & 入力パラメータ取得.
   *
   * @return Right:入力Form / Left:validateエラーメッセージ
   */
  protected def validateAndCreateForm: Either[Map[String, Seq[String]], Edit] = {
    val validator = Validator(
      param("id" -> params("id")) is longValue,
      param("kanbanTitle" -> params("kanbanTitle")) is required & maxLength(256),
      param("lockVersion" -> params("lockVersion")) is longValue
    )
    if (validator.hasErrors) Left(createValidateErrorMsg(validator)) else {
      Right(Edit(
        id = params.getAs[String]("id").getOrElse(""),
        kanbanTitle = params.getAs[String]("kanbanTitle").getOrElse(""),
        kanbanDescription = params.getAs[String]("kanbanDescription").getOrElse(""),
        archiveStatus = params.getAs[String]("archiveStatus").getOrElse(KanbanStatus.Open.code),
        lockVersion = params.getAs[String]("lockVersion").getOrElse("0")
      ))
    }
  }

  /**
   * JSON戻り値.
   * @param kanbanId かんばんID
   */
  case class Result(kanbanId: Long)
}
