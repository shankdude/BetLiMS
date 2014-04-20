package controllers;

import play.api.mvc.QueryStringBindable

object Binders {

  import FormEncapsulators.BookSearch

  implicit def queryStringBinder = new QueryStringBindable[BookSearch] {
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, BookSearch]] = {
      type opStr_t = QueryStringBindable[Option[String]]
      type opInt_t = QueryStringBindable[Option[Int]]
      val reducedMap = params - "isbn" - "title" - "author"
      for {
        isbn <- implicitly[opStr_t].bind("isbn", params)
        title <- implicitly[opStr_t].bind("title", params)
        author <- implicitly[opStr_t].bind("author", params)
        publisher <- implicitly[opStr_t].bind("publisher", params)
        edition <- implicitly[opInt_t].bind("edition", params)
        publishYear <- implicitly[opInt_t].bind("publishYear", params)
      } yield {
        (isbn, title, author, publisher, edition, publishYear, reducedMap.size) match {
          case (Right(isbn), Right(title), Right(author), Right(publisher), Right(edition), Right(publishYear), 0) =>
            Right(BookSearch(isbn, title, author, publisher, edition, publishYear))
          case _ => Left("invalid search parameters")
        }
      }
    }

    override def unbind(key: String, bs: BookSearch): String = {
      type str_t = QueryStringBindable[String]
      type int_t = QueryStringBindable[Int]
      val str = bs.isbn.map(implicitly[str_t].unbind("isbn", _) + "&").getOrElse("") +
        bs.title.map(implicitly[str_t].unbind("title", _) + "&").getOrElse("") +
        bs.author.map(implicitly[str_t].unbind("author", _) + "&").getOrElse("") +
        bs.publisher.map(implicitly[str_t].unbind("publisher", _) + "&").getOrElse("") +
        bs.edition.map(implicitly[int_t].unbind("edition", _) + "&").getOrElse("") +
        bs.publishYear.map(implicitly[int_t].unbind("publishYear", _) + "&").getOrElse("")
      str.stripSuffix("&")
    }
  }
}
