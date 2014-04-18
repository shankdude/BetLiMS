package controllers

import play.api.libs.json.{Json, JsPath, Writes, Reads}
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import Models._

object JsonWrappers {
  
  implicit val ejournalPublisherWrites = new Writes[EJournalPublisher] {
    def writes(publisher: EJournalPublisher) = Json.obj(
      "name" -> publisher.name,
      "code" -> publisher.code,
      "url" -> publisher.url
    )
  }
  
  implicit val ejournalWrites = new Writes[EJournal] {
    def writes(journal: EJournal) = Json.obj(
      "name" -> journal.name,
      "year accessed" -> journal.accessYear,
      "url" -> journal.url
    )
  }
  
  val ejournalPublisherReads: String => Reads[EJournalPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EJournalPublisher)
  
  val ejournalReads: (String, String) => Reads[EJournal] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "year accessed").read(Reads.of[Int] keepAnd Reads.min(1950)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EJournal)

  def defaultValueReads[T](default: T)(implicit r: Reads[T]) = {
    Reads.optionNoError[T].map(_.getOrElse(default)) keepAnd
    Reads.filter[T](ValidationError("validate.error.unexpected.value"))(_ == default)
  }
}
