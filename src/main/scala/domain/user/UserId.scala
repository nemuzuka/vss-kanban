package domain.user

import domain.ValueObject

/**
 * ユーザID.
 * @param id ID値
 */
case class UserId(
    id: Long
) extends ValueObject[UserId] {
  override def sameValueAs(other: UserId): Boolean = this.id == other.id
}
