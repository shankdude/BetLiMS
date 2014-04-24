package controllers

import play.api.db.slick.DB
import play.api.Application
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag

import java.sql.{Date => SqlDate}

trait SlickDatabaseTables {

  import Models._

  val booksTableName = "books"
  val books = TableQuery[BookTable]
  class BookTable(tag: Tag) extends Table[Book](tag, booksTableName) {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def title = column[String]("title")
    def author = column[String]("author")
    def publisher = column[String]("publisher")
    def callNo = column[String]("call_number")
    def edition = column[Int]("edition")
    def publishYear = column[Int]("publish_year")
    def pages = column[Int]("pages")
    
    def variables = foreignKey(bookVariablesTableName, isbn, bookVariables)(_.isbn)

    def * = (isbn, title, author, publisher, edition, publishYear, pages,
      callNo) <> (Book.tupled, Book.unapply)
  }

  val bookVariablesTableName = "book_variables"
  val bookVariables = TableQuery[BookVariablesTable]
  class BookVariablesTable(tag: Tag) extends Table[BookVariables](tag, bookVariablesTableName) {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def copies = column[Int]("copies")
    def references = column[Int]("references")
    def checkouts = column[Int]("checkouts")

    def book = foreignKey(booksTableName, isbn, books)(_.isbn)

    def * = (isbn, copies, references, checkouts) <> (BookVariables.tupled, BookVariables.unapply)
  }

  val bookPurchasesTableName = "book_purchase"
  val bookPurchases = TableQuery[BookPurchaseTable]
  class BookPurchaseTable(tag: Tag) extends Table[BookPurchase](tag, bookPurchasesTableName) {
    def refID = column[Int]("ref_id", O.PrimaryKey, O.AutoInc)
    def date = column[SqlDate]("date")

    def * = (refID, date) <> (BookPurchase.tupled, BookPurchase.unapply)
  }

  val bookPurchaseDetailsTableName = "book_purchase_details"
  val bookPurchaseDetails = TableQuery[BookPurchaseDetailsTable]
  class BookPurchaseDetailsTable(tag: Tag) extends Table[BookPurchaseDetails](tag, bookPurchaseDetailsTableName) {
    def refID = column[Int]("ref_id")
    def isbn = column[String]("isbn")
    def copies = column[Int]("copies")
    def references = column[Int]("references")
    
    def key = primaryKey("key", (refID, isbn))

    def * = (refID, isbn, copies, references) <> (BookPurchaseDetails.tupled, BookPurchaseDetails.unapply)
  }

  val studentsTableName = "student_user"
  val students = TableQuery[StudentTable]
  class StudentTable(tag: Tag) extends Table[StudentUser](tag, studentsTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def name = column[String]("name")
    def branch = column[String]("branch")
    def year = column[Int]("year")

    def * = (userid, name, year, branch) <> (StudentUser.tupled, StudentUser.unapply)
  }
  
  val issueHistoryTableName = "issue_history"
  val issueHistory = TableQuery[IssueHistoryTable]
  class IssueHistoryTable(tag: Tag) extends Table[IssueEntry](tag, issueHistoryTableName) {
    def isbn = column[String]("isbn")
    def userid = column[String]("user_id")
    def date = column[SqlDate]("date")
    
    def * = (date, isbn, userid) <> (IssueEntry.tupled, IssueEntry.unapply)
  }
  
  val issueRequestHistoryTableName = "issue_request_history"
  val issueRequestHistory = TableQuery[IssueRequestHistoryTable]
  class IssueRequestHistoryTable(tag: Tag) extends Table[IssueEntry](tag, issueRequestHistoryTableName) {
    def isbn = column[String]("isbn")
    def userid = column[String]("user_id")
    def date = column[SqlDate]("date")
    
    def * = (date, isbn, userid) <> (IssueEntry.tupled, IssueEntry.unapply)
  }
  
  val returnHistoryTableName = "return_history"
  val returnHistory = TableQuery[ReturnHistoryTable]
  class ReturnHistoryTable(tag: Tag) extends Table[ReturnEntry](tag, returnHistoryTableName) {
    def isbn = column[String]("isbn")
    def userid = column[String]("user_id")
    def issueDate = column[SqlDate]("issue_date")
    def returnDate = column[SqlDate]("return_date")
    
