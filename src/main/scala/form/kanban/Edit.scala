package form.kanban

/**
 * かんばん基本情報登録・変更Form.
 * @param id ID
 * @param kanbanTitle かんばんタイトル
 * @param kanbanDescription かんばん説明
 * @param archiveStatus アーカイブステータス
 * @param lockVersion バージョンNo
 */
case class Edit(
  id: String,
  kanbanTitle: String,
  kanbanDescription: String,
  archiveStatus: String,
  lockVersion: String
)
