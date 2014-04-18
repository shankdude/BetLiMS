package controllers;

import play.api.mvc.QueryStringBindable

object Binders {
  
  import FormEncapsulators.BookSearch
  
  implicit def queryStringBinder = new QueryStringBindable[BookSearch] {
    override def bind(key: String, params: Map[String, Seq[String]]): 
    Option[Either[String, BookSearch]] = {
      type b = QueryStringBindable[Option[String]]
      val reducedMap = params - "isbn" - "title" - "author"
      for {
        isbn <- implicitly[b].bind("isbn", params)
        title <- implicitly[b].bind("title", params)
        author <- implicitly[b].bind("author", params)
      } yield {
        (isbn, title, author, reducedMap.size) match {
          case (Right(isbn), Right(title), Right(author), 0) => Right(BookSearch(isbn, title, author))
          case _ => Left("invalid search parameters")
        }
      }
    }

    override def unbind(key: String, bs: BookSearch): String = {
      type b = QueryStringBindable[String]
      val str = bs.isbn.map(implicitly[b].unbind("isbn", _) + "&").getOrElse("") +
                bs.title.map(implicitly[b].unbind("title", _) + "&").getOrElse("") +
                bs.author.map(implicitly[b].unbind("author", _) + "&").getOrElse("")
      str.stripSuffix("&")
    }
  }
}
