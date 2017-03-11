package application.impl

import javax.inject.Inject

import application.{ UserDto, UserSerivce }
import domain.ApplicationException
import domain.user.{ User, UserAuthority, UserId, UserRepository }
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
          user <- userRepository.findById(UserId(id))
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
   * @inheritdoc
   */
  override def store(form: Edit)(implicit session: DBSession): Either[ApplicationException, Long] = {

    val user = toDomain(form)
    if (user.userId.isEmpty) {
      //新規
      userRepository.create(user, form.password)
    } else {
      //変更
      userRepository.update(user, if (form.password.isEmpty) None else Option(form.password))
    }
  }

  /**
   * @inheritdoc
   */
  override def delete(userId: Long, lockVersion: Long)(implicit session: DBSession): Either[ApplicationException, Long] = userRepository.delete(UserId(id = userId), lockVersion)

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

  /**
   * Userドメインへ変換.
   * @param form 登録・変更Form
   * @return Userドメイン
   */
  private[this] def toDomain(form: Edit): User = {
    User(
      userId = if (form.id.isEmpty) None else Option(UserId(form.id.toLong)),
      loginId = form.loginId,
      name = form.userName,
      authority = UserAuthority.withCode(form.authority).get,
      lockVersion = form.lockVersion.toLong
    )
  }
}
