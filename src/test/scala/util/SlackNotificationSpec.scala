package util

import org.scalatest._

/**
  * SlackNotificationのテストクラス.
  */
class SlackNotificationSpec extends FunSpec with Matchers {

  describe("send") {
    it("OK status") {
      val actual = SlackNotification.send(
        s"test ${CurrentDateUtil.nowDateTime} <https://alert-system.com/alerts/1234|Click here> \n壊したいぞうさん 28歳",
        "https://hooks.slack.com/services/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"
      )
      actual should be(None)
    }
    it("No service") {
      val actual = SlackNotification.send(
        s"test ${CurrentDateUtil.nowDateTime} <https://alert-system.com/alerts/1234|Click here> \n壊したいぞうさん 28歳",
        "https://hooks.slack.com/services/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"
      )
      actual.get should be((404, "No service"))
    }
    it("Bad token") {
      val actual = SlackNotification.send(
        s"test ${CurrentDateUtil.nowDateTime} <https://alert-system.com/alerts/1234|Click here> \n壊したいぞうさん 28歳",
        "https://hooks.slack.com/services/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"
      )
      actual.get should be((404, "Bad token"))
    }
    it("No active hooks") {
      val actual = SlackNotification.send(
        s"test ${CurrentDateUtil.nowDateTime} <https://alert-system.com/alerts/1234|Click here> \n壊したいぞうさん 28歳",
        "https://hooks.slack.com/services/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"
      )
      actual.get should be((404, "No active hooks"))
    }
  }

}
