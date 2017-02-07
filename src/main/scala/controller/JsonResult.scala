package controller

/**
 * Json戻り値保持Class.
 * @param msgs メッセージ(存在しない場合、空Seq)
 * @param errorMsg エラーメッセージ(存在しない場合、空Map)
 * @param result 結果
 */
case class JsonResult(
  msgs: Seq[String],
  errorMsg: Map[String, Seq[String]],
  result: AnyRef
)