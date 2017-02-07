package controller

import domain.user.UserAuthority

import scala.util.control.Breaks.{ break, breakable }
import domain.{ Enum, EnumEntry }

/**
 * Controller共通処理のFeature.
 */
trait CommonControllerFeature {
  //ログインチェックを行う場合、true
  val loginCheck: Boolean = true
  //複数権限設定時の比較方法(デフォルト:and)
  val authenticationCheck: ApplicationAuthenticationCheck = ApplicationAuthenticationCheck.And

  //チェック対象権限Seq(チェックしない場合、None)
  val authentications: Option[Seq[UserAuthority]]

  /**
   * 権限チェック.
   * @param userAuths ログインユーザ保持権限
   * @param authentications チェック対象権限Seq
   * @param authenticationCheck 複数権限設定時の比較方法
   * @return 使用権限を満たしている場合、true
   */
  def executeAuthenticationsCheck(
    userAuths: Seq[UserAuthority],
    authentications: Option[Seq[UserAuthority]],
    authenticationCheck: ApplicationAuthenticationCheck
  ): Boolean = {

    authentications match {
      case Some(seq) =>
        //チェック実施
        var res = false
        breakable {
          for (authentication <- seq) {
            if (!userAuths.contains(authentication)) {
              //含まない場合
              if (authenticationCheck == ApplicationAuthenticationCheck.And) {
                //andの場合、以下の処理を行っても意味が無いので終了
                res = false
                break
              }
            } else {
              //含む場合
              res = true
              //orの場合、1つだけでも存在すれば良いので終了
              if (authenticationCheck == ApplicationAuthenticationCheck.Or) break
            }
          }
        }
        res
      case None =>
        //チェックせずにOK
        true
    }
  }
}

//ユーザに対する権限
sealed abstract class ApplicationAuthenticationCheck(override val code: String) extends EnumEntry
object ApplicationAuthenticationCheck extends Enum[ApplicationAuthenticationCheck] {
  /** 全て保有. */
  case object And extends ApplicationAuthenticationCheck("and")
  /** いずれか1つ保有. */
  case object Or extends ApplicationAuthenticationCheck("or")
  protected val values = Seq(And, Or)
}
