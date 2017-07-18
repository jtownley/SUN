package com.yoppworks.chat.infrastructure

import akka.http.scaladsl.common.{ EntityStreamingSupport, JsonEntityStreamingSupport }
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{ Flow, Source }
import com.yoppworks.chat.api.Chat
import com.yoppworks.chat.domain.ChatMessage
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val chatMessageFormat = jsonFormat3(ChatMessage.apply)
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport = EntityStreamingSupport.json()
}


trait ChatRoutes extends JsonSupport {

  val api: Chat

  val chatRoutes: Route = pathPrefix("api") {
    path("chat") {
      post {
        entity(as[ChatMessage]) { message =>
          val result = api.publishOneMessage(message)
          complete(result)
        }
      } ~ get {
          parameters("topic", "client") { (topic, client) =>
            println(s"Recieved Request for $topic for client $client")
            complete(api.getMessages(topic, client))
          }
        }
    }
  }
}


