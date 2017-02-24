package domain.kanban

import domain.{ Entity, Enum, EnumEntry, ValueObject }
import domain.user.{ User, UserAuthority, UserId }

/**
 * かんばんドメイン.
 * @param kanbanId かんばんID
 * @param configuration かんばん構成情報
 * @param joinedUsers 参加ユーザ
 */
case class Kanban(
    kanbanId: Option[KanbanId],
    configuration: KanbanConfiguration,
    joinedUsers: Seq[JoinedUser]
) extends Entity[Kanban] {

  /**
   * @inheritdoc
   */
  override def sameIdentityAs(other: Kanban): Boolean = (for {
    thisId <- this.kanbanId
    otherId <- other.kanbanId
  } yield thisId.sameValueAs(otherId)) getOrElse false

  /** 参加ユーザMap. */
  private[this] lazy val joinedUserMap: Map[UserId, JoinedUser] = (joinedUsers map (v => (v.userId, v))).toMap

  /**
   * かんばんに参加しているか？
   * 対象ユーザが
   * ・かんばん参加ユーザに含まれる
   * ・Application管理者
   * のいずれかの場合、参加しているとみなします
   * @param user 対象ユーザ
   * @return かんばんに参加している場合、true
   */
  def isJoined(user: User): Boolean = user.userId match {
    case Some(userId) =>
      joinedUserMap.contains(userId) || user.authority == UserAuthority.ApplicationAdministrator
    case _ => false
  }

  /**
   * かんばんの管理者か？
   * 対象ユーザが
   * ・かんばん参加ユーザに含まれ、かんばん管理者である
   * ・Application管理者
   * のいずれかの場合、かんばん管理者とみなします
   * @param user 対象ユーザ
   * @return かんばんの管理者の場合、true
   */
  def isAdministrator(user: User): Boolean = user.userId match {
    case Some(userId) =>
      (joinedUserMap get userId).exists { v => v.authority == KanbanAuthority.Administrator } ||
        user.authority == UserAuthority.ApplicationAdministrator
    case _ => false
  }

  /**
   * かんばん情報変更.
   * かんばん名、説明文の更新をし、新しいインスタンスを返します
   * @param configuration かんばん構成情報
   * @return 更新後Board情報
   */
  def updated(configuration: KanbanConfiguration): Kanban = {
    this.copy(configuration = configuration)
  }

  /**
   * かんばんRow生成.
   * @param user 対象ユーザ
   * @return かんばんRow
   */
  def toKanbanRow(user: User) = KanbanRow(
    id = kanbanId.get.id,
    title = configuration.title,
    description = configuration.description,
    archiveStatus = configuration.kanbanStatus.code,
    lockVersion = configuration.lockVersion,
    authority = if (isAdministrator(user)) "1" else "0"
  )

}

/**
 * かんばんObject.
 */
object Kanban {

  /**
   * かんばん初期データ生成.
   * 登録したユーザをかんばんの管理者として登録します
   * @param kanbanTitle かんばんタイトル
   * @param kanbanDescription かんばん説明
   * @param loginUserId 登録ユーザID
   * @return かんばんドメイン
   */
  def createInitKanban(kanbanTitle: String, kanbanDescription: String, loginUserId: Long): Kanban = {
    Kanban(
      kanbanId = None,
      configuration = KanbanConfiguration(
        title = kanbanTitle,
        description = kanbanDescription,
        kanbanStatus = KanbanStatus.Open
      ),
      joinedUsers = Seq(
        JoinedUser(
          userId = UserId(loginUserId),
          authority = KanbanAuthority.Administrator,
          name = ""
        )
      )
    )
  }
}

/**
 * かんばんID.
 * @param id ID値
 */
case class KanbanId(
    id: Long
) extends ValueObject[KanbanId] {
  override def sameValueAs(other: KanbanId): Boolean = this.id == other.id
}

/**
 * かんばん構成情報.
 * @param title かんばんタイトル
 * @param description 説明文
 * @param kanbanStatus かんばんの状態
 * @param lockVersion lockVersion
 */
case class KanbanConfiguration(
  title: String,
  description: String,
  kanbanStatus: KanbanStatus,
  lockVersion: Long = 1L
)

//かんばんの状態
sealed abstract class KanbanStatus(override val code: String) extends EnumEntry
object KanbanStatus extends Enum[KanbanStatus] {
  /** Open(デフォルトで表示される). */
  case object Open extends KanbanStatus("0")
  /** Archive(検索条件を指定しないと表示されない). */
  case object Archive extends KanbanStatus("1")
  protected val values = Seq(Open, Archive)
}
