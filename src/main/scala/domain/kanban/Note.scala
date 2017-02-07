package domain.kanban

import domain.{ Entity, Enum, EnumEntry, ValueObject }
import org.joda.time.LocalDate

/**
 * 付箋ドメイン.
 * @param noteId 付箋ID
 * @param sortOrder ソート順
 * @param noteStatus 付箋の状態
 * @param title 件名
 * @param description 説明文
 * @param deadline 締切日
 * @param lockVersion lockVersion
 */
case class Note(
    noteId: Option[NoteId],
    sortOrder: Long = Long.MaxValue,
    noteStatus: NoteStatus = NoteStatus.Open,
    title: String,
    description: String,
    deadline: Option[LocalDate],
    lockVersion: Long = 1L
) extends Entity[Note] {
  override def sameIdentityAs(other: Note): Boolean = (for {
    thisId <- this.noteId
    otherId <- other.noteId
  } yield thisId.sameValueAs(otherId)) getOrElse false
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
  case object Open extends NoteStatus("1")
  /** Archive. */
  case object Archive extends NoteStatus("2")
  protected val values = Seq(Open, Archive)
}
