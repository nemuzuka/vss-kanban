package domain

import scalikejdbc.DBSession

/**
 * レポジトリ.
 */
trait Repository[T <: Entity[T]] {

  /**
   * リポジトリ上の全件取得
   * @param session Session
   * @return 該当データ
   */
  def findAll(implicit session: DBSession): Seq[T]

  /**
   * リポジトリ上の全件削除.
   * @param session Session
   */
  def deleteAll()(implicit session: DBSession)
}
