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

  override def authenticateUser(q: UserLogin): Option[User] = {
    DB(name) withSession { implicit session =>
      //Since userid is unique
      val s = for {
        sAuth <- studentUsersAuth where (_.userid is q.username) if (matchPasswords(sAuth.password, q.password))
        student <- sAuth.student
      } yield (sAuth, student)
      println("joke1")
      println(s)
      val a = for {
        aAuth <- adminUsersAuth where (_.userid is q.username) if (matchPasswords(aAuth.password, q.password))
        admin <- aAuth.admin
      } yield (aAuth, admin)
      println("joke2")
      println(a)

      None
    }
  }

  private def matchPasswords(passCorrect: Rep[String], passRequest: Rep[String]) = {
    passCorrect == passRequest
  }

  private def encryptPassword(pass: String) = {
    pass
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
