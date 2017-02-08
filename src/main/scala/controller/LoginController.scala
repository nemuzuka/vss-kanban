package controller

import domain.user.{ UserAuthority, UserRepository }
import form.Login
import scalikejdbc.DB
import skinny.validator._

/**
 * ログイン用Controller.
 */
class LoginController extends ApiController {
  override val loginCheck = false
  override val authentications: Option[Seq[UserAuthority]] = None

  protectFromForgery()

  /**
   * ログイン実行.
   */
  def execute: String = {

    logger.debug(request.getParameterMap)

    validateAndParam match {
      case Right(form) =>
        val userRepository = injector.getInstance(classOf[UserRepository])
        DB localTx { implicit dbSession =>
          userRepository.findByLoginId(form.loginId, form.password) match {
            case Some(user) =>
              //成功時、Sessionを破棄して再作成
              session.invalidate()
              session += Keys.Session.UserInfo -> user
              createJsonResult("")
            case _ =>
              val errorMsgs = Map(Keys.ErrMsg.Key -> List(getLabel("error.login")))
              createJsonResult(errorMsgs)
          }
        }
      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * validate & 入力パラメータ取得.
   * @return Right:入力Form / Left:validateエラーメッセージ
   */
  private def validateAndParam: Either[Map[String, Seq[String]], Login] = {
    val validator = Validator(
      param("loginId" -> params("loginId")) is required,
      param("password" -> params("password")) is required
    )
    if (validator.hasErrors) Left(createValidateErrorMsg(validator)) else {
      Right(Login(
        loginId = params.getAs[String]("loginId").get,
        password = params.getAs[String]("password").get
      ))
    }
  }

}
