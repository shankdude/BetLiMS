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
    }

    "authenticateUsers()" in withInMemoryDatabase { db =>
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

      examplesBlock {
        for ((ul, actual) <- logins) {
          ul.toString >> {
            val user = db.authenticateUser(ul)              
            actual match {
              case Some(actualUser) => user must beSome(actualUser)
              case None             => user must beNone
            }
          }
        }
      }
    }
  }
}

