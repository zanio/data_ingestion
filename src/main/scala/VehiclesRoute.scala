import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.api.daos.VehicleDao
import com.api.utils.{StringListHttpResponse, StringVehicleListHttpResponse, VehicleLocationHttpResponse}
import io.circe.generic.auto._
import io.circe.syntax._

object VehiclesRoute {
  val route = pathPrefix("vehicles"){
    path("list"){
      get{

        val returnVal = VehicleDao.getVehicleList
        if (returnVal.isEmpty){
          complete(HttpResponse(StatusCodes.NoContent))
        }else{
          complete(HttpResponse(StatusCodes.OK, entity = new StringVehicleListHttpResponse("Success","vehicle List Generated",returnVal).asJson.toString))
        }
      }
    }~ path("vehicle"/"lastposition"){
      get{
        parameter("vehicleid".as[String]){vehicleId =>

          val returnVal = VehicleDao.getVehiclePosition(vehicleId)
          if (returnVal != null){
            complete(HttpResponse(StatusCodes.OK, entity = new VehicleLocationHttpResponse("Success","vehicle Position",returnVal).asJson.toString))
          }else {
            complete(HttpResponse(StatusCodes.NoContent))
          }
        }
      }

      }

  }

}
