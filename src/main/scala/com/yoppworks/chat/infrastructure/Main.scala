package com.yoppworks.chat.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer


object Main extends App with StaticRoutes {
  implicit val system = ActorSystem("SafetyDance")
  implicit val materializer = ActorMaterializer()

  //  val api : Chat = ???

  val route = staticRoutes

  Http().bindAndHandle(route, "localhost", 8080)
}