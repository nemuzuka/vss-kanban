package infrastructure.user

import com.google.inject._
import _root_.test.DBTestTrait
import module.BindModule
import org.scalatest._
import scalikejdbc.DBSession
import scalikejdbc.scalatest._
import skinny._

/**
 * Created by kazumune on 2017/03/11.
 */
class UserRepositoryImplSpec extends fixture.FunSpec with AutoRollback with Matchers with DBSettings with DBTestTrait with BeforeAndAfter {

  @Inject
  private val userRepositoryImpl: UserRepositoryImpl = null

  //前処理
  before {
    Guice.createInjector(new BindModule()).injectMembers(this)
  }

  /**
   * *
   * テスト用データ登録.
   * @param session DBSession
   */
  override def fixture(implicit session: DBSession) {
    importExcelData(session, Seq("infrastructure/user/UserRepositoryImplSpec.xlsx"))
  }

  describe("findAll") {
    it("全件取得") { implicit session =>
      val actual = userRepositoryImpl.findAll
      actual.size should be(3L)
      actual.head.userId.get.id should be(4L)
      actual(1).userId.get.id should be(2L)
      actual(2).userId.get.id should be(3L)
    }
  }
}
