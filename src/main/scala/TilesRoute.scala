import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.api.daos.TilesDao
import com.api.utils.{StringListHttpResponse, VehicleCountHttpResponse}
import io.circe.generic.auto._
import io.circe.syntax._

import scala.jdk.CollectionConverters.CollectionHasAsScala
object TilesRoute {
  val route = pathPrefix("tiles"){
    path("filled"){
      get{




        complete("tiles")

      }
    }~ path("tile"/"avalablevehicles"){
      get{
        parameter("tileid".as[String]){tileId =>

          val returnVal = TilesDao.getVehicleList(tileId)
          if (returnVal.isEmpty){
            complete(HttpResponse(StatusCodes.NoContent))
          }else{
            complete(HttpResponse(StatusCodes.OK, entity = StringListHttpResponse("Success","vehicle List Generated",returnVal.asScala.toList).asJson.toString))
          }
        }
      }
    }~ path("usecase"/"vehiclecount"){
      get{
        parameter("tileid".as[String].repeated){tileIds =>

          val returnVal = TilesDao.getVehicleCount(tileIds.toList)
          if (returnVal.isEmpty) {
            complete(HttpResponse(StatusCodes.NoContent))
          } else {
            complete(HttpResponse(StatusCodes.OK, entity = VehicleCountHttpResponse("Success","vehicle Count Generated",returnVal).asJson.toString))
          }
        }
      }
    }







  }

}
