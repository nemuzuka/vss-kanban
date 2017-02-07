package util

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

import org.joda.time.DateTime

/**
 * パスワード暗号化の共通クラス.
 */
object PasswordDigestUtil {

  /**
   * Hash文字列作成.
   * @param passwordStr ユーザ入力パスワード文字列
   * @param timeStamp パスワード用日時
   * @return Hash化文字列
   */
  def createHashPassword(passwordStr: String, timeStamp: DateTime): String = {

    val passChars = passwordStr.toCharArray
    val solt = (timeStamp.toString(ConfigHelper.soltFormat) + ConfigHelper.pepper).getBytes
    val keySpec = new PBEKeySpec(passChars, solt, ConfigHelper.stretching, 256)
    val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
    val secretKey = skf.generateSecret(keySpec)
    secretKey.getEncoded.map("%02X" format _).mkString
  }
}
