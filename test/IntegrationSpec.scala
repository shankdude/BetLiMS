package test

import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain ("""|
            |The library is opened between 9.00 AM to 8.30 PM, in weekdays. 
            |On Sundays it opens at 9.00 AM and closes at 5.00 PM. 
            |Any body can issue/return books at any time using the self check in 
            |and check out system. The Central Library provides a healthy and peaceful 
            |environment for users to acquire modern knowledge.""".stripMargin.replace("\n", ""))
    }
  }
}
