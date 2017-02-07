package controller

import domain.user.User
import skinny._
import skinny.filter._

/**
 * JSONを返すControllerの基底クラス
 */
trait ApiController extends SkinnyApiController with CommonControllerFeature {

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
}
