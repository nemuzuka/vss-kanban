package test

import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.excel.XlsDataSet
import org.dbunit.operation.DatabaseOperation
import scalikejdbc.DBSession

/**
  * DBに対するテストに対する共通処理.
  */
trait DBTestTrait {
  /***
    * テストデータ登録.
    * 引数のExcelファイルをDBに登録します。
    * @param session DBSession
    * @param paths ExcelファイルパスSeq(classpath上に配置)
    */
  def importExcelData(session: DBSession, paths:Seq[String]): Unit = {
    val dbunitConn = new DatabaseConnection(session.connection)

    paths foreach{ path =>
      val is = getResourceAsStream(path)
      try {
        val dataset = new XlsDataSet(is)
        DatabaseOperation.CLEAN_INSERT.execute(dbunitConn, dataset)
      } finally {
        is.close()
      }
    }
  }

  /**
    * InputStream取得.
    * @param path クラスパス上に存在するファイルパス
    * @return InputStream
    */
  private[this] def getResourceAsStream(path:String) = {
    getClass.getClassLoader.getResourceAsStream(path)
  }
}
