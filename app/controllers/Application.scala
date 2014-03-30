package controllers

import play.api.data.Form
import play.api.mvc._

import Models._;
import FormEncapsulators._;

object BetLiMSApplication extends Controller {
  
  lazy val db: DatabaseUtil = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def search(bs: BookSearch) = Action {
    val list = db.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list)))
  }
  
  def searchPost() = Action { implicit request =>
    println("Trying to bind form request")
    Forms.searchForm.bindFromRequest.fold (
      fe => BadRequest(views.html.search(fe)(None)),
      bs => Redirect(routes.BetLiMSApplication.search(bs))
    )
  }

}