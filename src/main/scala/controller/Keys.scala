package controller

/**
 * SessionやRequestパラメータのKey.
 */
object Keys {

  /**
   * SessionパラメータのAttributeのKey.
   */
  object Session {
    /** ログインユーザ情報格納Key. */
    val UserInfo = "USER_INFO"
  }

  /**
   * エラーメッセージ格納用
   */
  object ErrMsg {
    /** グローバル領域. */
    val Key = "global_message"
  }
}
