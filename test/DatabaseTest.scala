package test

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.FormEncapsulators._
import controllers.Models._

object DatabaseTest extends Specification {

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
}

