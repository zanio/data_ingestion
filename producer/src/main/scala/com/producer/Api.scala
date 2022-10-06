package com.producer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

/**
 * Project working on lunatech-aakash-choudhary
 * New File created by ani in  lunatech-aakash-choudhary @ 15/09/2022  16:25
 */

case class ApiResponseException(message: String, cause: Throwable) extends RuntimeException(message, cause)

case class VehicleData(id:Int,
                       routeId:Int,
                       runId:Int,
                       predictable: Boolean,
                       heading:Int,
                       latitude:Double,
                      longitude:Double,
                      secondsSinceReport:Int)

object VehicleData{

  val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)

  def toJson(value: Any)(implicit mapper: ObjectMapper): String = {
    mapper.writeValueAsString(value)
  }
  def fromJsonStringToSeq(json:String): Seq[VehicleData] = {
 val mapRes = mapper.readValue(json, classOf[Seq[Map[String, Int]]])
    mapRes.map(it=>
      VehicleData(it("id"),
        it("routeId"),
        it("runId"),
        it("predictable").asInstanceOf[Boolean],
        it("heading"),
        it("latitude").asInstanceOf[Double],
        it("longitude").asInstanceOf[Double],
        it("secondsSinceReport")))

  }
  def fromJsonStringToVehicleData(json:String): VehicleData = {
  try {
    val it = mapper.readValue(json, classOf[Map[String, Any]])
    VehicleData(it("id").asInstanceOf[Int],
      it("routeId").asInstanceOf[Int],
      it("runId").asInstanceOf[Int],
      it("predictable").asInstanceOf[Boolean],
      it("heading").asInstanceOf[Int],
      it("latitude").asInstanceOf[Double],
      it("longitude").asInstanceOf[Double],
      it("secondsSinceReport").asInstanceOf[Int])
  } catch {
    case e: Exception => throw e
  }


  }
}
object Api {

  def callApi()(implicit system : ActorSystem): Future[String] = {
    implicit val ec: ExecutionContextExecutor = system.dispatcher
    val config = ConfigFactory.load()
    val url = config.getString("http.url")
    val response =  Http().singleRequest(HttpRequest(uri = url+"/api/vehicle"))
    response.flatMap(_.entity.toStrict(1 seconds))
          .map(_.data.utf8String)
    .recoverWith{
      case ex =>
        Future.failed(ApiResponseException(ex.getMessage,ex))
    }
  }

}



