package com.yoppworks.chat.infrastructure

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

object StaticRoutes {
  val STATIC_PATH = "html/"
}

trait StaticRoutes {
  import StaticRoutes._

  val staticRoutes: Route =
    pathPrefix("static") {
       path(Remaining) {
         remainingPath =>
           get {
             getFromResource(STATIC_PATH + remainingPath)
           }
       }
    } ~ pathEndOrSingleSlash {
      get {
        getFromResource(STATIC_PATH + "index.html")
      }
    }
}
