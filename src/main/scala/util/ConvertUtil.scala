package util

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import skinny.logging.Logger

/**
 * 各種変換用Util.
 */
object ConvertUtil {

  private[this] val logger = Logger(this.getClass)

  /**
   * 日付変更.
   * @param org 日付文字列(yyyy/MM/dd yyyy-MM-dd yyyyMMdd形式)
   * @return 日付データ(空文字の場合、None)
   */
  def toDate(org: String): Option[LocalDate] = {
    if (org.isEmpty) None else {
      val str = org.replaceAll("/", "").replaceAll("-", "")
      try {
        Option(DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate(str))
      } catch {
        case e: Throwable =>
          logger.error(e.getMessage, e)
          None
      }
    }
  }
}
