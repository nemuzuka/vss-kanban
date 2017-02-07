package util

import scala.language.reflectiveCalls

/**
 * LoanPattern用Support.
 */
trait LoanSupport {
  //streamのclose用
  def using[A, R <: { def close() }](r: R)(f: R => A): A =
    try {
      f(r)
    } finally {
      r.close()
    }
}
