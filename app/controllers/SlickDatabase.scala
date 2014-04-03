
package controllers

import javax.sql.DataSource
import play.api.db.DB
import play.api.Application
import scala.slick.driver.JdbcProfile

trait SlickDatabaseTables {

  val profile: JdbcProfile

  import profile.simple._
  import Models._

  val booksTableName = "books"
  val books = TableQuery[BookTable]
  class BookTable(tag: Tag) extends Table[Book](tag, booksTableName) {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def title = column[String]("title")
    def author = column[String]("author")
    def copies = column[Int]("copies")

    def * = (isbn, title, author, copies) <> (Book.tupled, Book.unapply)
  }

}

trait SlickDatabaseService extends DatabaseService {
  tables: SlickDatabaseTables =>

  def dataSource: DataSource

  import profile.simple._
  import Models._
  import FormEncapsulators._

  override def booksearch(q: BookSearch): List[Book] = {
    inDatabase { implicit session =>
      val v0 = books

      val v1 = q.isbn match {
        case Some(x) => v0.filter(y => y.isbn.like("%" + x + "%"))
        case None => v0
      }
      val v2 = q.title match {
        case Some(x) => v1.filter(y => y.title.like("%" + x + "%"))
        case None => v1
      }
      val v3 = q.author match {
        case Some(x) => v2.filter(y => y.author.like("%" + x + "%"))
        case None => v2
      }

      v3.list
    }
  }

  override def init() {
    /*inDatabase { implicit session =>
      import scala.slick.jdbc.meta._

      if (MTable.getTables(booksTableName).list().isEmpty) {
        books.ddl.create
      }
    }*/
  }

  def inDatabase[R](f: (Session) => R): R = {
    Database.forDataSource(dataSource) withSession {
      session => f(session)
    }
  }
}

object SlickDatabaseUtil {
  val SLICK_DRIVER = "db.%s.slickdriver"
  val DEFAULT_SLICK_DRIVER = "scala.slick.driver.H2Driver"

  def getDBUtil(name: String = "default")(implicit app: Application): DatabaseService = {
    val driverClass = app.configuration.getString(SLICK_DRIVER.format(name)).
      getOrElse(DEFAULT_SLICK_DRIVER)
    val driver = singleton[JdbcProfile](driverClass)
    new SlickDatabaseService with SlickDatabaseTables {
      override lazy val profile = driver
      override def dataSource = DB.getDataSource(name)
    }
  }

  private def singleton[T](name: String)(implicit man: Manifest[T]): T = {
    import scala.reflect.runtime.{ currentMirror => cm }
    import scala.reflect.runtime.universe._

    val moduleSymbol = cm.moduleSymbol(Class.forName(name))
    val moduleMirror = cm.reflectModule(moduleSymbol)

    moduleMirror.instance.asInstanceOf[T]
  }
}
