package domain

/**
 * 例外の基底クラス.
 */
abstract class ApplicationException extends RuntimeException {
  /** メッセージKey. */
  val messageKey: String
}
