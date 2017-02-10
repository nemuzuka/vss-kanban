package controller.user

import application.UserSerivce
import controller.{ ApiController, Keys }
import domain.user.UserAuthority
import form.user.Edit
import scalikejdbc.DB
import skinny.validator._

/**
 * ユーザ登録・変更・削除のController.
 */
class EditController extends ApiController {
  protectFromForgery()
  override val authentications = Option(Seq(UserAuthority.ApplicationAdministrator))

  /**
   * Form情報取得.
   */
  def detail: String = {
    DB localTx { implicit session =>
      val userService = injector.getInstance(classOf[UserSerivce])
      userService.getForm(params.getAs[Long]("userId"))
    } match {
      case Some(form) =>
        createJsonResult(form)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 登録・変更.
   */
  def store: String = {
    validateAndCreateForm match {
      case Right(form) =>

        import scalikejdbc.TxBoundary.Either._
        DB.localTx { implicit session =>
          val userService = injector.getInstance(classOf[UserSerivce])
          userService.store(form)
        } match {
          case Right(_) =>
            createJsonResult("success")
          case Left(exception) =>
            val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
            createJsonResult(errorMsg)
        }

      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * 削除.
   */
  def delete: String = {
    import scalikejdbc.TxBoundary.Either._
    DB.localTx { implicit session =>
      val userService = injector.getInstance(classOf[UserSerivce])
      userService.delete(params.getAs[Long]("userId").getOrElse(-1L), params.getAs[Long]("lockVersion").getOrElse(-1L))
    } match {
      case Right(_) =>
        createJsonResult("success")
      case Left(exception) =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
        createJsonResult(errorMsg)
    }
  }

  /**
   * validate & 入力パラメータ取得.
   * @return Right:入力Form / Left:validateエラーメッセージ
   */
  private[this] def validateAndCreateForm: Either[Map[String, Seq[String]], Edit] = {
    val validator = Validator(
      param("id" -> params("id")) is longValue,
      param("loginId" -> params("loginId")) is required & maxLength(256),
      param("password" -> params("password")) is minLength(6) & maxLength(256),
      param("passwordConfirm" -> params("passwordConfirm")) is minLength(6) & maxLength(256),
      param("userName" -> params("userName")) is required & maxLength(256),
      param("authority" -> params("authority")) is required & maxLength(1),
      param("lockVersion" -> params("lockVersion")) is longValue
    )
    if (validator.hasErrors) Left(createValidateErrorMsg(validator)) else {

      val id = params.getAs[String]("id").getOrElse("")
      val password = params.getAs[String]("password").getOrElse("")
      val passwordConfirm = params.getAs[String]("passwordConfirm").getOrElse("")

      val errorMsgOpt = if (id.isEmpty) {
        //新規の場合
        val customValidator = Validator(
          param("password" -> params("password")) is required,
          param("passwordConfirm" -> params("passwordConfirm")) is required
        )
        if (customValidator.hasErrors) Option(createValidateErrorMsg(customValidator)) else {
          if (password != passwordConfirm) {
            Option(createErrorMsg("password", "invalidPassword", Seq()))
          } else None
        }
      } else {
        //変更の場合
        if (!password.isEmpty || !passwordConfirm.isEmpty) {
          if (password.isEmpty) {
            Option(createErrorMsg("password", "required", Seq("password")))
          } else if (passwordConfirm.isEmpty) {
            Option(createErrorMsg("passwordConfirm", "required", Seq("passwordConfirm")))
          } else if (password != passwordConfirm) {
            Option(createErrorMsg("password", "invalidPassword", Seq()))
          } else None
        } else None
      }
      errorMsgOpt match {
        case Some(errorMsg) =>
          Left(errorMsg)
        case _ =>
          //エラーが無いので、Edit生成
          Right(Edit(
            id = params.getAs[String]("id").getOrElse(""),
            loginId = params.getAs[String]("loginId").getOrElse(""),
            password = params.getAs[String]("password").getOrElse(""),
            userName = params.getAs[String]("userName").getOrElse(""),
            authority = params.getAs[String]("authority").getOrElse(""),
            lockVersion = params.getAs[String]("lockVersion").getOrElse("")
          ))
      }

    }
  }
}
