package controller

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility
import org.apache.commons.codec.net.URLCodec

import domain.user.User
import skinny._
import skinny.filter._

/**
 * HTMLやファイルを返すControllerの基底クラス
 */
trait ApplicationController extends SkinnyController
    // with TxPerRequestFilter
    // with SkinnySessionFilter
    with ErrorPageFilter with CommonControllerFeature with DiInjector {

  addErrorFilter {
    case e: Throwable =>
      logger.error(e.getMessage, e)
      haltWithBody(500)
  }

  //共通処理
  beforeAction() {
    if (loginCheck) {
      //ログインチェックを行う場合のみ実施
      session.getAs[User](Keys.Session.UserInfo) match {
        case Some(v) =>
          //存在する場合、チェック対象権限Seqのチェックを行い、使用権限が無ければ、406の画面表示
          set("userInfo", v)
          if (!executeAuthenticationsCheck(v.authentications, authentications, authenticationCheck)) {
            logger.error(s"406 status loginId:${v.loginId}:name:${v.name}:path:${request.getRequestURL}")
            haltWithBody(406)
          }
        case _ =>
          //未ログインなので、400の画面表示
          haltWithBody(400)
      }
    }
  }

  /**
   * レスポンスヘッダ書き込み.
   * @param fileName ファイル名
   * @param mimeType MIME-TYPE
   */
  protected def writeHeader(fileName: String, mimeType: String): Unit = {

    val userAgent = request.getHeader("User-Agent") match {
      case "" | null =>
        request.getHeader("user-agent") match {
          case null => ""
          case s => s.toUpperCase
        }
      case s => s.toUpperCase
    }

    //ファイル名のエンコーディング
    val outputFileName = if (userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("TRIDENT") > -1 ||
      (userAgent.indexOf("WIN") > -1 && userAgent.indexOf("EDGE") > -1)) {
      //IEの場合
      val codec = new URLCodec()
      codec.encode(fileName, "UTF-8")
    } else if (userAgent.indexOf("SAFARI") > -1 || userAgent.indexOf("MAC") > -1) {
      //Safari、OSがMacの場合
      new String(fileName.getBytes("UTF-8"), "8859_1")
    } else {
      //それ以外
      MimeUtility.encodeWord(fileName, "ISO-2022-JP", "B")
    }

    contentType = mimeType
    response.setHeader("Content-Disposition", "attachment" + "; filename=" + outputFileName)
  }
}
