package com.yoppworks.chat.infrastructure

import akka.actor.ActorSystem
import akka.kafka.{ ConsumerSettings, Subscriptions }
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.yoppworks.chat.domain.{ ChatMessage, ChatMessageConsumer }
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{ ByteArrayDeserializer, StringDeserializer }

import scala.concurrent.Future

class KafkaChatMessageConsumer(implicit val system: ActorSystem, implicit val materializer: ActorMaterializer) extends ChatMessageConsumer {
  private val serverAddress = "localhost:9092"

  def  get(topic: String, client: String) : Source[ChatMessage,Consumer.Control] = {
    val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers(serverAddress)
      .withGroupId(client)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val subscription = Subscriptions.topics(topic)

    Consumer.plainSource(consumerSettings, subscription).mapAsync(1)(v => Future.successful(ChatMessage(v.value())))
  }
}
