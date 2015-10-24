package net.manub.socrasocial

import akka.actor.Actor
import akka.persistence.inmemory.query.InMemoryReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

class QueryActor extends Actor with LazyLogging {

  val readJournal = PersistenceQuery(context.system).readJournalFor[InMemoryReadJournal](InMemoryReadJournal.Identifier)
  var messages: Map[String, List[Message]] = Map.empty

  implicit val materializer = ActorMaterializer()

  readJournal.allPersistenceIds().runForeach { persistenceId =>

    messages += persistenceId -> List.empty

    readJournal.eventsByPersistenceId(persistenceId).runForeach { eventEnvelope =>
      eventEnvelope.event match {
        case MessagePosted(message) =>
          logger.info(s"received $message for $persistenceId")
          messages += persistenceId -> (messages(persistenceId) :+ message)
      }
    }

  }


  //  }

  override def receive = {
    case MessagesFor(user) => sender() ! Messages(messages.getOrElse(s"messages-$user", List.empty))
  }

}
