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
  
  implicit val ebookPublisherWrites = new Writes[EBookPublisher] {
    def writes(publisher: EBookPublisher) = Json.obj(
      "name" -> publisher.name,
      "code" -> publisher.code,
      "url" -> publisher.url
    )
  }
  
  implicit val ebookWrites = new Writes[EBook] {
    def writes(book: EBook) = Json.obj(
      "name" -> book.name,
      "url" -> book.url
    )
  }
  
  implicit val edatabaseWrites = new Writes[EDatabase] {
    def writes(database: EDatabase) = Json.obj(
      "name" -> database.name,
      "url" -> database.url
    )
  }
  
  implicit val bookWrites = new Writes[(Book, BookVariables)] {
    def writes(details: (Book, BookVariables)) = {
      val (b, bv) = details
      Json.obj(
        "isbn" -> b.isbn,
        "title" -> b.title,
        "author" -> b.author,
        "publisher" -> b.publisher,
        "edition" -> b.edition,
        "publishYear" -> b.publishYear,
        "pages" -> b.pages,
        "callNo" -> b.callNo,
        "copies" -> bv.copies,
        "references" -> bv.references,
        "checkouts" -> bv.checkouts
      )
    }
  }
  
  implicit val studentUserWrite = new Writes[StudentUser] {
    def writes(s: StudentUser) = Json.obj(
      "userid" -> s.userid,
      "name" -> s.name,
      "year" -> s.year,
      "branch" -> s.branch
    )
  }
  
  implicit val adminUserWrite = new Writes[AdminUser] {
    def writes(a: AdminUser) = Json.obj(
      "userid" -> a.userid,
      "name" -> a.name
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
  
  val ebookPublisherReads: String => Reads[EBookPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EBookPublisher)
  
  val ebookReads: (String, String) => Reads[EBook] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EBook)
  
  val edatabaseReads: (String) => Reads[EDatabase] = (name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String]
  )(EDatabase)

  def defaultValueReads[T](default: T)(implicit r: Reads[T]) = {
    Reads.optionNoError[T].map(_.getOrElse(default)) keepAnd
    Reads.filter[T](ValidationError("validate.error.unexpected.value"))(_ == default)
  }
}
