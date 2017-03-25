package model

import org.scalatest.{ Matchers, fixture }
import scalikejdbc.DBSession
import scalikejdbc.scalatest.AutoRollback
import skinny.DBSettings
import test.DBTestTrait

/**
 * Created by kazumune on 2017/03/24.
 */
class NoteNotificationSpec extends fixture.FunSpec with AutoRollback with Matchers with DBSettings with DBTestTrait {

  /**
   * テスト用データ登録.
   * @param session DBSession
   */
  override def fixture(implicit session: DBSession) {
    importExcelData(session, Seq("model/NoteNotificationSpec.xlsx"))
  }

  describe("deleteByNotExistsJoinedUser") {
    it("指定されたユーザが存在しない") { implicit session =>
      NoteNotification.findAll().length should be(6L)
      NoteNotification.deleteByNotExistsJoinedUser(10L)
      NoteNotification.findAll().length should be(6L)
    }
    it("指定されたユーザは存在するが、削除対象のデータが存在しない") { implicit session =>
      NoteNotification.findAll().length should be(6L)
      NoteNotification.deleteByNotExistsJoinedUser(1L)
      NoteNotification.findAll().length should be(6L)
    }
    it("指定されたユーザは存在し、削除対象のデータが存在する") { implicit session =>
      NoteNotification.findAll().length should be(6L)
      NoteNotification.findById(4).isDefined should be(true)
      NoteNotification.findById(6).isDefined should be(true)

      NoteNotification.deleteByNotExistsJoinedUser(3L)

      NoteNotification.findAll().length should be(4L)
      NoteNotification.findById(4).isDefined should be(false)
      NoteNotification.findById(6).isDefined should be(false)
    }
  }

}
