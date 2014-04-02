import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class DatabaseSpec extends Specification {

  "DatabaseUtil" should {

    //Mock the existing database util object
    val db = mock[DatabaseUtil]

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }
  }

  "[Specific Test] SlickDatabaseUtil" should {

    val appWithMemoryDatabase = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

    "search for a book" in new WithApplication(appWithMemoryDatabase) {
      Fixtures.load("books.table.yaml")

    }
  }
}