    def * = (issueDate, isbn, userid, returnDate) <> (ReturnEntry.tupled, ReturnEntry.unapply)
  }

  val adminsTableName = "admin_user"
  val admins = TableQuery[AdminTable]
  class AdminTable(tag: Tag) extends Table[AdminUser](tag, adminsTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (userid, name) <> (AdminUser.tupled, AdminUser.unapply)
  }

  val studentUsersAuthTableName = "student_user_auth"
  val studentUsersAuth = TableQuery[StudentUserAuthTable]
  class StudentUserAuthTable(tag: Tag) extends Table[(String, String)](tag, studentUsersAuthTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def password = column[String]("pass")
    def student = foreignKey("student_user", userid, students)(_.userid)

    def * = (userid, password)
  }


  val adminUsersAuthTableName = "admin_user_auth"
  val adminUsersAuth = TableQuery[AdminUserAuthTable]
  class AdminUserAuthTable(tag: Tag) extends Table[(String, String)](tag, adminUsersAuthTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def password = column[String]("pass")
    def admin = foreignKey("admin_user", userid, admins)(_.userid)

    def * = (userid, password)
  }

  val ejournalPublishersTableName = "ejournal_publisher"
  val ejournalPublishers = TableQuery[EJournalPublisherTable]
  class EJournalPublisherTable(tag: Tag) extends Table[EJournalPublisher](tag, ejournalPublishersTableName) {
    def name = column[String]("name")
    def code = column[String]("code", O.PrimaryKey)
    def url = column[String]("url")

    def * = (name, code, url) <> (EJournalPublisher.tupled, EJournalPublisher.unapply)
  }

  val ejournalsTableName = "ejournals"
  val ejournals = TableQuery[EJournalTable]
  class EJournalTable(tag: Tag) extends Table[EJournal](tag, ejournalsTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def accessYear = column[Int]("access_from")
    def url = column[String]("url")
    def publisherCode = column[String]("publisher_code")
    def publisher = foreignKey(ejournalPublishersTableName, publisherCode,
      ejournalPublishers)(_.code)

    def * = (name, accessYear, url, publisherCode) <> (EJournal.tupled, EJournal.unapply)
  }

  val ebookPublishersTableName = "ebook_publisher"
  val ebookPublishers = TableQuery[EBookPublisherTable]
  class EBookPublisherTable(tag: Tag) extends Table[EBookPublisher](tag, ebookPublishersTableName) {
    def name = column[String]("name")
    def code = column[String]("code", O.PrimaryKey)
    def url = column[String]("url")

    def * = (name, code, url) <> (EBookPublisher.tupled, EBookPublisher.unapply)
  }

  val ebooksTableName = "ebooks"
  val ebooks = TableQuery[EBookTable]
  class EBookTable(tag: Tag) extends Table[EBook](tag, ebooksTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def url = column[String]("url")
    def publisherCode = column[String]("publisher_code")
    def publisher = foreignKey(ebookPublishersTableName, publisherCode,
      ebookPublishers)(_.code)

    def * = (name, url, publisherCode) <> (EBook.tupled, EBook.unapply)
  }

  val edatabasesTableName = "edatabases"
  val edatabases = TableQuery[EDatabaseTable]
  class EDatabaseTable(tag: Tag) extends Table[EDatabase](tag, edatabasesTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def url = column[String]("url")

    def * = (name, url) <> (EDatabase.tupled, EDatabase.unapply)
  }
}

trait SlickDatabaseService extends DatabaseService with DatabaseServiceMessages {
  tables: SlickDatabaseTables =>

  import Models._
  import FormEncapsulators._

  implicit val application: Application
  val name: String = "default"

  override def addBooks(books: Seq[Book]) {
    DB withSession { implicit session =>
      for (b <- books) {
        tables.books += b
        tables.bookVariables += BookVariables(b.isbn, 0, 0, 0)
      }
    }
  }

  override def purchaseBook(_details: Seq[BookPurchaseDetails]) = DB(name) withTransaction {
  implicit session =>  
    val date = new SqlDate(System.currentTimeMillis)
    val refID = (bookPurchases returning bookPurchases.map(_.refID)) += BookPurchase(-1, date)
    val details = _details map (d => BookPurchaseDetails(refID, d.isbn, d.copies, d.references))
    for (d <- details) {
      tables.bookPurchaseDetails += d
      val q = tables.bookVariables.filter(_.isbn === d.isbn).map(bv => (bv.copies, bv.references))
      val present = q.list.head
      q.update((present._1 + d.copies, present._2 + d.references))
    }
  }

