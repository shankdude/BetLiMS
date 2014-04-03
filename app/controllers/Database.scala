
package controllers

import play.api.db._
import play.api.Play.current 

import FormEncapsulators._;

object Models {
  case class Book(isbn: String, title: String, author: String, copies: Int)
}

trait DatabaseService {

  import Models._

  init()

  def booksearch(q: BookSearch): List[Book]

  def init(): Unit
}

trait DatabaseServiceProvider {
  def databaseService: DatabaseService
}