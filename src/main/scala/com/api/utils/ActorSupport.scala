package com.api.utils

import akka.actor.ActorSystem
import akka.stream.Materializer

trait ActorSupport {
  implicit val system = ActorSystem("Riverus-riauth-api")
  //implicit val materializer = ActorMaterializer()
  implicit val materializer = Materializer
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

}
