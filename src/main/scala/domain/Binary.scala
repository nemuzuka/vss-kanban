package domain

/**
 * On/Offの2値を示す.
 */
sealed abstract class Binary(override val code: String) extends EnumEntry
object Binary extends Enum[Binary] {
  /** Active. */
  case object Off extends Binary("0")
  /** Archive. */
  case object On extends Binary("1")
  protected val values = Seq(Off, On)
}
