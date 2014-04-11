package test

import play.api.test._
import play.api.test.Helpers._

import controllers.FormEncapsulators._
import controllers.Models._

class DatabaseSpec extends BetLiMSSpec {
  
  "SlickDatabaseService" should {

    "search for a book" in withInMemoryDatabase { db =>

      db insertBooks Seq (
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1),
        Book("13356", "Intro 3", "XYZtosh", 1)
      )

      val bs1 = BookSearch(None, None, Some("Ashu"))
      db.booksearch(bs1) must beEqualTo(List[Book](
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1)
      ))

      val bs2 = BookSearch(None, None, Some("tosh"))
      db.booksearch(bs2) must beEqualTo(List[Book](
        Book("12356", "Intro 2", "Ashutosh", 1),
        Book("13356", "Intro 3", "XYZtosh", 1)
      ))
      
      db.booksearch(bs2) must not be equalTo(List[Book]())
    }

    "authenticateUsers()" >> withInMemoryDatabase { db =>
      val admin1 = AdminUser("admin1", "Admin 1")
      val admin2 = AdminUser("admin2", "Admin 2")
      val stud1 = StudentUser("stud1", "Student 1", 1, "CSE")
      val stud2 = StudentUser("stud2", "Student 2", 2, "CSE")
      val stud3 = StudentUser("stud3", "Student 3", 3, "CSE")
      val stud4 = StudentUser("stud4", "Student 4", 4, "CSE")

      db insertUser Seq(
        admin1 -> "pasw",
        admin2 -> "pasw",
        stud1 -> "pasw",
        stud2 -> "pasw",
        stud3 -> "pasw",
        stud4 -> "pasw"
      )

      val logins = List[(UserLogin, Option[User])](
        UserLogin("admin1", "pasw") -> Some(admin1),
        UserLogin("stud1", "pasw") -> Some(stud1)
      )
      
      val ul1 = UserLogin("admin2", "pasw")
      val user1 = db.authenticateUser(ul1)
      user1 must beSome(admin2)
      
      val ul2 = UserLogin("admin1", "pasw")
      val user2 = db.authenticateUser(ul2)
      user2 must be equalTo(Some(admin1))

      val ul3 = UserLogin("stud1", "pasw")
      val user3 = db.authenticateUser(ul3)
      user3 must be equalTo(Some(stud1))

      val ul4 = UserLogin("stud2", "pasw")
      val user4 = db.authenticateUser(ul4)
      user4 must be equalTo(Some(stud2))      
      
      val ul5 = UserLogin("admin2", "passw")
      val user5 = db.authenticateUser(ul5)
      user5 must be equalTo(None)
      
      val ul6 = UserLogin("admin1", "passw")
      val user6 = db.authenticateUser(ul6)
      user6 must be equalTo(None)

      val ul7 = UserLogin("stud1", "passw")
      val user7 = db.authenticateUser(ul7)
      user7 must be equalTo(None)

      val ul8 = UserLogin("stud2", "passw")
      val user8 = db.authenticateUser(ul8)
      user8 must be equalTo(None) 
    }
  }
}

