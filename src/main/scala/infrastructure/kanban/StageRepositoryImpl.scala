package infrastructure.kanban

import domain.kanban._
import scalikejdbc.DBSession

/**
 * StageRepositoryの実装クラス.
 */
class StageRepositoryImpl extends StageRepository {
  /**
   * @inheritdoc
   */
  override def findById(stageId: StageId)(implicit session: DBSession): Option[Stage] = ???

  /**
   * @inheritdoc
   */
  override def findByKanbanId(kanbanId: KanbanId, includeArchive: Boolean)(implicit session: DBSession): Seq[StageRow] = {
    model.Stage.findByKanbanId(kanbanId.id, includeArchive) map { v =>
      StageRow(
        stageId = v.id.toString,
        stageName = v.stageName,
        archiveStatus = v.archiveStatus,
        completeStage = if (v.completeStage == "1") true else false
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def store(stages: Seq[Stage], kanbanId: KanbanId)(implicit session: DBSession): Unit = {

    val beforeStageIdSeq = findByKanbanId(kanbanId, includeArchive = true) map (_.stageId.toLong)

    val afterStageIdSeq = stages map { stage =>
      if (stage.stageId.isEmpty) {
        model.Stage.create(model.Stage(
          id = -1L,
          kanbanId = kanbanId.id,
          stageName = stage.configuration.name,
          archiveStatus = stage.configuration.stageStatus.code,
          sortNum = stage.sortNum,
          completeStage = if (stage.configuration.completeStage) "1" else "0"
        ))
      } else {
        model.Stage.update(model.Stage(
          id = stage.stageId.get.id,
          kanbanId = kanbanId.id,
          stageName = stage.configuration.name,
          archiveStatus = stage.configuration.stageStatus.code,
          sortNum = stage.sortNum,
          completeStage = if (stage.configuration.completeStage) "1" else "0"
        ))
      }
    }

    beforeStageIdSeq diff afterStageIdSeq foreach { model.Stage.deleteById }
  }

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[Stage] = ???

  /**
   * @inheritdoc
   */
  override def deleteAll()(implicit session: DBSession): Unit = ???
}
