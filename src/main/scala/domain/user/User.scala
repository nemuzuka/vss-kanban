package domain.user

import domain.{ Enum, EnumEntry, ValueObject }

/**
 * ユーザ情報.
 * @param userId ユーザID
 * @param name ユーザ名
 * @param loginId ログインID
 * @param authority ユーザ権限
 * @param lockVersion lockVersion
 */
case class User(
    userId: Option[UserId],
    loginId: String,
    name: String,
    authority: UserAuthority,
    lockVersion: Long = 1L
) extends ValueObject[User] {
  /**
   * @inheritdoc
   */
  override def sameValueAs(other: User): Boolean = (for {
    thisId <- this.userId
    otherId <- other.userId
  } yield thisId.sameValueAs(otherId)) getOrElse false

  /** ユーザ権限取得. */
  def authentications: Seq[UserAuthority] = Seq(authority)

}

//ユーザに対する権限
sealed abstract class UserAuthority(override val code: String) extends EnumEntry
object UserAuthority extends Enum[UserAuthority] {
  /** Application管理者. */
  case object ApplicationAdministrator extends UserAuthority("1")
  /** 一般. */
  case object Normal extends UserAuthority("2")
  protected val values = Seq(ApplicationAdministrator, Normal)
}
