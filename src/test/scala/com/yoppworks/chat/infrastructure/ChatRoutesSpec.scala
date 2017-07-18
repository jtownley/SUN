package com.yoppworks.chat.infrastructure

import java.util.UUID

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, StatusCodes }
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Source
import com.yoppworks.chat.api.Chat
import com.yoppworks.chat.domain.ChatMessage
import org.scalamock.scalatest.MockFactory
import org.scalatest.{ FreeSpec, Matchers }

import scala.concurrent.Future


class ChatRoutesSpec extends FreeSpec with Matchers with ScalatestRouteTest with ChatRoutes with MockFactory {

  val api: Chat = stub[Chat]
  "chat" - {
    "post" - {
      "should call Chat api with expected message" in {
        val expectedSender = "James"
        val expectedMessageTxt = "Message"
        val expectedRoom = "Room1"
        val expectedMessageJson = s"""{"sender":"$expectedSender","message":"$expectedMessageTxt","room":"$expectedRoom"}"""
        val expectedMessage = ChatMessage(expectedSender, expectedMessageTxt, expectedRoom)

        (api.publishOneMessage _).when(expectedMessage).returns(Future.successful(expectedMessage))

        Post("/api/chat", HttpEntity(ContentTypes.`application/json`, expectedMessageJson)) ~> chatRoutes ~> check {
          assert(status === StatusCodes.OK)
          assert(responseAs[String] === expectedMessageJson)
        }
      }
    }
    "get" - {
      "should return a sourced array of json elements" in {
        val topic = "someTopic"
        val client = UUID.randomUUID().toString
        val chatMessage = ChatMessage("test", "test", "test")
        val source = Source(List(chatMessage))
        (api.getMessages _).when(topic, client).returns(source)
        Get(s"/api/chat?topic=$topic&client=$client") ~> chatRoutes ~> check {
          assert(status === StatusCodes.OK)
          assert(responseAs[List[ChatMessage]] === List(chatMessage))
        }

      }
      "should fail if query paramater is missing" in {
        Get("/api/chat") ~> Route.seal(chatRoutes) ~> check {
          assert(status === StatusCodes.NotFound)
          assert(responseAs[String].contains("topic"))
        }
      }
    }
  }
}
