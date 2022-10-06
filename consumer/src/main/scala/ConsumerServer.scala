import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.kafka.{CommitterSettings, ConsumerSettings, Subscriptions}
import akka.stream.Materializer
import com.consumer.config.DbConfig
import com.consumer.repository.DataIngestionRepository
import com.typesafe.config.ConfigFactory
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContextExecutor

object ConsumerServer {
  def main(args: Array[String]): Unit = {
    val dataIngestionRepository: DataIngestionRepository = new DataIngestionRepository(DbConfig.getSession);
    implicit val system: ActorSystem = ActorSystem("consumer-server")
    implicit val mat = Materializer.matFromSystem(system)
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    val configLoader = ConfigFactory.load()
    val topic = configLoader.getString("kafka.topic")
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
        .withGroupId("group"+topic)
    val committerSettings = CommitterSettings(system)

    val control =
      Consumer
        .committableSource(consumerSettings, Subscriptions.topics(topic))
        .mapAsync(1) { msg =>
          dataIngestionRepository.doBusinessLogic(msg.record)
            .map(_ => msg.committableOffset)
        }
        .toMat(Committer.sink(committerSettings))(DrainingControl.apply)
        .run
//        .runWith(Sink.ignore)
//    Thread.sleep(2000)
//    control onComplete {
//      case Success(value) => println(value)
//      case Failure(exception) => throw exception
//    }

  }
}
