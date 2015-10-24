package net.manub.socrasocial

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.testkit.TestKit
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.time.{Second, Milliseconds, Seconds, Span}
import org.scalatest.{Matchers, WordSpecLike}

class SocialNetworkSpec
  extends TestKit(ActorSystem("myTest"))
  with WordSpecLike with Matchers with ScalaFutures with Json4sSupport with Eventually {

  implicit val serialization = Serialization
  implicit val defaultFormats = DefaultFormats
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher

  val user = "emanuele"
  new SocialNetwork().start()
  val message = Message("hello #socratesbe!")

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(1, Second))

  "reading my timeline" should {
    "return an empty list of messages" when {
      "the user hasn't posted any message" in {


        val futureResponse = Http().singleRequest(HttpRequest(method = HttpMethods.GET,
          uri = Uri(s"http://localhost:8080/$user/messages")))

        val response = futureResponse.futureValue
        response.status shouldBe StatusCodes.OK

        val receivedMessages = Unmarshal(response.entity).to[Messages].futureValue
        receivedMessages.messages shouldBe 'empty

      }
    }

    "return my posted messages" in {

      val entity = Marshal(message).to[RequestEntity].futureValue

      val postMessageResponse = Http().singleRequest(HttpRequest(method = HttpMethods.POST,
        uri = Uri(s"http://localhost:8080/$user/messages"), entity = entity)).futureValue

      postMessageResponse.status shouldBe StatusCodes.Accepted

      eventually {
        val getMessagesResponse = Http().singleRequest(HttpRequest(method = HttpMethods.GET,
          uri = Uri(s"http://localhost:8080/$user/messages")))

        val response = getMessagesResponse.futureValue
        response.status shouldBe StatusCodes.OK

        val receivedMessages = Unmarshal(response.entity).to[Messages].futureValue
        receivedMessages.messages should contain only message
      }
    }

    "return only messages posted by the requesting user" in {
      val anotherMessage = Message("hello Dries Mertens")

      Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(s"http://localhost:8080/kris/messages"),
        entity = Marshal(anotherMessage).to[RequestEntity].futureValue)).futureValue

      eventually {
        val getMessagesResponse = Http().singleRequest(HttpRequest(method = HttpMethods.GET,
          uri = Uri(s"http://localhost:8080/kris/messages")))

        val response = getMessagesResponse.futureValue
        response.status shouldBe StatusCodes.OK

        val receivedMessages = Unmarshal(response.entity).to[Messages].futureValue
        receivedMessages.messages should contain only anotherMessage

      }
    }
  }

}
