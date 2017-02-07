package domain

/**
 * レポジトリ.
 */
trait Repository[T <: Entity[T]] {

  /**
   * リポジトリ上の全件取得
   * @return 該当データ
   */
  def findAll(): Seq[T]

  /**
   * リポジトリ上の全件削除.
   */
  def deleteAll()
}
