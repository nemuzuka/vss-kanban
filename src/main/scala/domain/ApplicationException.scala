package domain

/**
 * 例外の基底クラス.
 * @param messageKey メッセージKey
 * @param paramKey パラメータKeySeq
 */
class ApplicationException(messageKey: String, paramKey: Seq[String]) extends RuntimeException
