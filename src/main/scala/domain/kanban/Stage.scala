package domain.kanban

import domain.{ Entity, Enum, EnumEntry, ValueObject }

/**
 * ステージドメイン.
 * @param stageId ステージID
 * @param sortNum ソート順
 * @param configuration ステージ構成情報
 */
case class Stage(
    stageId: Option[StageId],
    sortNum: Long = Long.MaxValue,
    configuration: StageConfiguration
) extends Entity[Stage] {
  override def sameIdentityAs(other: Stage): Boolean = (for {
    thisId <- this.stageId
    otherId <- other.stageId
  } yield thisId.sameValueAs(otherId)) getOrElse false

  /**
   * ステージ情報更新.
   * @param configuration ステージ構成情報
   * @return 更新後ステージ情報
   */
  def updated(configuration: StageConfiguration): Stage = {
    this.copy(configuration = configuration)
  }
}

/**
 * ステージObject.
 */
object Stage {

  /**
   * 初期ステージ作成.
   * 未実施 / 実施中 / 完了のステージを作成します
   * @return 初期ステージSeq
   */
  def createInitStage: Seq[Stage] = {
    Seq(
      Stage(
        stageId = None,
        configuration = StageConfiguration(
          stageStatus = StageStatus.Open,
          completeStage = false,
          name = "未実施"
        )
      ),
      Stage(
        stageId = None,
        configuration = StageConfiguration(
          stageStatus = StageStatus.Open,
          completeStage = false,
          name = "実施中"
        )
      ),
      Stage(
        stageId = None,
        configuration = StageConfiguration(
          stageStatus = StageStatus.Open,
          completeStage = true,
          name = "完了"
        )
      )
    )
  }

  /**
   * ステージSeq生成.
   * @param stageIds ステージIDSeq
   * @param stageNames ステージ名Seq
   * @param archiveStatuses アーカイブステータスSeq
   * @param completeStages 完了扱いのステージSeq
   * @return ステージSeq
   */
  def createStages(
    stageIds: Seq[String],
    stageNames: Seq[String],
    archiveStatuses: Seq[String],
    completeStages: Seq[Boolean]
  ): Seq[Stage] = stageIds.zipWithIndex map {
    case (stageId, index) =>
      Stage(
        stageId = if (stageId.isEmpty) None else Option(StageId(stageId.toLong)),
        configuration = StageConfiguration(
          stageStatus = StageStatus.withCode(archiveStatuses(index)).get,
          completeStage = completeStages(index),
          name = stageNames(index)
        ),
        sortNum = index
      )
  }

}

/**
 * ステージID.
 * @param id ID値
 */
case class StageId(
    id: Long
) extends ValueObject[StageId] {
  /**
   * @inheritdoc
   */
  override def sameValueAs(other: StageId): Boolean = this.id == other.id
}

/**
 * ステージ構成情報.
 * @param stageStatus ステージの状態
 * @param completeStage 「完了扱いのステージ」か
 * @param name ステージ名
 */
case class StageConfiguration(
  stageStatus: StageStatus = StageStatus.Open,
  completeStage: Boolean,
  name: String
)

//ステージの状態
sealed abstract class StageStatus(override val code: String) extends EnumEntry
object StageStatus extends Enum[StageStatus] {
  /** Open. */
  case object Open extends StageStatus("0")
  /** Archive. */
  case object Archive extends StageStatus("1")
  protected val values = Seq(Open, Archive)
}
