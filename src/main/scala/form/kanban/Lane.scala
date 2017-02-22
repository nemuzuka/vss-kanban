package form.kanban

/**
 * レーン変更Form.
 * @param id かんばんID
 * @param lockVersion バージョンNo
 * @param laneIds レーンIDSeq
 * @param laneNames レーン名Seq
 * @param archiveStatuses アーカイブステータスSeq
 * @param completeLanes 完了扱いのレーンSeq
 */
case class Lane(
  id: Long,
  lockVersion: Long,
  laneIds: Seq[String],
  laneNames: Seq[String],
  archiveStatuses: Seq[String],
  completeLanes: Seq[Boolean]
)
