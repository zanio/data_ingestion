package com.api.daos

import com.api.model.VehicleCount
import com.consumer.config.DbConfig.{getSession => dbSession}
import com.datastax.oss.driver.api.core.cql.ResultSet

import java.util
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._

object TilesDao {

//  def getTileList() = {
//
//
//  }

  private def convertVehicleList(rows: ResultSet) = {

    var vehicleList: util.Set[Integer] = new util.HashSet[Integer]()
    rows.all().forEach { row =>
      vehicleList = row.getSet(0, classOf[java.lang.Integer])
    }
    vehicleList

  }

  def getVehicleList(quadKey: String): util.Set[Integer] = {
    val statement = dbSession.prepare("select vehicle_ids from tile_vehicles where quad_key = ? limit 1 ")
      .bind(quadKey)
    val resultSet = dbSession.execute(statement)
    convertVehicleList(resultSet)
  }


  private def convertVehicleCount(rows: ResultSet) = {

    val vehicleCounts = new ListBuffer[VehicleCount]
    rows.all().forEach{ row =>
      vehicleCounts += VehicleCount(row.getString("quad_key"),row.getInt("vehicle_count"))
    }
    vehicleCounts.toList
  }
  def getVehicleCount(quadKey: List[String]): Seq[VehicleCount] ={

    val statement = dbSession.prepare("select * from tile_vehicle_count where quad_key in ? ")
      .bind(quadKey.asJava)
    val resultSet = dbSession.execute(statement)
    convertVehicleCount(resultSet)
  }

}
