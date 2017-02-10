package domain

/**
 * 例外の基底クラス.
 * @param _messageKey メッセージKey
 * @param _paramKey パラメータKeySeq
 */
class ApplicationException(_messageKey: String, _paramKey: Seq[String]) extends RuntimeException {
  val messageKey: String = _messageKey
  val paramKey: Seq[String] = _paramKey
}
