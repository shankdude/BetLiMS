package test

import org.specs2.execute.Result
import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.Models._
import controllers.FormEncapsulators._

import controllers.SlickDatabaseService
import controllers.SlickDatabaseTables
import controllers.SlickDatabaseUtil._

class SlickDatabaseTest extends Specification {

  type SlickInjectedDatabase = SlickDatabaseService with SlickDatabaseTables

  val appMemDb = FakeApplication(additionalConfiguration = inMemoryDatabase())

  "SlickDatabaseServer" should {

    "search for a book" in {
      running(appMemDb) {
        val db = getDBUtil("test")(appMemDb).asInstanceOf[SlickInjectedDatabase]

        db insertBooks Seq (
          Book("12345", "Intro 1", "Ashu", 4),
          Book("12356", "Intro 2", "Ashutosh", 1),
          Book("13356", "Intro 3", "XYZ", 1)
        )

        val bs = BookSearch(None, None, Some("Ashu"))
        db.booksearch(bs) must equalTo(List[Book](
          Book("12345", "Intro 1", "Ashu", 4),
          Book("12356", "Intro 2", "Ashutosh", 1)
        ))
      }
    }
  }
}
