package controllers

import play.api._
import play.api.data.Form
import play.api.mvc._

import Models._;
import FormEncapsulators._;

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def search(isbn: Option[String], 
             title: Option[String], 
             author: Option[String]) = Action {
    val bs = (BookSearch(isbn, title, author))
    val list = DatabaseUtil.booksearch(bs);
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list)))
  }
  
  def searchPost = Action { implicit request =>
    Forms.searchForm.bindFromRequest.fold(
      fe => BadRequest(views.html.search(fe)(None)),
      bs => Redirect{
        routes.Application.search _ tupled BookSearch.unapply(bs).get
      }
    )
  }

}