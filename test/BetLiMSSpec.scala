package test

import org.specs2.execute.AsResult
import org.specs2.execute.Result
import org.specs2.execute.Results
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.AllExpectations
import org.specs2.specification.ResultsContext

import play.api.GlobalSettings
import play.api.test._
import play.api.test.Helpers._

import controllers._
import controllers.SlickDatabaseUtil._

abstract class BetLiMSSpec extends Specification with Mockito {

  type SlickInjectedDatabase = SlickDatabaseService with SlickDatabaseTables

  def withInMemoryDatabase[T](f: SlickInjectedDatabase => T) = {
    val appMemDb = FakeApplication(additionalConfiguration = inMemoryDatabase())
    running(appMemDb) {
      val db = getDBUtil()(appMemDb).asInstanceOf[SlickInjectedDatabase]
      f(db)
    }
  }

  def withMockDatabase[T](f: (DatabaseService, BetLiMSApplication) => T) = {
    val db = mock[DatabaseService]
    object app extends BetLiMSApplication {
      def databaseService = db
    }
    val fakeApp = FakeApplication(withGlobal = Some(new GlobalSettings() {
      override def getControllerInstance[A](controllerClass: Class[A]): A = {
        app.asInstanceOf[A]
      }
    }))
    running(fakeApp) {
      f(db, app)
    }
  }
}
