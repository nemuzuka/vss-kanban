package form.kanban

/**
 * ステージ変更Form.
 * @param id かんばんID
 * @param lockVersion バージョンNo
 * @param stageIds ステージIDSeq
 * @param stageNames ステージ名Seq
 * @param archiveStatuses アーカイブステータスSeq
 * @param completeStages 完了扱いのステージSeq
 */
case class Stage(
  id: Long,
  lockVersion: Long,
  stageIds: Seq[String],
  stageNames: Seq[String],
  archiveStatuses: Seq[String],
  completeStages: Seq[Boolean]
)
