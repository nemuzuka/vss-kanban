package application.impl

import javax.inject.Inject

import application.KanbanAdminService
import domain.ApplicationException
import domain.kanban.{ KanbanConfiguration, KanbanId, KanbanRepository, KanbanStatus }
import domain.user.User
import form.kanban.Edit
import scalikejdbc.DBSession

/**
 * KanbanAdminServiceの実装クラス.
 */
class KanbanAdminServiceImpl @Inject() (
    kanbanRepository: KanbanRepository
) extends KanbanAdminService {

  /**
   * @inheritdoc
   */
  override def getForm(id: Long, loginUser: User)(implicit session: DBSession): Option[Edit] = {
    for (
      kanbanRow <- kanbanRepository.findRowById(KanbanId(id), loginUser)
    ) yield {
      Edit(
        id = kanbanRow.id.toString,
        kanbanTitle = kanbanRow.title,
        kanbanDescription = kanbanRow.description,
        archiveStatus = kanbanRow.archiveStatus,
        lockVersion = kanbanRow.lockVersion.toString
      )
    }
  }

  /**
   * @inheritdoc
   */
  override def updateKanban(form: Edit, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long] = {
    val result = for {
      kanban <- kanbanRepository.findById(KanbanId(form.id.toLong), loginUser) if kanban.isAdministrator(loginUser)
    } yield {
      val updateKanban = kanban.copy(configuration =
        KanbanConfiguration(
          title = form.kanbanTitle,
          description = form.kanbanDescription,
          kanbanStatus = KanbanStatus.withCode(form.archiveStatus).get,
          lockVersion = form.lockVersion.toLong
        ))
      kanbanRepository.store(updateKanban)
    }
    result getOrElse Left(new ApplicationException("noData", Seq()))
  }
}
