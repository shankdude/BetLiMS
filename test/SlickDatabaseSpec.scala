package test

import org.specs2.execute.Result
import org.specs2.mutable.Specification

import play.api.test._
import play.api.test.Helpers._

import controllers.SlickDatabaseService
import controllers.SlickDatabaseTables
import controllers.SlickDatabaseUtil._

class SlickDatabaseSpec extends Specification {

  type SlickInjectedDatabase = SlickDatabaseService with SlickDatabaseTables

  val appMemDb = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  def withInMemoryDatabase[T](f: SlickInjectedDatabase => T): Result = new WithApplication(appMemDb) {
    val db = getDBUtil("test")(appMemDb).asInstanceOf[SlickInjectedDatabase]
    f(db)
  }
}
