package controllers

import play.api.db.slick.DB
import play.api.Application
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag

trait SlickDatabaseTables {

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

  import Models._
  import FormEncapsulators._

  val application: Application
  val name: String

  def insertBooks(b: Seq[Book]) {
    DB withSession { implicit session => 
      tables.books ++= b
    }
  }

  override def booksearch(q: BookSearch): List[Book] = {
    DB withSession { implicit session =>
      val v0 = tables.books

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
    DB withSession { implicit session =>
      import scala.slick.jdbc.meta._

      if (MTable.getTables(tables.booksTableName).list().isEmpty) {
        tables.books.ddl.create
      }
    }
  }
}

object SlickDatabaseUtil {
  def getDBUtil(_name: String = "default")(implicit app: Application): DatabaseService = {
    new {
      val application = app
      val name = _name
    } with SlickDatabaseTables with SlickDatabaseService
  }
}
