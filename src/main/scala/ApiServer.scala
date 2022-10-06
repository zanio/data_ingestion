import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.api.utils.ActorSupport
import com.typesafe.config.ConfigFactory

object ApiServer  extends ActorSupport {
  def main(args: Array[String])={

    implicit val config = ConfigFactory.load()
    val host = config.getString("http.ip")
    val port = config.getInt("http.port")

    val routes =pathPrefix("api"){

      VehiclesRoute.route~
        TilesRoute.route
    }

    Http().newServerAt(host,port).bind(routes)

    println(s"Server online at http://$host:$port")
  }
}
