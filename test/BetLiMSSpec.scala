package test

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.DatabaseService
import controllers.SlickDatabaseService
import controllers.SlickDatabaseTables
import controllers.SlickDatabaseUtil._

abstract class BetLiMSSpec extends Specification with Mockito {

  type SlickInjectedDatabase = SlickDatabaseService with SlickDatabaseTables

  val appMemDb = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def withInMemoryDatabase[T](f: SlickInjectedDatabase => T) = running(appMemDb) {
    val db = getDBUtil()(appMemDb).asInstanceOf[SlickInjectedDatabase]
    f(db)
  }

  def withMockDatabase[T](f: DatabaseService => T) = running(FakeApplication()) {
    val db = mock[DatabaseService]
    f(db)
  }
}
