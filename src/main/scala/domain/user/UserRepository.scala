package domain.user

import domain.ApplicationException
import scalikejdbc._

/**
 * Userに関するRepository.
 */
trait UserRepository {

  /**
   * ログインIDからの取得.
   * ※今回の認証はユーザドメインで完結するので、特にドメインサービス化は行いません
   * @param loginId ログインID
   * @param password ユーザ入力パスワード
   * @param session Session
   * @return 該当User(存在しない or パスワードが異なる場合None)
   */
  def findByLoginId(loginId: String, password: String)(implicit session: DBSession): Option[User]

  /**
   * ユーザIDとパスワードが合致するUserの取得.
   * @param id ユーザID
   * @param password パスワード
   * @param session Session
   * @return 該当User(存在しない or パスワードが異なる場合None)
   */
  def findByIdWithPassword(id: Long, password: String)(implicit session: DBSession): Option[User]

  /**
   * ユーザIDが合致するUserの取得.
   * @param id ユーザID
   * @param session Session
   * @return 該当User(存在しない場合None)
   */
  def findById(id: Long)(implicit session: DBSession): Option[User]

  /**
   * 登録.
   * @param user ユーザドメイン
   * @param password ユーザ入力パスワード
   * @param session Session
   * @return Right:ユーザID, Left:エラー情報
   */
  def create(user: User, password: String)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * 更新.
   * @param user ユーザドメイン
   * @param password ユーザ入力パスワード
   * @param session Session
   * @return Right:ユーザID, Left:エラー情報
   */
  def update(user: User, password: Option[String])(implicit session: DBSession): Either[ApplicationException, Long]

}
