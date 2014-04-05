package test

import play.api.test._
import play.api.test.Helpers._

import controllers.FormEncapsulators._
import controllers.Models._

class DatabaseSpec extends BetLiMSSpec {

  "DatabaseService" should {

    "send 404 on a bad request" in withMockDatabase { _ =>
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }
  }

  "SlickDatabaseServer" should {

    "search for a book" in withInMemoryDatabase { db =>

      db insertBooks Seq (
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1),
        Book("13356", "Intro 3", "XYZ", 1)
      )

      val bs = BookSearch(None, None, Some("Ashu"))
      db.booksearch(bs) must beEqualTo(List[Book](
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1)
      ))
    }
  }
}

