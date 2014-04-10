package test

import org.specs2.mutable.Specification

import play.api.mvc.SimpleResult
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Future

import controllers.BetLiMSApplication
import controllers.FormEncapsulators._
import controllers.Models._

class ApplicationSpec extends BetLiMSSpec {

  "BetLiMS Application" should {

    "send status 404 for url" >> {
      test404(GET, "/boun")
      test404(GET, "/search")
      test404(GET, "/search?book=")
    }

    "render the index page for GET url /" in withMockDatabase { (db, app) =>
      val result = app.index()(FakeRequest(GET, "/"))

      testStatus(result)
      testContentType(result)
      testContentContains(result, """
            The library is opened between 9.00 AM to 8.30 PM, in weekdays. 
            On Sundays it opens at 9.00 AM and closes at 5.00 PM. 
            Any body can issue/return books at any time using the self check in 
            and check out system. The Central Library provides a healthy and peaceful 
            environment for users to acquire modern knowledge.""")
    }

    "render the search page for GET url /search" in withMockDatabase { (db, app) =>
      db.booksearch(any[BookSearch]) returns List[Book]()
      val result = app.search(BookSearch(None, None, None))(FakeRequest(GET, "/search"))

      testStatus(result)
      testContentType(result)
      testContentContains(result, "Please Enter the Search query")
      testContentContains(result, "Search Results", false)
    }

    "render the search page for GET url /search?title=Ashu" in withMockDatabase { (db, app) =>
      db.booksearch(any[BookSearch]) returns List[Book] (
        Book("12345", "Intro 1", "Ashu", 4),
        Book("12356", "Intro 2", "Ashutosh", 1)
      )

      val result = app.search(BookSearch(Some("Ashu"), None, None))(
         FakeRequest(GET, "/search?title=Ashu")
      )

      testStatus(result)
      testContentType(result)
      testContentContains(result, "Please Enter the Search query")
      testContentContains(result, "Search Results")
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

  def test404(method: String, path: String) = {
    path in new WithApplication {
      route(FakeRequest(method, path)) must be none
    }
  }
  
  def testStatus(result: scala.concurrent.Future[play.api.mvc.SimpleResult]) = {
    "status must be OK" >> {
      status(result) must equalTo(OK)
    }
  }
  
  def testContentType(result: scala.concurrent.Future[play.api.mvc.SimpleResult]) = {
    "content type must be text/html" >> {
      contentType(result) must beSome.which(_ == "text/html")
    }
  }  
  
  def testContentContains(result: Future[SimpleResult], check: String, z: Boolean = true) = {
    val msg = if (check.length < 50) check else "some predefined text"
    "content must contain " + msg >> {
      if (z) contentAsString(result) must contain(check)
      else contentAsString(result) must not contain(check)
    }
  }
}
