package test

import org.specs2.mutable.Specification
//import org.junit.runner.JUnitRunner

import play.api.test._
import play.api.test.Helpers._

import controllers.FormEncapsulators._
import controllers.Models._

//@RunWith(classOf[JUnitRunner])
object DatabaseSpec extends SlickDatabaseSpec {

  /*"DatabaseService" should {
    import controllers.DatabaseService

    //Create a mock DatabaseService 
    val db = mock[DatabaseService]

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")

      true
    }
  }*/

  "SlickDatabaseServer" should {
    import controllers.SlickDatabaseService
    import controllers.SlickDatabaseTables
    import controllers.SlickDatabaseUtil._

    "search for a book" in withInMemoryDatabase { db =>
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

