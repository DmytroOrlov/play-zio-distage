package com.example.playscalajs.controllers

import com.example.playscalajs.shared.SharedMessages
import play.api.mvc._

class Application(val controllerComponents: ControllerComponents) extends BaseController {

  def index = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

}
