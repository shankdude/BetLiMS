package test

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.BetLiMSApplication
import controllers.FormEncapsulators._
import controllers.Models._

class ApplicationSpec extends BetLiMSSpec {

  "BetLiMS Application" should {

    "send status 404 for url" >> {
      "/boun" in new WithApplication {
        route(FakeRequest(GET, "/boum")) must beNone
      }

      "/index" in new WithApplication {
        route(FakeRequest(GET, "/index")) must beNone
      }

      "/search?book=" in new WithApplication {
        route(FakeRequest(GET, "/search?book=")) must beNone
      } 
    }

    "render the index page for GET url /" in withMockDatabase { (db, app) =>
      val result = app.index()(FakeRequest(GET, "/"))

      "status must be OK" >> {
        status(result) must equalTo(OK)
      }
      "content type must be text/html" >> {
        contentType(result) must beSome.which(_ == "text/html")
      }
      "content must contain \"Your new application is ready.\"" >> {
        contentAsString(result) must contain ("Your new application is ready.")
      }
    }

    "render the search page for GET url /search" in withMockDatabase { (db, app) =>
      db.booksearch(any[BookSearch]) returns List[Book]()
      val result = app.search(BookSearch(None, None, None))(FakeRequest(GET, "/search"))

      "status must be OK" >> {
        status(result) must equalTo(OK)
      }
      "content type must be text/html" >> {
        contentType(result) must beSome.which(_ == "text/html")
      }
      "content must contain \"Please Enter the Search query\"" >> {
        contentAsString(result) must contain ("Please Enter the Search query")
      }
      "content must not contain \"Search Results\"" >> {
        contentAsString(result) must not contain ("Search Results")
      }
    }

    "render the search page for GET url /search?title=Ashu" in withMockDatabase { (db, app) =>
      db.booksearch(any[BookSearch]) returns List[Book] (
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1)
      )

      val result = app.search(BookSearch(Some("Ashu"), None, None))(
         FakeRequest(GET, "/search?title=Ashu")
      )

      "status must be OK" >> {
        status(result) must equalTo(OK)
      }
      "content type must be text/html" >> {
        contentType(result) must beSome.which(_ == "text/html")
      }
      "content must contain \"Please Enter the Search query\"" >> {
        contentAsString(result) must contain ("Please Enter the Search query")
      }
      "content must contain \"Search Results\"" >> {
        contentAsString(result) must contain ("Search Results")
      }
    }

    "cater to the form submission" >> {
      "searchPost" in withMockDatabase { (db, app) =>
        val url = "/search?title=Ashu"

        val result = app.searchPost().apply(FakeRequest("GET", url))

        status(result) must be equalTo(SEE_OTHER)
        redirectLocation(result) must be equalTo(Some(url))
      }
    }
  }
}
