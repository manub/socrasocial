package net.manub.socrasocial

case class PostMessage(message: Message)
case class MessagePosted(message: Message)
case class MessagesFor(user: String)