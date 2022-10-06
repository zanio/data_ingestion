import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

import scala.util.Random

object DataSource {
    def main(args: Array[String])={

      implicit val system = ActorSystem("Fake-Data-Source")
      implicit val config = ConfigFactory.load()
      val host = config.getString("http.ip")
      val port = config.getInt("http.port")

      val vehicleIds =(10 to 100).toList
      var  constantId  = 0
      def routes = pathPrefix("api"/"vehicle") {
        pathEnd {
          get {
            val data = vehicleIds.map{id =>
              constantId = constantId + 1
              s"""
                |{
                | "id": $constantId,
                | "routeId": $id,
                | "runId": ${Random.between(100,150)},
                | "predictable": true,
                | "heading": ${Random.between(100,150)},
                | "longitude": ${Random.between(-180,180)},
                | "latitude": ${Random.between(-180,180)},
                | "secondsSinceReport": ${Random.between(1,10)}
                |}
                |""".stripMargin
            }
            complete(HttpResponse(StatusCodes.OK, entity = "[ " + data.foldLeft("")(_ + "," + _).substring(1) + " ]"))
          }
        }
      }

      Http().newServerAt(host, port).bind(routes)
      println(s"Server online at http://$host:$port/\nPress RETURN to stop...")

      }
}
