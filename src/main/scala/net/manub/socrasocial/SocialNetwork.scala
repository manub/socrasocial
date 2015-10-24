package net.manub.socrasocial

import akka.actor.{ActorRef, Props, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization

import akka.pattern.ask
import scala.concurrent.duration._

class SocialNetwork(implicit actorSystem: ActorSystem) extends Json4sSupport with LazyLogging {
  def start(): Unit = {

    import actorSystem.dispatcher
    implicit val materializer = ActorMaterializer()
    implicit val serialization = Serialization
    implicit val defaultFormats = DefaultFormats
    implicit val timeout = Timeout(3.seconds)

    var commandActors = Map.empty[String, ActorRef]

    val queryActor = actorSystem.actorOf(Props(classOf[QueryActor]))

    val route =
      path(Segment / "messages") { user =>
        get {
          onComplete((queryActor ? MessagesFor(user)).mapTo[Messages]) { messages =>
            complete(messages)
          }
        } ~
        post {
          entity(as[Message]) { message =>

            val commandActor = commandActors.getOrElse(user, {
              val newActor = actorSystem.actorOf(Props(classOf[CommandActor], user))
              commandActors += (user -> newActor)
              newActor
            })

            logger.info("received message")
            val command = PostMessage(message)
            commandActor ! command
            complete(StatusCodes.Accepted)
          }
        }

      }


    Http().bindAndHandle(route, "localhost", 8080)
  }

}
