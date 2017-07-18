package com.yoppworks.chat.infrastructure

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._
import com.yoppworks.chat.api.Chat


object Main extends App with StaticRoutes with ChatRoutes{
  implicit val system = ActorSystem("SafetyDance")
  implicit val materializer = ActorMaterializer()

  //  val api : Chat = ???

  val route = staticRoutes ~ chatRoutes

  Http().bindAndHandle(route, "localhost", 8080)
}