package domain.user

import domain.{ ApplicationException, Repository }
import scalikejdbc._

/**
 * Userに関するRepository.
 */
trait UserRepository extends Repository[User] {

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
   * @param userId ユーザID
   * @param password パスワード
   * @param session Session
   * @return 該当User(存在しない or パスワードが異なる場合None)
   */
  def findByIdWithPassword(userId: UserId, password: String)(implicit session: DBSession): Option[User]

  /**
   * ユーザIDが合致するUserの取得.
   * @param userId ユーザID
   * @param session Session
   * @return 該当User(存在しない場合None)
   */
  def findById(userId: UserId)(implicit session: DBSession): Option[User]

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

  /**
   * 登録されているユーザが存在するか?
   * @param session Session
   * @return 1件以上登録されている場合、true
   */
  def existsUser(implicit session: DBSession): Boolean

  /**
   * 削除.
   * @param userId ユーザID
   * @param lockVersion バージョンNo
   * @param session Session
   * @return Right:ユーザID, Left:エラー情報
   */
  def delete(userId: UserId, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * ソート順変更.
   * ユーザIDSeqの順にソート順を変更していきます
   * @param userIds ユーザIDSeq
   * @param session Session
   */
  def updateSortNum(userIds: Seq[UserId])(implicit session: DBSession): Unit
}
