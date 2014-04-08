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

  val usersAuthTableName = "user_auth"
  val usersAuth = TableQuery[UserAuthTable]
  class UserAuthTable(tag: Tag) extends Table[(String, String)](tag, usersAuthTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def password = column[String]("pass")
    def student = foreignKey("student_user", userid, students)(_.userid)
    def admin = foreignKey("admin_user", userid, admins)(_.userid)

    def * = (userid, password)
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

  override def authenticateUser(q: UserLogin): Option[User] = {
    DB(name) withSession { implicit session =>
      //Since userid is unique
      val a = for {
        uAuth <- usersAuth where (_.userid is q.username) if (matchPasswords(uAuth.password, q.password))
        admin <- uAuth.admin
        student <- uAuth.student
      } yield (uAuth, admin, student)
      None
    }
  }
  
  private def matchPasswords(passCorrect: Rep[String], passRequest: Rep[String]) = {
    passCorrect == passRequest
  }

  override def init() {
    DB(name) withSession { implicit session =>
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
      override val name = _name
    } with SlickDatabaseTables with SlickDatabaseService
  }
}
