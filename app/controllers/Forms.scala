/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers

import play.api.data._
import play.api.data.Forms._

object Forms {

  val searchForm: Form[String] = Form {
    single("title" -> nonEmptyText)
  }

}