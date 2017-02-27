package controller.kanban

import application.NoteService
import controller.{ ApiController, Keys }
import domain.kanban.{ KanbanId, LaneId, NoteId, NoteRepository }
import domain.user.User
import form.kanban.Note
import scalikejdbc.DB
import skinny.validator._

/**
 * ふせんの登録・変更・削除のController.
 */
class NoteEditController extends ApiController {
  protectFromForgery()
  override val authentications = None

  /**
   * 登録・変更情報取得.
   */
  def form: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val laneId = params.getAs[Long]("laneId").getOrElse(-1L)
    val noteId = params.getAs[String]("noteId").getOrElse("")
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val noteService = injector.getInstance(classOf[NoteService])
      noteService.getForm(
        KanbanId(kanbanId),
        LaneId(laneId), if (noteId.isEmpty) None else Option(NoteId(noteId.toLong)), userInfo
      )
    } match {
      case Some(form) =>
        createJsonResult(form)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 登録・変更.
   */
  def store: String = {
    validateAndCreateForm match {
      case Right(form) =>
        val userInfo = session.getAs[User](Keys.Session.UserInfo).get

        import scalikejdbc.TxBoundary.Either._
        DB.localTx { implicit session =>
          val noteService = injector.getInstance(classOf[NoteService])
          noteService.storeNote(form, userInfo)
        } match {
          case Right(noteId) =>
            createJsonResult("success", Result(noteId = noteId))
          case Left(exception) =>
            val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
            createJsonResult(errorMsg)
        }

      case Left(v) => createJsonResult(v)
    }
  }

  /**
   * 詳細情報取得.
   */
  def detail: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val laneId = params.getAs[Long]("laneId").getOrElse(-1L)
    val noteId = params.getAs[Long]("noteId").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    DB localTx { implicit session =>
      val noteService = injector.getInstance(classOf[NoteService])
      noteService.getDetail(KanbanId(kanbanId), LaneId(laneId), NoteId(noteId), userInfo)
    } match {
      case Some(detail) =>
        createJsonResult(detail)
      case _ =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, "noData", Seq())
        createJsonResult(errorMsg)
    }
  }

  /**
   * 削除.
   */
  def delete: String = {
    val kanbanId = params.getAs[Long]("kanbanId").getOrElse(-1L)
    val noteId = params.getAs[Long]("noteId").getOrElse(-1L)
    val lockVersion = params.getAs[Long]("lockVersion").getOrElse(-1L)
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    import scalikejdbc.TxBoundary.Either._
    DB.localTx { implicit session =>
      val noteService = injector.getInstance(classOf[NoteService])
      noteService.deleteById(KanbanId(kanbanId), NoteId(noteId), lockVersion, userInfo)
    } match {
      case Right(noteId) =>
        createJsonResult("success", Result(noteId = noteId))
      case Left(exception) =>
        val errorMsg = createErrorMsg(Keys.ErrMsg.Key, exception.messageKey, exception.paramKey)
        createJsonResult(errorMsg)
    }
  }

  /**
   * コメント登録.
   */
  def commentStore(): String = {
    val userInfo = session.getAs[User](Keys.Session.UserInfo).get

    val noteId = params.getAs[Long]("noteId").getOrElse(-1L)
    val laneId = params.getAs[Long]("laneId").getOrElse(-1L)
    val attachmentFileIds = multiParams.getAs[Long]("attachmentFileIds").getOrElse(Seq())
    val comment = params.getAs[String]("comment").getOrElse("")

    DB.localTx { implicit session =>
      val noteRepository = injector.getInstance(classOf[NoteRepository])
      noteRepository.store(LaneId(laneId), NoteId(noteId), comment, attachmentFileIds, userInfo)
    }
    createJsonResult("success")
  }

  /**
   * ふせん移動.
   */
  def move: String = {
    val noteId = params.getAs[String]("noteId").getOrElse("")
    val laneId = params.getAs[Long]("laneId").getOrElse(-1L)
    val noteIds = multiParams.getAs[Long]("noteIds").getOrElse(Seq())

    DB.localTx { implicit session =>
      val noteService = injector.getInstance(classOf[NoteService])
      noteService.moveNote(LaneId(laneId), if (noteId.isEmpty) None else Option(NoteId(noteId.toLong)), noteIds)
    }
    createJsonResult("success")
  }

  /**
   * validate & 入力パラメータ取得.
   *
   * @return Right:入力Form / Left:validateエラーメッセージ
   */
  private[this] def validateAndCreateForm: Either[Map[String, Seq[String]], Note] = {
    val validator = Validator(
      param("id" -> params("id")) is longValue,
      param("kanbanId" -> params("kanbanId")) is longValue,
      param("laneId" -> params("laneId")) is longValue,
      param("noteTitle" -> params("noteTitle")) is required & maxLength(256),
      param("fixDate" -> params("fixDate")) is dateFormat,
      param("lockVersion" -> params("lockVersion")) is longValue
    )
    if (validator.hasErrors) Left(createValidateErrorMsg(validator)) else {
      Right(Note(
        id = params.getAs[String]("id").getOrElse(""),
        kanbanId = params.getAs[String]("kanbanId").getOrElse(""),
        laneId = params.getAs[String]("laneId").getOrElse(""),
        lockVersion = params.getAs[String]("lockVersion").getOrElse(""),
        noteTitle = params.getAs[String]("noteTitle").getOrElse(""),
        noteDescription = params.getAs[String]("noteDescription").getOrElse(""),
        archiveStatus = params.getAs[String]("archiveStatus").getOrElse(""),
        fixDate = params.getAs[String]("fixDate").getOrElse(""),
        chargedUserIds = multiParams.getAs[Long]("chargedUserIds").getOrElse(Seq()),
        attachmentFileIds = multiParams.getAs[Long]("attachmentFileIds").getOrElse(Seq())
      ))
    }
  }

  /**
   * JSON戻り値.
   * @param noteId ふせんID
   */
  case class Result(noteId: Long)
}
