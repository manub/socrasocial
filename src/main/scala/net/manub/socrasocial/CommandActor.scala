package net.manub.socrasocial

import akka.persistence.PersistentActor
import com.typesafe.scalalogging.LazyLogging

class CommandActor(user: String) extends PersistentActor with LazyLogging {

  override def persistenceId = s"messages-$user"

  override def receiveCommand: Receive = {
    case PostMessage(message) =>
      persist(MessagePosted(message)) { event =>
        logger.warn(s"persisted $event")
      }
  }
  
  override def receiveRecover: Receive = {
    case _ =>
  }

}
