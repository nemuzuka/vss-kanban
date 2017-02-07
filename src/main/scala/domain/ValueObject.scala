package domain

/**
 * 値オブジェクト.
 * @tparam T 型
 */
trait ValueObject[T] extends Serializable {

  /**
   * 同じ値オブジェクトか.
   * @param other 比較対象インスタンス
   * @return 同じ値オブジェクトの場合、true / そうでない場合、false
   */
  def sameValueAs(other: T): Boolean
}
