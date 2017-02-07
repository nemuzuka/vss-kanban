package domain.user

import scalikejdbc.DBSession

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
}
