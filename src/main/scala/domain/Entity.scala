package domain

/**
 * Entity.
 * @tparam T 型
 */
trait Entity[T] {
  /**
   * 同じEntityか.
   * @param other 比較対象インスタンス
   * @return 同じEntityの場合、true / そうでない場合、false
   */
  def sameIdentityAs(other: T): Boolean
}
