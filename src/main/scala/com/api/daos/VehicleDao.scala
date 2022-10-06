package com.api.daos

import com.api.model.VehicleLocation
import com.consumer.config.DbConfig.{getSession => dbSession}
import com.datastax.oss.driver.api.core.cql.ResultSet

import scala.collection.mutable.ListBuffer

object VehicleDao {

  private def convertVehicleList(rows: ResultSet) = {
    val vehicleIds = new ListBuffer[String]
    rows.all().forEach{row =>
      vehicleIds.addOne(row.getString("vehicle_id"))
    }

    vehicleIds.toList
  }
  def getVehicleList: Seq[String] ={

    val statement = dbSession.prepare("select distinct vehicle_id from data_digestion.vehicle_location").bind()
    val resultSet = dbSession.execute(statement)
    convertVehicleList(resultSet)

  }

  private def convertVehiclePosition(rows: ResultSet) = {

    var vehicleLocation:VehicleLocation = null
    rows.all.forEach{row =>
      vehicleLocation = VehicleLocation(row.getString("vehicle_id"),row.getDouble(1),row.getDouble(2))
    }
    vehicleLocation
  }
  def getVehiclePosition(vehicleId:String): VehicleLocation = {

    val statement = dbSession.prepare("select vehicle_id,latitude,longitude from data_digestion.vehicle_location where vehicle_id = ? limit 1").bind(vehicleId)
    val resultSet = dbSession.execute(statement)
    convertVehiclePosition(resultSet)
  }

}
