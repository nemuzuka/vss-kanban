package form.kanban

/**
 * かんばん参加者変更Form.
 * @param id かんばんID
 * @param lockVersion バージョンNo
 * @param joinedUserIds かんばん参加者IDSeq
 * @param adminUserIds かんばん管理者IDSeq
 */
case class JoinedUser(
  id: Long,
  lockVersion: Long,
  joinedUserIds: Seq[Long],
  adminUserIds: Seq[Long]
)
