
package controllers

import play.api.db._
import play.api.Play.current 

import FormEncapsulators._;

object Models {
  case class Book(isbn: String, title: String, author: String, copies: Int)
  
  sealed abstract class User(val userid: String)
  case class StudentUser(override val userid: String, name: String, year: Int, branch: String) extends User(userid)
  case class AdminUser(override val userid: String, name: String) extends User(userid)
  
  case class EJournalPublisher(name: String, code: String, url: String)
  case class EJournal(name: String, accessYear: Int, url: String, publisherCode: String)
  case class EBookPublisher(name: String, code: String, url: String)
  case class EBook(name: String, url: String, publisherCode: String)
  case class EDatabase(name: String, url: String)
}

trait DatabaseService {

  import Models._

  init()

  def booksearch(q: BookSearch): List[Book]

  def authenticateStudentUser(q: UserLogin): Option[StudentUser]  
  def authenticateAdminUser(q: UserLogin): Option[AdminUser]
  
  def allEJournalPublishers(): List[EJournalPublisher]  
  def addEJournalPublisher(publisher: EJournalPublisher): Unit  
  def removeEJournalPublisher(publisher: EJournalPublisher): Unit  
  def allEJournals(publisherCode: String): Option[List[EJournal]]  
  def addEJournal(journal: EJournal): Option[Unit]  
  def removeEJournal(journal: EJournal): Option[Unit]
  
  def allEBookPublishers(): List[EBookPublisher]  
  def addEBookPublisher(publisher: EBookPublisher): Unit  
  def removeEBookPublisher(publisher: EBookPublisher): Unit  
  def allEBooks(publisherCode: String): Option[List[EBook]]  
  def addEBook(book: EBook): Option[Unit]  
  def removeEBook(book: EBook): Option[Unit]
  
  def allEDatabases(): List[EDatabase]
  def addEDatabase(book: EDatabase): Unit
  def removeEDatabase(book: EDatabase): Unit

  def init(): Unit
}

trait DatabaseServiceProvider {
  def databaseService: DatabaseService
}