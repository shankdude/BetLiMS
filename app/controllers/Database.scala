
package controllers

import play.api.db._
import play.api.Play.current 

import scala.slick.driver.H2Driver.simple._

import FormEncapsulators._;

object DatabaseUtil {
  
  import DatabaseTables._
  import Models._
  
  val books = TableQuery[BookTable]
  
  def booksearch(q: BookSearch): List[Book] = {
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
  
  private def inDatabase[R](f: (Session) => R): R = {
    val ds = DB.getDataSource()
    Database.forDataSource(ds) withSession {
      session => f(session)
    }
  }
}

object DatabaseTables {
   import Models._;
  
  class BookTable(tag: Tag) extends Table[Book](tag, "") {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def title = column[String]("isbn")
    def author = column[String]("isbn")
    def copies = column[Int]("isbn")

    def * = (isbn, title, author, copies) <> (Book.tupled, Book.unapply)
  }
}

object Models {
  case class Book(isbn: String, title: String, author: String, copies: Int)
}