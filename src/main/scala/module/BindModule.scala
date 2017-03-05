package module

import application._
import application.impl._
import com.google.inject._
import domain.attachment.AttachmentFileRepository
import domain.kanban.{ KanbanRepository, StageRepository, NoteRepository }
import domain.user.UserRepository
import infrastructure.attachment.AttachmentFileRepositoryImpl
import infrastructure.kanban.{ KanbanRepositoryImpl, StageRepositoryImpl, NoteRepositoryImpl }
import infrastructure.user.UserRepositoryImpl

/**
 * DI設定Module.
 */
class BindModule extends AbstractModule {
  /**
   * DI設定定義
   */
  override def configure(): Unit = {
    //traitに対するインスタンスの解決を定義
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl]).in(classOf[Singleton])
    bind(classOf[KanbanRepository]).to(classOf[KanbanRepositoryImpl]).in(classOf[Singleton])
    bind(classOf[StageRepository]).to(classOf[StageRepositoryImpl]).in(classOf[Singleton])
    bind(classOf[NoteRepository]).to(classOf[NoteRepositoryImpl]).in(classOf[Singleton])
    bind(classOf[AttachmentFileRepository]).to(classOf[AttachmentFileRepositoryImpl]).in(classOf[Singleton])

    bind(classOf[MaintenanceService]).to(classOf[MaintenanceServiceImpl]).in(classOf[Singleton])
    bind(classOf[UserSerivce]).to(classOf[UserSerivceImpl]).in(classOf[Singleton])
    bind(classOf[KanbanService]).to(classOf[KanbanServiceImpl]).in(classOf[Singleton])
    bind(classOf[NoteService]).to(classOf[NoteServiceImpl]).in(classOf[Singleton])
    bind(classOf[KanbanAdminService]).to(classOf[KanbanAdminServiceImpl]).in(classOf[Singleton])
    bind(classOf[AttachmentFileService]).to(classOf[AttachmentFileServiceImpl]).in(classOf[Singleton])
  }
}

/**
 * DI設定インスタンス管理.
 */
object BindModule {
  val injector: Injector = Guice.createInjector(new BindModule())
}
