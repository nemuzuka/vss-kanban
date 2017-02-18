package controller

import skinny.controller.SkinnyServlet
import skinny.controller.feature.{ CSRFProtectionFeature, FileUploadFeature }

/**
 * AjaxによるファイルアップロードControllerの基底クラス.
 */
class UploadController extends SkinnyServlet with FileUploadFeature with DiInjector with CSRFProtectionFeature {

  addErrorFilter {
    case e: Throwable =>
      logger.error(e.getMessage, e)
      halt(500)
  }

}
