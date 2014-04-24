package test

import play.api.test._
import play.api.test.Helpers._

import controllers.DatabaseServiceMessages
import controllers.FormEncapsulators._
import controllers.Models._

class DatabaseSpec extends BetLiMSSpec with DatabaseServiceMessages {
  
  "SlickDatabaseService" should {

    "search for a book" in withInMemoryDatabase { db =>

      db addBooks Seq (
        Book("12345", "Intro 1", "Ashu", "XYZ Publisher", 1, 2008, 504, "537.6 GRI/I P08"),
        Book("12356", "Intro 2", "Ashutosh", "1 Publishers", 2, 2009, 652, "535 THO/O P06"),
        Book("13356", "Intro 3", "XYZtosh", "21 Publications", 3, 2010, 444, "539.2 HEA/C N95")
      )
      
      db purchaseBook Seq (
        BookPurchaseDetails(-1, "12345", 12, 3),
        BookPurchaseDetails(-1, "12356", 13, 4),
        BookPurchaseDetails(-1, "13356", 14, 4)
      )

      val bs1 = BookSearch(None, None, Some("Ashu"), None, None, None)
      db.booksearch(bs1) must beEqualTo(List[(Book, BookVariables)](
        Book("12345", "Intro 1", "Ashu", "XYZ Publisher", 1, 2008, 504, "537.6 GRI/I P08") ->
          BookVariables("12345", 12, 3, 0),
        Book("12356", "Intro 2", "Ashutosh", "1 Publishers", 2, 2009, 652, "535 THO/O P06") ->
          BookVariables("12356", 13, 4, 0)
      ))

      val bs2 = BookSearch(None, None, Some("tosh"), None, None, None)
      db.booksearch(bs2) must beEqualTo(List[(Book, BookVariables)](
        Book("12356", "Intro 2", "Ashutosh", "1 Publishers", 2, 2009, 652, "535 THO/O P06") ->
          BookVariables("12356", 13, 4, 0),
        Book("13356", "Intro 3", "XYZtosh", "21 Publications", 3, 2010, 444, "539.2 HEA/C N95") ->
          BookVariables("13356", 14, 4, 0)
      ))
      
      db.booksearch(bs2) must not be equalTo(List[(Book, BookVariables)]())
    }

    "authenticateAdminUsers()" >> withInMemoryDatabase { db =>
      val admin1 = AdminUser("admin1", "Admin 1")
      val admin2 = AdminUser("admin2", "Admin 2")

      db insertUser Seq (
        admin1 -> "pasw",
        admin2 -> "pasw"
      )

      val logins = List[(UserLogin, Option[AdminUser])] (
        UserLogin("admin1", "pasw") -> Some(admin1)
      )
      
      val ul1 = UserLogin("admin2", "pasw")
      val user1 = db.authenticateAdminUser(ul1)
      user1 must beSome(admin2)
      
      val ul2 = UserLogin("admin1", "pasw")
      val user2 = db.authenticateAdminUser(ul2)
      user2 must be equalTo(Some(admin1))

      val ul5 = UserLogin("admin2", "passw")
      val user5 = db.authenticateAdminUser(ul5)
      user5 must be equalTo(None)
      
      val ul6 = UserLogin("admin1", "passw")
      val user6 = db.authenticateAdminUser(ul6)
      user6 must be equalTo(None)
      
  }
  
  "authenticateStudentUsers()" >> withInMemoryDatabase { db =>
      val stud1 = StudentUser("stud1", "Student 1", 1, "CSE")
      val stud2 = StudentUser("stud2", "Student 2", 2, "CSE")
      val stud3 = StudentUser("stud3", "Student 3", 3, "CSE")
      val stud4 = StudentUser("stud4", "Student 4", 4, "CSE")

      db insertUser Seq(
        stud1 -> "pasw",
        stud2 -> "pasw",
        stud3 -> "pasw",
        stud4 -> "pasw"
      )

      val logins = List[(UserLogin, Option[StudentUser])](
        UserLogin("stud1", "pasw") -> Some(stud1)
      )
      
      val ul3 = UserLogin("stud1", "pasw")
      val user3 = db.authenticateStudentUser(ul3)
      user3 must be equalTo(Some(stud1))

      val ul4 = UserLogin("stud2", "pasw")
      val user4 = db.authenticateStudentUser(ul4)
      user4 must be equalTo(Some(stud2))      
      
      val ul7 = UserLogin("stud1", "passw")
      val user7 = db.authenticateStudentUser(ul7)
      user7 must be equalTo(None)

      val ul8 = UserLogin("stud2", "passw")
      val user8 = db.authenticateStudentUser(ul8)
      user8 must be equalTo(None) 
    }
  }
}
