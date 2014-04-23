package controllers

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import Models._;
import FormEncapsulators._;

object BetLiMSApplication extends BetLiMSApplication with BetLiMSRestfulServer {
  override lazy val databaseService = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)
}

trait BetLiMSApplication extends Controller with DatabaseServiceProvider{

  def index = Action {
    Ok(views.html.index("Your new application is ready.")(None, Forms.loginForm))
  }

  def search(bs: BookSearch) = Action {
    val list = databaseService.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list))(None, Forms.loginForm))
  }

  def searchPost() = Action { implicit request =>
    println("Trying to bind form request")
    Forms.searchForm.bindFromRequest.fold (
      fe => BadRequest(views.html.search(fe)(None)(None, Forms.loginForm)),
      bs => Redirect(routes.BetLiMSApplication.search(bs))
    )
  }
  
  def personal() = Action {
    val userid = "1201CS41"
    val issues = databaseService.issueList(userid)
    val requests = databaseService.issueRequestList(userid)
    val history = databaseService.returnList(userid) take 5
    Ok(views.html.personal(issues, requests, history)(None, Forms.loginForm))
  }

}

trait BetLiMSRestfulServer extends Controller with DatabaseServiceProvider {
  import JsonWrappers._
  
  def links_ejournalPublishers() = Action {
    val ejournalPublishers = databaseService.allEJournalPublishers()
    Ok(Json.toJson(ejournalPublishers))
  }
  
  def links_ejournalPublishers_insert(code: String) = Action(parse.json) { request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.addEJournalPublisher(valid)
        Ok
      }
    )
  }
  
  def links_ejournalPublishers_delete(code: String) = Action(parse.json) { request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.removeEJournalPublisher(valid)
        Ok
      }
    )
  }
  
  def links_ejournals(code: String) = Action {
    databaseService.allEJournals(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
  
  def links_ejournals_insert(code: String, name: String) = Action(parse.json) { request =>
    val ejournalJSON = request.body
    ejournalJSON.validate[EJournal](ejournalReads(code, name)).fold (
      invalid => BadRequest("Invalid EJournal"),
      valid => {
        databaseService.addEJournal(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
  
  def links_ejournals_delete(code: String, name: String) = Action(parse.json) { request =>
    val ejournalJSON = request.body
    ejournalJSON.validate[EJournal](ejournalReads(code, name)).fold (
      invalid => BadRequest("Invalid EJournal"),
      valid => {
        databaseService.removeEJournal(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
  
  def links_ebookPublishers() = Action {
    val ebookPublishers = databaseService.allEBookPublishers()
    Ok(Json.toJson(ebookPublishers))
  }
  
  def links_ebookPublishers_insert(code: String) = Action(parse.json) { request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.addEBookPublisher(valid)
        Ok
      }
    )
  }
  
  def links_ebookPublishers_delete(code: String) = Action(parse.json) { request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.removeEBookPublisher(valid)
        Ok
      }
    )
  }
  
  def links_ebooks(code: String) = Action {
    databaseService.allEBooks(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
  
  def links_ebooks_insert(code: String, name: String) = Action(parse.json) { request =>
    val ebookJSON = request.body
    ebookJSON.validate[EBook](ebookReads(code, name)).fold (
      invalid => BadRequest("Invalid EBook"),
      valid => {
        databaseService.addEBook(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
  
  def links_ebooks_delete(code: String, name: String) = Action(parse.json) { request =>
    val ebookJSON = request.body
    ebookJSON.validate[EBook](ebookReads(code, name)).fold (
      invalid => BadRequest("Invalid EBook"),
      valid => {
        databaseService.removeEBook(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
  
  def links_edatabases() = Action {
    val edatabase = databaseService.allEDatabases()
    Ok(Json.toJson(edatabase))
  }
  
  def links_edatabases_insert(name: String) = Action(parse.json) { request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
  }
  
  def links_edatabases_delete(name: String) = Action(parse.json) { request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
  }
  
}