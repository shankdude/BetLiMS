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
      "isbn" -> optional(text),
      "title" -> optional(text),
      "author" -> optional(text)
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
  case class BookSearch(isbn: Option[String], title: Option[String], author: Option[String]) {
    def isDefined = isbn.isDefined || title.isDefined || author.isDefined
  }

  case class UserLogin(username: String, password: String)
}