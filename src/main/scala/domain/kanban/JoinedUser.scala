package domain.kanban

import domain.user.UserId
import domain.{ Enum, EnumEntry, ValueObject }

/**
 * かんばん参加ユーザ.
 * @param userId ユーザID
 * @param authority かんばん権限
 * @param name ユーザ名
 */
case class JoinedUser(
    userId: UserId,
    authority: KanbanAuthority,
    name: String
) extends ValueObject[JoinedUser] {
  /**
   * @inheritdoc
   */
  override def sameValueAs(order: JoinedUser): Boolean = this.userId.sameValueAs(order.userId)
}

//かんばんに対する権限
sealed abstract class KanbanAuthority(override val code: String) extends EnumEntry
object KanbanAuthority extends Enum[KanbanAuthority] {
  /** かんばん管理者. */
  case object Administrator extends KanbanAuthority("1")
  /** メンバー. */
  case object Member extends KanbanAuthority("2")
  protected val values = Seq(Administrator, Member)
}

