package util

import org.joda.time.{ DateTime, LocalDate }
import org.joda.time.format.DateTimeFormat

/**
 * システム日付に関するUtil.
 */
object CurrentDateUtil {

  /**
   * *
   *  テスト用固定日付.
   *  -DfixDate=2016-01-01 のようにパラメータを設定すると、テスト用に日付を固定します。
   */
  private val fixDate = System.getProperty("fixDate", "") match {
    case date if !date.isEmpty => Option(DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(date))
    case _ => None
  }

  /**
   * *
   *  テスト用固定日時.
   *  -DfixDateTime=2016-01-01T23:59:59 のようにパラメータを設定すると、テスト用に日付を固定します。
   */
  private val fixDateTime = System.getProperty("fixDateTime", "") match {
    case dateTime if !dateTime.isEmpty => Option(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").parseDateTime(dateTime))
    case _ => None
  }

  /**
   * 現在日付取得.
   * ※JVMの引数に、-DfixDate=2016-01-01 のように設定されている場合、指定した固定日付を返します
   * @return 現在日付
   */
  def now: LocalDate = {
    if (fixDate.isEmpty) LocalDate.now() else fixDate.get
  }

  /**
   * 現在日時取得.
   * ※JVMの引数に、-DfixDateTime=2016-01-01T23:59:59 のように設定されている場合、指定した固定日時を返します
   * @return 現在日時
   */
  def nowDateTime: DateTime = {
    if (fixDateTime.isEmpty) DateTime.now() else fixDateTime.get
  }

  /**
   * 最大日付取得.
   * システム上の最大日付を取得します
   * @return 最大日付
   */
  def getMaxDate: LocalDate = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate("29991231")

  /**
   * 最小日付取得.
   * システム上の最小日付を取得します
   * @return 最大日付
   */
  def getMinDate: LocalDate = DateTimeFormat.forPattern("yyyyMMdd").parseLocalDate("00010101")

}
