package infrastructure.kanban

import domain.kanban._
import scalikejdbc.DBSession

/**
 * LaneRepositoryの実装クラス.
 */
class LaneRepositoryImpl extends LaneRepository {
  /**
   * @inheritdoc
   */
  override def findById(laneId: LaneId)(implicit session: DBSession): Option[Lane] = ???

  /**
   * @inheritdoc
   */
  override def findByKanbanId(kanbanId: KanbanId, includeArchive: Boolean)(implicit session: DBSession): Seq[LaneRow] = {
    model.Lane.findByKanbanId(kanbanId.id, includeArchive) map { v =>
      LaneRow(
        laneId = v.id.toString,
        laneName = v.laneName,
        archiveStatus = v.archiveStatus,
        completeLane = if (v.completeLane == "1") true else false
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def store(lanes: Seq[Lane], kanbanId: KanbanId)(implicit session: DBSession): Unit = {

    val beforeLaneIdSeq = findByKanbanId(kanbanId, includeArchive = true) map (_.laneId.toLong)

    val afterLaneIdSeq = lanes map { lane =>
      if (lane.laneId.isEmpty) {
        model.Lane.create(model.Lane(
          id = -1L,
          kanbanId = kanbanId.id,
          laneName = lane.configuration.name,
          archiveStatus = lane.configuration.laneStatus.code,
          sortNum = lane.sortNum,
          completeLane = if (lane.configuration.completeLane) "1" else "0"
        ))
      } else {
        model.Lane.update(model.Lane(
          id = lane.laneId.get.id,
          kanbanId = kanbanId.id,
          laneName = lane.configuration.name,
          archiveStatus = lane.configuration.laneStatus.code,
          sortNum = lane.sortNum,
          completeLane = if (lane.configuration.completeLane) "1" else "0"
        ))
      }
    }

    beforeLaneIdSeq diff afterLaneIdSeq foreach { model.Lane.deleteById }
  }

  /**
   * @inheritdoc
   */
  override def findAll(implicit session: DBSession): Seq[Lane] = ???

  /**
   * @inheritdoc
   */
  override def deleteAll()(implicit session: DBSession): Unit = ???
}
