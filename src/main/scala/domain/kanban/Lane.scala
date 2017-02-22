package domain.kanban

import domain.{ Entity, Enum, EnumEntry, ValueObject }

/**
 * レーンドメイン.
 * @param laneId レーンID
 * @param sortNum ソート順
 * @param configuration レーン構成情報
 */
case class Lane(
    laneId: Option[LaneId],
    sortNum: Long = Long.MaxValue,
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
 * レーンObject.
 */
object Lane {

  /**
   * 初期レーン作成.
   * 未実施 / 実施中 / 完了のレーンを作成します
   * @return 初期レーンSeq
   */
  def createInitLane: Seq[Lane] = {
    Seq(
      Lane(
        laneId = None,
        configuration = LaneConfiguration(
          laneStatus = LaneStatus.Open,
          completeLane = false,
          name = "未実施"
        )
      ),
      Lane(
        laneId = None,
        configuration = LaneConfiguration(
          laneStatus = LaneStatus.Open,
          completeLane = false,
          name = "実施中"
        )
      ),
      Lane(
        laneId = None,
        configuration = LaneConfiguration(
          laneStatus = LaneStatus.Open,
          completeLane = true,
          name = "完了"
        )
      )
    )
  }

  /**
   * レーンSeq生成.
   * @param laneIds レーンIDSeq
   * @param laneNames レーン名Seq
   * @param archiveStatuses アーカイブステータスSeq
   * @param completeLanes 完了扱いのレーンSeq
   * @return レーンSeq
   */
  def createLanes(
    laneIds: Seq[String],
    laneNames: Seq[String],
    archiveStatuses: Seq[String],
    completeLanes: Seq[Boolean]
  ): Seq[Lane] = laneIds.zipWithIndex map {
    case (laneId, index) =>
      Lane(
        laneId = if (laneId.isEmpty) None else Option(LaneId(laneId.toLong)),
        configuration = LaneConfiguration(
          laneStatus = LaneStatus.withCode(archiveStatuses(index)).get,
          completeLane = completeLanes(index),
          name = laneNames(index)
        ),
        sortNum = index
      )
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
 */
case class LaneConfiguration(
  laneStatus: LaneStatus = LaneStatus.Open,
  completeLane: Boolean,
  name: String
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
