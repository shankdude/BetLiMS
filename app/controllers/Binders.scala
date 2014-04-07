package controllers;

import play.api.mvc.QueryStringBindable

object Binders {
  
  import FormEncapsulators.BookSearch
  
  implicit def queryStringBinder = new QueryStringBindable[BookSearch] {
    override def bind(key: String, params: Map[String, Seq[String]]): 
    Option[Either[String, BookSearch]] = {
      type b = QueryStringBindable[Option[String]]
      for {
        isbn <- implicitly[b].bind("isbn", params)
        title <- implicitly[b].bind("title", params)
        author <- implicitly[b].bind("author", params)
      } yield {
        (isbn, title, author) match {
          case (Right(isbn), Right(title), Right(author)) => Right(BookSearch(isbn, title, author))
          case _ => Left("Unable to bind a BookSearch")
        }
      }
      //println(s"Created: $c")
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
