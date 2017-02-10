package application

import domain.ApplicationException
import form.user.Edit
import scalikejdbc.DBSession

/**
 * Userに関するApplicationService.
 */
trait UserSerivce {

  /**
   * 全件取得.
   * @param session Session
   * @return 登録されている全てのUserDto
   */
  def findAll(implicit session: DBSession): Seq[UserDto]

  /**
   * Form情報取得.
   * @param idOpt ユーザID。Noneの場合新規登録用のForm情報を返却
   * @param session Session
   * @return Form情報(ID指定で該当データが存在しない場合、None)
   */
  def getForm(idOpt: Option[Long])(implicit session: DBSession): Option[Edit]

  /**
   * 登録・変更
   * @param form Form情報
   * @param session Session
   * @return Right:ユーザID, Left:エラー情報
   */
  def store(form: Edit)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * 削除.
   * @param userId ユーザID
   * @param lockVersion バージョンNo
   * @param session Session
   * @return Right:ユーザID, Left:エラー情報
   */
  def delete(userId: Long, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * UserDto
   * @param id ID
   * @param name ユーザ名
   * @param loginId ログインID
   * @param authority ユーザ権限
   * @param lockVersion バージョンNo
   */
  case class UserDto(
    id: Long,
    name: String,
    loginId: String,
    authority: String,
    lockVersion: Long
  )
}
