
package controllers

import play.api.db._
import play.api.Play.current 

import java.sql.{Date => SqlDate}

import FormEncapsulators._;

object Models {
  case class Book(isbn: String, title: String, author: String, publisher: String,
                  edition: Int, publishYear: Int, pages: Int, callNo: String)
  case class BookVariables(isbn: String, copies: Int, references: Int, checkouts: Int)
  case class BookPurchase(refID: Int, date: SqlDate)
  case class BookPurchaseDetails(refID: Int, isbn: String, copies: Int, references: Int)
  
  case class IssueEntry(date: SqlDate, isbn: String, userid: String)
  case class ReturnEntry(issueDate: SqlDate, isbn: String, userid: String, returnDate: SqlDate)
  
  sealed abstract class User(val userid: String)
  case class StudentUser(override val userid: String, name: String, year: Int, branch: String) extends User(userid)
  case class AdminUser(override val userid: String, name: String) extends User(userid)
  
  case class EJournalPublisher(name: String, code: String, url: String)
  case class EJournal(name: String, accessYear: Int, url: String, publisherCode: String)
  case class EBookPublisher(name: String, code: String, url: String)
  case class EBook(name: String, url: String, publisherCode: String)
  case class EDatabase(name: String, url: String)
  
  object AdminConstants {
    val issueRequestTime  = "IssueRequestTime"
    val bookIssueLimit    = "BookIssueLimit"
    val returnDaysLimit   = "ReturndaysLimit"
  }
}

trait DatabaseService {

  import Models._

  init()

  def booksearch(q: BookSearch): List[(Book, BookVariables)]
  def addBooks(book: Seq[Book]): Unit
  def purchaseBook(details: Seq[BookPurchaseDetails]): Unit

  def issueRequest(isbn: String, userid: String): Either[String, Unit]
  def issueBook(isbn: String, userid: String): Either[String, Unit]
  def returnBook(isbn: String, userid: String): Either[String, Unit]
  def issueList(): List[IssueEntry]
  def issueRequestList(): List[IssueEntry]
  def returnList(): List[ReturnEntry]
  def issueList(userid: String): List[IssueEntry]
  def issueRequestList(userid: String): List[IssueEntry]
  def returnList(userid: String): List[ReturnEntry]
  
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

trait DatabaseServiceMessages {
  val REQUEST_PENDING = "A request for the book is already pending"
  val USER_LIMIT_REACHED = "User Limit for Book Issues reached"
  val NO_MORE_COPIES = "No more copies of the book left"
  val NO_ISSUE_FOUND = "No issue entry for the reuested book and user found"
}

trait DatabaseServiceProvider {
  def databaseService: DatabaseService
}