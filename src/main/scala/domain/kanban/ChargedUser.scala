package domain.kanban

import domain.ValueObject
import domain.user.UserId

/**
 * ふせん担当者,
 * @param userId ユーザID
 * @param name 名前
 */
case class ChargedUser(
    userId: UserId,
    name: String
) extends ValueObject[ChargedUser] {
  /**
   * @inheritdoc
   */
  override def sameValueAs(order: ChargedUser): Boolean = this.userId.sameValueAs(order.userId)
}

/**
 * ふせん担当者Object.
 */
object createChargedUser {
  /**
   * ふせん担当者生成.
   * @param userId ユーザID
   * @param name 名前
   * @return ふせん担当者
   */
  def createChargedUser(userId: Long, name: String = "") = ChargedUser(
    userId = UserId(userId),
    name = name
  )
}
