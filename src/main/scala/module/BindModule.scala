package module

import application._
import application.impl._
import com.google.inject._
import domain.attachment.AttachmentFileRepository
import domain.kanban.{ KanbanRepository, LaneRepository }
import domain.user.UserRepository
import infrastructure.attachment.AttachmentFileRepositoryImpl
import infrastructure.kanban.{ KanbanRepositoryImpl, LaneRepositoryImpl }
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
    bind(classOf[LaneRepository]).to(classOf[LaneRepositoryImpl]).in(classOf[Singleton])
    bind(classOf[AttachmentFileRepository]).to(classOf[AttachmentFileRepositoryImpl]).in(classOf[Singleton])

    bind(classOf[MaintenanceService]).to(classOf[MaintenanceServiceImpl]).in(classOf[Singleton])
    bind(classOf[UserSerivce]).to(classOf[UserSerivceImpl]).in(classOf[Singleton])
    bind(classOf[KanbanService]).to(classOf[KanbanServiceImpl]).in(classOf[Singleton])
    bind(classOf[AttachmentFileService]).to(classOf[AttachmentFileServiceImpl]).in(classOf[Singleton])
  }
}

/**
 * DI設定インスタンス管理.
 */
object BindModule {
  val injector: Injector = Guice.createInjector(new BindModule())
}
