package controllers

import play.api.data.Form
import play.api.mvc._

import Models._;
import FormEncapsulators._;

object BetLiMSApplication extends BetLiMSApplication {
  override lazy val databaseService = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)
}

trait BetLiMSApplication extends Controller with DatabaseServiceProvider{

  def index = Action {
    Ok(views.html.index("Your new application is ready.", Forms.loginForm))
  }

  def search(bs: BookSearch) = Action {
    val list = databaseService.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs), Forms.loginForm)(Some(list)))
  }

  def searchPost() = Action { implicit request =>
    println("Trying to bind form request")
    Forms.searchForm.bindFromRequest.fold (
      fe => BadRequest(views.html.search(fe,Forms.loginForm)(None)),
      bs => Redirect(routes.BetLiMSApplication.search(bs))
    )
  }

}