package form.user

/**
 * ユーザ登録・変更用Form.
 * @param id ユーザID
 * @param loginId ログインID
 * @param userName ユーザ名
 * @param password パスワード
 * @param authority ユーザ権限(1:アプリケーション管理者、0:一般)
 * @param lockVersion バージョンNo
 */
case class Edit(
  id: String,
  loginId: String,
  userName: String,
  password: String = "",
  authority: String = "0",
  lockVersion: String = "0"
)