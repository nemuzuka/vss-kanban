package application

import domain.ApplicationException
import domain.user.User
import form.kanban.{ Edit, JoinedUser }
import scalikejdbc.DBSession

/**
 * かんばん管理者用機能のApplicationService.
 */
trait KanbanAdminService {

  /**
   * 基本情報Form取得.
   * @param id かんばんID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 基本情報Form(存在しない場合、None)
   */
  def getForm(id: Long, loginUser: User)(implicit session: DBSession): Option[Edit]

  /**
   * 基本情報更新.
   * かんばんの管理者権限を所有するユーザのみ操作を受け付けます
   * @param form 基本情報Form
   * @param loginUser ログインユーザ
   * @param session Session
   * @return Right:かんばんID, Left:エラー情報
   */
  def updateKanban(form: Edit, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long]

  /**
   * 参加者情報取得.
   * @param id かんばんID
   * @param loginUser ログインユーザ情報
   * @param session Session
   * @return 参加者情報Dto(存在しない場合、None)
   */
  def getJoinedUser(id: Long, loginUser: User)(implicit session: DBSession): Option[JoinedUserTargetDto]

  /**
   * 参加者情報更新.
   * カンバン参加者IDSeqに存在しないユーザは、管理者として設定されていても参加者にはなりません
   * かんばんの管理者権限を所有するユーザのみ操作を受け付けます
   * @param form 参加者情報変更Form
   * @param loginUser ログインユーザ
   * @param session Session
   * @return Right:かんばんID, Left:エラー情報
   */
  def updateJoinedUser(form: JoinedUser, loginUser: User)(implicit session: DBSession): Either[ApplicationException, Long]

}

/**
 * 参加者情報Dto.
 * @param id かんばんID
 * @param lockVersion バージョンNo
 * @param joinedUsers 参加者Seq
 * @param allUsers 全ユーザSeq
 */
case class JoinedUserTargetDto(
  id: Long,
  lockVersion: Long,
  joinedUsers: Seq[JoinedUserDto],
  allUsers: Seq[UserDto]
)

/**
 * 参加者Dto.
 * @param userId ユーザID
 * @param authority 権限
 */
case class JoinedUserDto(
  userId: Long,
  authority: String
)