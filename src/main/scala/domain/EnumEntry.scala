package domain

/**
 * Code値を持つEnum要素.
 */
abstract class EnumEntry {
  def code: String = toString
}

/**
 * Code値とLabelを持つEnum要素.
 */
abstract class EnumLabelEntry extends EnumEntry {
  def label: String = toString
}

/**
 * EnumObject基底クラス.
 * @tparam A EnumClass.
 */
abstract class Enum[A <: EnumEntry] {
  /** 全ての要素. */
  protected val values: Seq[A]
  /** 全ての要素のcodeをKeyにして保持するMap. */
  private lazy val codeMap: Map[String, A] = (values map (v => (v.code, v))).toMap

  /**
   * Code値からのEnum要素取得.
   * @param code Code
   * @return 該当EnumEntry
   */
  def withCode(code: String): Option[A] = codeMap get code
}