  override def booksearch(q: BookSearch): List[(Book, BookVariables)] = {
    DB(name) withSession { implicit session =>
      val v0 = tables.books

      val v1 = q.isbn match {
        case Some(x) => v0.filter{ _.isbn.like("%" + x + "%") }
        case None    => v0
      }
      val v2 = q.title match {
        case Some(x) => v1.filter{ _.title.like("%" + x + "%") }
        case None    => v1
      }
      val v3 = q.author match {
        case Some(x) => v2.filter{ _.author.like("%" + x + "%") }
        case None    => v2
      }
      val v4 = q.publisher match {
        case Some(x) => v3.filter{ _.publisher.like("%" + x + "%") }
        case None    => v3
      }
      val v5 = q.edition match {
        case Some(x) => v4.filter{ _.edition === x }
        case None    => v4
      }
      val v6 = q.publishYear match {
        case Some(x) => v5.filter{ _.publishYear === x }
        case None    => v5
      }
      println(v6.list)
      val v7 = for {
        b <- v6
        bv <- bookVariables.filter(_.isbn === b.isbn)
      } yield(b, bv)
      v7.list
    }
  }

  def issueRequest(isbn: String, userid: String) = DB(name) withSession { implicit session =>
    val userIssued = tables.issueHistory.filter(_.userid === userid).list.length
    if (userIssued < 3) {
      val today = new SqlDate(System.currentTimeMillis)
      val issued = tables.issueHistory.filter(_.isbn === isbn).list.length
      val total = tables.bookVariables.filter(_.isbn === isbn).map(_.copies).list.head
      val issueReq = tables.issueRequestHistory.filter(_.isbn === isbn).list.filter {
        today.getTime - _.date.getTime < 500000
      }
      if (issued == total) {
        Left(NO_MORE_COPIES)
      } else if (issued + issueReq.length < total) {
        tables.issueRequestHistory += IssueEntry(today, isbn, userid)
        Right()
      } else {
        Left(REQUEST_PENDING)
      }
    } else {
      Left(USER_LIMIT_REACHED)
    }
  }
  
  def issueBook(isbn: String, userid: String) = DB(name) withSession { implicit session =>
    val userIssued = tables.issueHistory.filter(_.userid === userid).list.length
    if (userIssued < 3) {
      val today = new SqlDate(System.currentTimeMillis)
      val issued = tables.issueHistory.filter(_.isbn === isbn).list.length
      val total = tables.bookVariables.filter(_.isbn === isbn).map(_.copies).list.head
      val issueReqUsrQ = tables.issueRequestHistory.filter(ie => ie.isbn === isbn && ie.userid === userid)
      val issueReqAll = tables.issueRequestHistory.filter(ie => ie.isbn === isbn && ie.userid =!= userid).list.filter {
        today.getTime - _.date.getTime < 500000
      }
      if (issued == total) {
        Left(NO_MORE_COPIES)
      } else if (issued + issueReqAll.length < total) {
        issueReqUsrQ.delete
        tables.issueHistory += IssueEntry(today, isbn, userid)
        Right()
      } else {
        Left(REQUEST_PENDING)
      }
    } else {
       Left(USER_LIMIT_REACHED)
    }
  }
  
  def returnBook(isbn: String, userid: String) = DB(name) withSession { implicit session =>
    val issueQ = tables.issueHistory.filter(ie => ie.isbn === isbn && ie.userid === userid)
    issueQ.list.headOption match {
      case Some(issue) =>
        val today = new SqlDate(System.currentTimeMillis)
        issueQ.delete
        tables.returnHistory += ReturnEntry(issue.date, issue.isbn, issue.userid, today)
        Right()
      case None => Left(NO_ISSUE_FOUND)
    }
  }
  
  def issueList() = DB(name) withSession { implicit session =>
    tables.issueHistory.list
  }
  
  def issueRequestList() = DB(name) withSession { implicit session =>
    val today = new SqlDate(System.currentTimeMillis)
    tables.issueRequestHistory.list.filter(today.getTime - _.date.getTime < 500000)
  }
  
