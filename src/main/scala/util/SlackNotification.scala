package util

import skinny.http._
import skinny.json4s.JSONStringOps._

/**
  * Slackに対する通知.
  */
object SlackNotification {

  /**
    * 通知送信.
    * Incoming Webhooksの機能を使用して、
    * 引数のメッセージをSlackに表示します.
    * @param msg メッセージ.
    * @param url Slack通知用URL
    * @return 正常終了しなかった時のエラーメッセージ _1:HTTPステータスコード _2:エラーbody (正常終了時はNone)
    */
  def send(msg:String, url:String):Option[(Int, String)] = {
    val jsonStr = toJSONString(Param(text = msg))
    val request = Request(url).body(jsonStr.getBytes(HTTP.DEFAULT_CHARSET), "application/json")
    val response = HTTP.post(request)
    if(200 <= response.status && response.status <= 399) None else Option((response.status, response.textBody))
  }

  /**
    * リクエスト用パラメータ.
    * @param text text項目
    */
  case class Param(text:String)
}
