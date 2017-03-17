package util

import org.scalatest._

class PasswordDigestUtilSpec extends FunSpec with Matchers {

  describe("createHashPassword") {
    it("same value") {

      val baseDate = CurrentDateUtil.nowDateTime
      val beforeStr = PasswordDigestUtil.createHashPassword("hoge", baseDate)
      val afterStr = PasswordDigestUtil.createHashPassword("hoge", baseDate)
      beforeStr should be(afterStr)
    }
  }

}
