package domain.kanban

import domain.{ Entity, Enum, EnumEntry, ValueObject }

/**
 * レーンドメイン.
 * @param laneId レーンID
 * @param sortOrder ソート順
 * @param configuration レーン構成情報
 */
case class Lane(
    laneId: Option[LaneId],
    sortOrder: Long = Long.MaxValue,
    configuration: LaneConfiguration
) extends Entity[Lane] {
  override def sameIdentityAs(other: Lane): Boolean = (for {
    thisId <- this.laneId
    otherId <- other.laneId
  } yield thisId.sameValueAs(otherId)) getOrElse false

  /**
   * レーン情報更新.
   * @param configuration レーン構成情報
   * @return 更新後レーン情報
   */
  def updated(configuration: LaneConfiguration): Lane = {
    this.copy(configuration = configuration)
  }
}

/**
 * レーンID.
 * @param id ID値
 */
case class LaneId(
    id: Long
) extends ValueObject[LaneId] {
  /**
   * @inheritdoc
   */
  override def sameValueAs(other: LaneId): Boolean = this.id == other.id
}

/**
 * レーン構成情報.
 * @param laneStatus レーンの状態
 * @param completeLane 「完了扱いのレーン」か
 * @param name レーン名
 * @param lockVersion lockVersion
 */
case class LaneConfiguration(
  laneStatus: LaneStatus = LaneStatus.Open,
  completeLane: Boolean,
  name: String,
  lockVersion: Long = 1L
)

//レーンの状態
sealed abstract class LaneStatus(override val code: String) extends EnumEntry
object LaneStatus extends Enum[LaneStatus] {
  /** Open. */
  case object Open extends LaneStatus("0")
  /** Archive. */
  case object Archive extends LaneStatus("1")
  protected val values = Seq(Open, Archive)
}
