package module

import application._
import application.impl._
import com.google.inject._
import domain.user.UserRepository
import infrastructure.user.UserRepositoryImpl

/**
 * DI設定Module.
 */
class BindModule extends AbstractModule {
  /**
   * DI設定定義
   */
  override def configure() = {
    //traitに対するインスタンスの解決を定義
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl]).in(classOf[Singleton])

    bind(classOf[MaintenanceService]).to(classOf[MaintenanceServiceImpl]).in(classOf[Singleton])
  }
}

/**
 * DI設定インスタンス管理.
 */
object BindModule {
  val injector: Injector = Guice.createInjector(new BindModule())
}
