package application.impl

import javax.inject.Inject

import application.UserSerivce
import domain.user.{ User, UserRepository }
import form.user.Edit
import scalikejdbc.DBSession

/**
 * UserSerivceの実装クラス.
 */
class UserSerivceImpl @Inject() (
    userRepository: UserRepository
) extends UserSerivce {

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[UserDto] = {
    userRepository.findAll map toDto
  }

  /**
   * @inheritdoc
   */
  override def getForm(idOpt: Option[Long])(implicit session: DBSession): Option[Edit] = {

    idOpt match {
      case Some(id) =>
        for (
          user <- userRepository.findById(id)
        ) yield {
          Edit(
            id = user.userId.get.id.toString,
            loginId = user.loginId,
            userName = user.name,
            authority = user.authority.code,
            lockVersion = user.lockVersion.toString
          )
        }

      case _ =>
        Option(Edit(
          id = "",
          loginId = "",
          userName = ""
        ))
    }

  }

  /**
   * UserDtoへ変換.
   * @param user Userドメイン
   * @return UserDto
   */
  private[this] def toDto(user: User): UserDto = {
    UserDto(
      id = user.userId.get.id,
      name = user.name,
      loginId = user.loginId,
      lockVersion = user.lockVersion,
      authority = user.authority.code
    )
  }
}
