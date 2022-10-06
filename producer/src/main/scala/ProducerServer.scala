import com.producer.Api.callApi
import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContextExecutor
import scala.language.postfixOps
import scala.util.{Failure, Success}
import com.fasterxml.jackson.databind.ObjectWriter
import com.producer.VehicleData

object ProducerServer {

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  val vehicleDataWriter: ObjectWriter = mapper.writerFor(classOf[VehicleData])
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("producer-server")
    implicit val mat = Materializer.matFromSystem(system)
    val config = ConfigFactory.load()
    val topic = config.getString("kafka.topic")
    val bootStripeConfig = system.settings.config.getConfig("akka.kafka.producer")
    val producerSettings =
      ProducerSettings(bootStripeConfig, new StringSerializer, new StringSerializer)
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    system.scheduler.scheduleWithFixedDelay(0 seconds, 2 seconds){
      ()=>
        callApi() onComplete {
          case Success(data) =>
           val vehicles = VehicleData.fromJsonStringToSeq(data)
           Source(vehicles)
             .map(vehicleDataWriter.writeValueAsString(_))
             .map(value => new ProducerRecord[String, String](topic, value))
             .runWith(Producer.plainSink(producerSettings)) onComplete {
             case Success(value) => println(value)
             case Failure(exception) => throw exception
           }
          case Failure(exception) =>
            println(exception.getMessage)
            exception.getMessage
        }


    }
  }
}
