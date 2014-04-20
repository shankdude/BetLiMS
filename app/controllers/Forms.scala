/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers

import play.api.data._
import play.api.data.Forms._

object Forms {
  
  import FormEncapsulators._;

  val searchForm: Form[BookSearch] = Form {
    mapping(
      "isbn" -> optional(nonEmptyText),
      "title" -> optional(nonEmptyText),
      "author" -> optional(nonEmptyText),
      "publisher" -> optional(nonEmptyText),
      "edition" -> optional(number),
      "publishYear" -> optional(number)
    )(BookSearch.apply)(BookSearch.unapply) verifying(
      "Form must contain at least one filled field",
      (bSearch: BookSearch) => bSearch.isDefined
    )
  }

  val loginForm: Form[UserLogin] = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLogin.apply)(UserLogin.unapply)
  }

}

object FormEncapsulators {
  case class BookSearch(isbn: Option[String], title: Option[String], author: Option[String],
    publisher: Option[String], edition: Option[Int], publishYear: Option[Int]) {
    def isDefined = isbn.isDefined || title.isDefined || author.isDefined || 
      publisher.isDefined || edition.isDefined || publishYear.isDefined
  }

  case class UserLogin(username: String, password: String)
}