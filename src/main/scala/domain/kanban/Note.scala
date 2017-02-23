package domain.kanban

import domain.user.{ User, UserAuthority, UserId }
import domain.{ Entity, Enum, EnumEntry, ValueObject }
import org.joda.time.LocalDate

/**
 * ふせんドメイン.
 * @param noteId 付箋ID
 * @param sortNum ソート順
 * @param noteStatus 付箋の状態
 * @param title 件名
 * @param description 説明文
 * @param fixDate 期限
 * @param createUserId ふせん作成者
 * @param lockVersion lockVersion
 * @param chargedUsers ふせん担当者Seq
 */
case class Note(
    noteId: Option[NoteId],
    sortNum: Long = Long.MaxValue,
    noteStatus: NoteStatus = NoteStatus.Open,
    title: String,
    description: String,
    fixDate: Option[LocalDate],
    createUserId: UserId,
    lockVersion: Long = 1L,
    chargedUsers: Seq[ChargedUser]
) extends Entity[Note] {

  /** ふせん担当者Map. */
  private[this] lazy val chargedUserMap: Map[UserId, ChargedUser] = (chargedUsers map (v => (v.userId, v))).toMap

  override def sameIdentityAs(other: Note): Boolean = (for {
    thisId <- this.noteId
    otherId <- other.noteId
  } yield thisId.sameValueAs(otherId)) getOrElse false

  /**
   * ふせん担当者か？
   * 対象ユーザが
   * ・ふせん作成者
   * ・ふせん担当者ユーザに含まれる
   * ・かんばん管理者
   * ・Application管理者
   * のいずれかの場合、担当者とみなします
   * @param user 対象ユーザ
   * @return ふせん担当者の場合、true
   */
  def isCharged(user: User, kanban: Kanban): Boolean = user.userId match {
    case Some(userId) =>
      createUserId == userId || chargedUserMap.contains(userId) || kanban.isAdministrator(user) || user.authority == UserAuthority.ApplicationAdministrator
    case _ => false
  }
}

/**
 * 付箋ID.
 * @param id ID値
 */
case class NoteId(
    id: Long
) extends ValueObject[NoteId] {
  override def sameValueAs(other: NoteId): Boolean = this.id == other.id
}

//付箋の状態
sealed abstract class NoteStatus(override val code: String) extends EnumEntry
object NoteStatus extends Enum[NoteStatus] {
  /** Open. */
  case object Open extends NoteStatus("0")
  /** Archive. */
  case object Archive extends NoteStatus("1")
  protected val values = Seq(Open, Archive)
}
