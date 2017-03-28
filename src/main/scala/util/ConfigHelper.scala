package util

import skinny.SkinnyConfig

/**
 * 設定値を保持するHelper.
 */
object ConfigHelper {

  /** アプリケーションversion. */
  val appVersion = "1.0.1"

  //認証で使用する設定値
  /** pepper値. */
  val pepper: String = SkinnyConfig.stringConfigValue("security.pepper").getOrElse("hoge-pepper")
  /** ストレッチング回数. */
  val stretching: Int = SkinnyConfig.intConfigValue("security.stretching").getOrElse(500)
  /** solt用フォーマット文字列. */
  val soltFormat: String = SkinnyConfig.stringConfigValue("security.soltFormat").getOrElse("yyyyMMdd")
}
