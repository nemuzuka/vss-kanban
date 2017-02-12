package controller.kanban

import application.KanbanService
import controller.{ ApiController, Keys }
import domain.kanban.KanbanStatus
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
        createJsonResult(Result(kanbanId = kanbanId))

      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * validate & 入力パラメータ取得.
   *
   * @return Right:入力Form / Left:validateエラーメッセージ
   */
  private[this] def validateAndCreateForm: Either[Map[String, Seq[String]], Edit] = {
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
