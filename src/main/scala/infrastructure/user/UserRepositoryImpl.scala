package infrastructure.user

import domain.user.{User, UserRepository}
import scalikejdbc.DBSession

/**
 * UserRepositoryの実装クラス.
 */
class UserRepositoryImpl extends UserRepository {

  /**
    * @inheritdoc
    */
  override def findByLoginId(loginId: String, password: String)(implicit session: DBSession): Option[User] = {

    //TODO loginIdを元に該当レコードを取得し、登録済みのパスワードと合致するかチェック
    //該当データが存在しない or パスワードが合致しない場合、None
    //それ以外の場合、UserDomainオブジェクトを生成する

    ???
  }
}
