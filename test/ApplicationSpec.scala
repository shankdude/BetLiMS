package test

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.BetLiMSApplication
import controllers.FormEncapsulators._
import controllers.Models._

class ApplicationSpec extends BetLiMSSpec {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
      route(FakeRequest(GET, "/index")) must beNone
      //route(FakeRequest(GET, "/search?book=")) must beNone
    }

    "render the index page for GET url /" in new WithApplication {
      val result = route(FakeRequest(GET, "/")).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "text/html")
      contentAsString(result) must contain ("Your new application is ready.")
    }

    "render the search page for GET url /search" in new WithApplication {
      val result = route(FakeRequest(GET, "/search")).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "text/html")
      contentAsString(result) must contain ("Please Enter the Search query")
      //contentAsString(result) must not contain("Search Results")
    }
  }

  "BetLiMS Application" should {

    "cater to the search GET request" in withMockDatabase { db =>
      db.booksearch(any[BookSearch]) returns List[Book](
        Book("12356", "Intro 2", "Ashutosh", 1),
        Book("13356", "Intro 3", "XYZtosh", 1)
      )

      val app = mock[BetLiMSApplication]
      app.databaseService returns db

      val request = FakeRequest("GET", "/search?title=Ashu")
      val result = app.searchPost().apply(request)

      val bodyText = contentAsString(result)
      bodyText must be equalTo "ok"
    }
  }
}
