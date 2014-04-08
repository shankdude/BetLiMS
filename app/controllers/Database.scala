
package controllers

import play.api.db._
import play.api.Play.current 

import FormEncapsulators._;

object Models {
  case class Book(isbn: String, title: String, author: String, copies: Int)
  sealed abstract class User(val userid: String)
  case class StudentUser(userid: String, name: String, year: Int, branch: String) extends User(userid)
}

trait DatabaseService {

  import Models._

  init()

  def booksearch(q: BookSearch): List[Book]

  def authenticateUser(q: UserLogin): Option[User]

  def init(): Unit
}

trait DatabaseServiceProvider {
  def databaseService: DatabaseService
}