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

  val studentsTableName = "student_user"
  val students = TableQuery[StudentTable]
  class StudentTable(tag: Tag) extends Table[StudentUser](tag, studentsTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def name = column[String]("name")
    def branch = column[String]("branch")
    def year = column[Int]("year")

    def * = (userid, name, year, branch) <> (StudentUser.tupled, StudentUser.unapply)
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
}

trait SlickDatabaseService extends DatabaseService {
  tables: SlickDatabaseTables =>

  import Models._
  import FormEncapsulators._

  implicit val application: Application
  val name: String = "default"

  def insertBooks(b: Seq[Book]) {
    DB withSession { implicit session =>
      tables.books ++= b
    }
  }

  override def booksearch(q: BookSearch): List[Book] = {
    DB(name) withSession { implicit session =>
      val v0 = tables.books

      val v1 = q.isbn match {
        case Some(x) => v0.filter(y => y.isbn.like("%" + x + "%"))
        case None    => v0
      }
      val v2 = q.title match {
        case Some(x) => v1.filter(y => y.title.like("%" + x + "%"))
        case None    => v1
      }
      val v3 = q.author match {
        case Some(x) => v2.filter(y => y.author.like("%" + x + "%"))
        case None    => v2
      }

      v3.list
    }
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

  override def init() {
    DB(name) withSession { implicit session =>
      import scala.slick.jdbc.meta._

      if (MTable.getTables(tables.booksTableName).list().isEmpty) {
        tables.books.ddl.create
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
