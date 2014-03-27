
package controllers

import play.api.db._

import scala.slick.driver.H2Driver.simple._

object Database {
  import DatabaseTables._
  
  val books = TableQuery[Book]
}

object DatabaseTables {
  class Book(tag: Tag) extends Table[(String, String, String, Int)](tag, "") {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def title = column[String]("isbn")
    def author = column[String]("isbn")
    def copies = column[Int]("isbn")

    def * = (isbn, title, author, copies)
  }
}
