package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class NoteComment(
  id: Long,
  noteId: Long,
  commentText: String,
  createLoginUserInfoId: Long,
  createAt: DateTime
)

object NoteComment extends SkinnyCRUDMapper[NoteComment] {
  override lazy val tableName = "note_comment"
  override lazy val defaultAlias = createAlias("nc")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[NoteComment]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[NoteComment]): NoteComment = new NoteComment(
    id = rs.get(rn.id),
    noteId = rs.get(rn.noteId),
    commentText = rs.get(rn.commentText),
    createLoginUserInfoId = rs.get(rn.createLoginUserInfoId),
    createAt = rs.get(rn.createAt)
  )

  /**
   * 登録.
   * @param entity 対象Entity
   * @param session Session
   * @return 生成ID
   */
  def create(entity: NoteComment)(implicit session: DBSession): Long = {
    NoteComment.createWithAttributes(
      'noteId -> entity.noteId,
      'commentText -> entity.commentText,
      'createLoginUserInfoId -> entity.createLoginUserInfoId,
      'createAt -> entity.createAt
    )
  }
}