  def returnList() = DB(name) withSession { implicit session =>
    tables.returnHistory.list
  }
  
  def issueList(userid: String) = DB(name) withSession { implicit session =>
    tables.issueHistory.filter(_.userid === userid).list
  }
  
  def issueRequestList(userid: String) = DB(name) withSession { implicit session =>
    val today = new SqlDate(System.currentTimeMillis)
    tables.issueRequestHistory.filter(_.userid === userid).list.filter {
      today.getTime - _.date.getTime < 500000
    }
  }
  
  def returnList(userid: String) = DB(name) withSession { implicit session =>
    tables.returnHistory.filter(_.userid === userid).list
  }
  
  def booksList() = DB(name) withSession { implicit session => 
    val q = for {
      b <- tables.books
      bv <- b.variables
    } yield (b, bv)
    q.list
  }
  
  def studentUsersList() = DB(name) withSession { implicit session => 
    tables.students.list
  }
  
  def adminUsersList() = DB(name) withSession { implicit session => 
    tables.admins.list
  }
  
  def bookInfo(isbn: String) = DB(name) withSession { implicit session => 
    val q = for {
      b <- tables.books if (b.isbn === isbn)
      bv <- b.variables
    } yield (b, bv)
    q.list.headOption
  }
  
  def studentUserInfo(userid: String) = DB(name) withSession { implicit session => 
    tables.students.filter(_.userid === userid).list.headOption
  }
  
  def adminUserInfo(userid: String) = DB(name) withSession { implicit session => 
    tables.admins.filter(_.userid === userid).list.headOption
  }
  
  def insertUser(u: Seq[(User, String)]) {
    DB(name) withSession { implicit session =>
      for ((user, pass) <- u) {
        user match {
          case s: StudentUser =>
            tables.students += s
            tables.studentUsersAuth += (user.userid, encryptPassword(pass))
          case a: AdminUser   => 
            tables.admins += a
            tables.adminUsersAuth += (user.userid, encryptPassword(pass))
        }
      }
    }
  }

  override def authenticateAdminUser(q: UserLogin): Option[AdminUser] = {
    DB(name) withSession { implicit session =>
      val a = for {
        aAuth <- adminUsersAuth filter (_.userid === q.username) if (matchPasswords(aAuth.password, q.password))
        admin <- aAuth.admin
      } yield admin
      //Since userid is unique
      a.list.headOption
    }
  }

  override def authenticateStudentUser(q: UserLogin): Option[StudentUser] = {
    DB(name) withSession { implicit session =>
      val s = for {
        sAuth <- studentUsersAuth filter (_.userid === q.username) if (matchPasswords(sAuth.password, q.password))
        student <- sAuth.student
      } yield student
      //Since userid is unique
      s.list.headOption
    }
  }

  private def matchPasswords(passCorrect: Column[String], passRequest: String) = {
    passCorrect === passRequest
  }

  private def encryptPassword(pass: String) = {
    pass
  }

  override def allEJournalPublishers() = DB(name) withSession { implicit session =>
    ejournalPublishers.list
  }

  override def addEJournalPublisher(publisher: EJournalPublisher) =
    DB(name) withSession { implicit session =>
      ejournalPublishers += publisher
    }

  override def removeEJournalPublisher(publisher: EJournalPublisher) =
    DB(name) withSession { implicit session =>
      val publisherQuery = ejournalPublishers.filter(_.code === publisher.code)
      publisherQuery.list.headOption.map { p =>
        ejournals.filter(j => j.publisherCode === p.code && j.name === name).delete
      }
      publisherQuery.delete
    }

  override def allEJournals(publisherCode: String) = DB(name) withSession {
    implicit session =>
      ejournalPublishers.filter(_.code === publisherCode).list.headOption.map { p =>
        ejournals.filter(_.publisherCode === p.code).list
      }
  }

  override def addEJournal(journal: EJournal) = DB(name) withSession { implicit session =>
    ejournalPublishers.filter(_.code === journal.publisherCode).list.headOption.map { p =>
      ejournals += journal
    }
  }

  override def removeEJournal(journal: EJournal) = DB(name) withSession {
    implicit session =>
      ejournalPublishers.filter(_.code === journal.publisherCode).list.headOption.map { p =>
        ejournals.filter(j => j.publisherCode === p.code && j.name === journal.name).delete
      }
  }

