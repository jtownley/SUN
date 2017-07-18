package com.yoppworks.chat.infrastructure

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import com.yoppworks.chat.domain.{ ChatMessage, ChatMessagePublisher }
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer }
import akka.stream.scaladsl.Source
import scala.concurrent.ExecutionContext.Implicits.global

class KafkaChatMessagePublisher(implicit val system: ActorSystem, implicit val materializer: ActorMaterializer) extends ChatMessagePublisher {

  private val serverAddress = "localhost:9092"

  private val keySerializer = new ByteArraySerializer
  private val valueSerializer = new StringSerializer
  val producerSettings = ProducerSettings(system, keySerializer, valueSerializer).withBootstrapServers(serverAddress)

  def one(message: ChatMessage) = {
    println("Publishing: " + message)
    Source(List(message))
      .map( message => new ProducerRecord[Array[Byte], String](message.room, message.asJsonString))
      .runWith(Producer.plainSink(producerSettings)).map(_ => message)
  }
}