package application

import domain.ApplicationException
import domain.user.User
import form.kanban.Edit
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
}