  override def allEBookPublishers() = DB(name) withSession { implicit session =>
    ebookPublishers.list
  }

  override def addEBookPublisher(publisher: EBookPublisher) =
    DB(name) withSession { implicit session =>
      ebookPublishers += publisher
    }

  override def removeEBookPublisher(publisher: EBookPublisher) =
    DB(name) withSession { implicit session =>
      val publisherQuery = ebookPublishers.filter(_.code === publisher.code)
      publisherQuery.list.headOption.map { p =>
        ebooks.filter(j => j.publisherCode === p.code && j.name === name).delete
      }
      publisherQuery.delete
    }

  override def allEBooks(publisherCode: String) = DB(name) withSession {
    implicit session =>
      ebookPublishers.filter(_.code === publisherCode).list.headOption.map { p =>
        ebooks.filter(_.publisherCode === p.code).list
      }
  }

  override def addEBook(book: EBook) = DB(name) withSession { implicit session =>
    ebookPublishers.filter(_.code === book.publisherCode).list.headOption.map { p =>
      ebooks += book
    }
  }

  override def removeEBook(book: EBook) = DB(name) withSession {
    implicit session =>
      ebookPublishers.filter(_.code === book.publisherCode).list.headOption.map { p =>
        ebooks.filter(j => j.publisherCode === p.code && j.name === book.name).delete
      }
  }

  override def allEDatabases() = DB(name) withSession { implicit session =>
    edatabases.list
  }

  override def addEDatabase(database: EDatabase) =
    DB(name) withSession { implicit session =>
      edatabases += database
    }

  override def removeEDatabase(database: EDatabase) =
    DB(name) withSession { implicit session =>
      val databaseQuery = edatabases.filter(_.name === database.name)
      databaseQuery.delete
    }

  override def init() {
    DB(name) withSession { implicit session =>
      import scala.slick.jdbc.meta._

      if (MTable.getTables(tables.booksTableName).list().isEmpty) {
        tables.books.ddl.create
      }
      if (MTable.getTables(tables.bookVariablesTableName).list().isEmpty) {
        tables.bookVariables.ddl.create
      }
      if (MTable.getTables(tables.bookPurchasesTableName).list().isEmpty) {
        tables.bookPurchases.ddl.create
      }
      if (MTable.getTables(tables.bookPurchaseDetailsTableName).list().isEmpty) {
        tables.bookPurchaseDetails.ddl.create
      }
      if (MTable.getTables(tables.issueHistoryTableName).list().isEmpty) {
        tables.issueHistory.ddl.create
      }
      if (MTable.getTables(tables.issueRequestHistoryTableName).list().isEmpty) {
        tables.issueRequestHistory.ddl.create
      }
      if (MTable.getTables(tables.returnHistoryTableName).list().isEmpty) {
        tables.returnHistory.ddl.create
      }
      if (MTable.getTables(tables.studentsTableName).list().isEmpty) {
        tables.students.ddl.create
      }
      if (MTable.getTables(tables.adminsTableName).list().isEmpty) {
        tables.admins.ddl.create
      }
      if (MTable.getTables(tables.adminUsersAuthTableName).list().isEmpty) {
        tables.adminUsersAuth.ddl.create
      }
      if (MTable.getTables(tables.studentUsersAuthTableName).list().isEmpty) {
        tables.studentUsersAuth.ddl.create
      }
      if (MTable.getTables(tables.ejournalPublishersTableName).list().isEmpty) {
        tables.ejournalPublishers.ddl.create
      }
      if (MTable.getTables(tables.ejournalsTableName).list().isEmpty) {
        tables.ejournals.ddl.create
      }
      if (MTable.getTables(tables.ebookPublishersTableName).list().isEmpty) {
        tables.ebookPublishers.ddl.create
      }
      if (MTable.getTables(tables.ebooksTableName).list().isEmpty) {
        tables.ebooks.ddl.create
      }
      if (MTable.getTables(tables.edatabasesTableName).list().isEmpty) {
        tables.edatabases.ddl.create
      }
    }
  }
}

object SlickDatabaseUtil {
  def getDBUtil(_name: String = "default")(implicit app: Application): DatabaseService = {
    new {
      val application = app
      override val name = _name
    } with SlickDatabaseTables with SlickDatabaseService
  }
}
