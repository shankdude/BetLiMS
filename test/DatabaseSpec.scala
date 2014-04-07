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
  }
}

