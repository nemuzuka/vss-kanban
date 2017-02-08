package controller

import domain.user.User
import skinny._
import skinny.validator.{ Messages, Validator }
import skinny.controller.feature.CSRFProtectionFeature

import scala.collection.mutable

/**
 * JSONを返すControllerの基底クラス
 */
trait ApiController extends SkinnyApiController with CommonControllerFeature with DiInjector with CSRFProtectionFeature {

  val messages: Messages = Messages.loadFromConfig()

  /*
   * Handles when unexpected exceptions are thrown from controllers.
   */
  addErrorFilter {
    case e: Throwable =>
      // For example, logs a exception and responds with status 500.
      logger.error(e.getMessage, e)
      haltWithBody(500)
  }

  //共通処理
  beforeAction() {
    contentType = "application/json"
    if (loginCheck) {
      //ログインチェックを行う場合のみ実施
      session.getAs[User](Keys.Session.UserInfo) match {
        case Some(v) =>
          //存在する場合、チェック対象権限Seqのチェックを行い、使用権限が無ければ、406のレスポンス
          if (!executeAuthenticationsCheck(v.authentications, authentications, authenticationCheck)) {
            logger.error(s"406 status loginId:${v.loginId}:name:${v.name}:path:${request.getRequestURL}")
            halt(406)
          }
        case _ =>
          //未ログインなので、400のレスポンス
          halt(400)
      }
    }
  }

  /**
   * JSON戻り値生成.
   * エラーが存在する際のメッセージを設定します。
   * @param errorMsg エラーメッセージMap
   * @return JSON形式の戻り値
   */
  def createJsonResult(errorMsg: Map[String, Seq[String]]): String = {
    toJSONString(JsonResult(
      msgs = Seq(),
      errorMsg = errorMsg,
      result = ""
    ), underscoreKeys = false)
  }

  /**
   * JSON戻り値生成.
   * @param result 戻り値
   * @return JSON形式の戻り値
   */
  def createJsonResult(result: AnyRef): String = {
    toJSONString(JsonResult(
      msgs = Seq(),
      errorMsg = Map(),
      result = result
    ), underscoreKeys = false)
  }

  /**
   * validateエラー時戻り値生成.
   * validator.hasErrors が true の場合に本処理を呼び出して下さい
   * @param validator エラーが存在するvalidate
   * @return エラーメッセージMap
   */
  def createValidateErrorMsg(validator: Validator): Map[String, Seq[String]] = {
    val map = mutable.Map[String, Seq[String]]()

    for ((key, errors) <- validator.errors.toMap()) {
      val label = getLabel(key)
      for (error <- errors) {
        messages.get(error.name, Seq(label) ++ error.messageParams) foreach { message => appendMessage(map, key, message) }
      }
    }
    map.toMap
  }

  /**
   * メッセージ追加.
   * @param map 対象Map
   * @param key キー文字列
   * @param message メッセージ
   */
  protected def appendMessage(map: mutable.Map[String, Seq[String]], key: String, message: String): Unit = {
    val seq = map.getOrElseUpdate(key, Seq()) ++ Seq(message)
    map += key -> seq
  }

  /**
   * label取得.
   * @param key key
   * @return 該当label(存在しない場合、key)
   */
  protected def getLabel(key: String): String = {
    skinny.I18n().get(key) getOrElse key
  }
}
