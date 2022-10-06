package com.consumer.repository

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.{AsyncResultSet, ResultSet}
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.{ExecutionContextExecutor, Future}
import com.producer._
import service.TileSystem

import java.util
import java.util.concurrent.CompletionStage

/**
 * Project working on lunatech-aakash-choudhary
 * New File created by ani in  lunatech-aakash-choudhary @ 27/09/2022  13:13
 */
class DataIngestionRepository(session: CqlSession) {
  val levelOfDetails = 3

  def saveIfNotExist(vehicleData: VehicleData): CompletionStage[AsyncResultSet] = {
    val vehicleLocationQueries = s"insert into vehicle_location(vehicle_id,insertion_time, latitude,longitude) values(${vehicleData.routeId.toString},toUnixTimestamp(now()),${vehicleData.latitude},${vehicleData.longitude});"
    val vehiclesQueries = s"insert into vehicles(id) values('${vehicleData.routeId.toString}');"
    session.executeAsync(vehiclesQueries)
    session.executeAsync(vehicleLocationQueries)
  }

  //    1. select * from tiles where vehicleId = ?
  //    2. from the result returned find one with desc order and get the quad_key, if it doesn't exist then insert the new quadKey, vehicleId and timestamp into the
  //    tiles table also insert into the tile_vehicles
  //    3. if the query from no 2 exist then select * from tile_vehicles where quad_key = ? ( quad key from no two ) which returns the old quad_key, if the old key is not equals
  //    to the value of the new calculated quad key then check if the vehicle exist in the list of vehicle within that quad_key and remove the vehicle and reinsert what is left
  //    with the old quad_key.
  //    5 again with the newly calculated quad_key , select * from tile_vehicles where quad_key = ? ( new calculated quad key ) if result is returned append the vehicleId to the list of vehicle_ids returned and safe into the db
  //    4. if no 3 returns no value then go ahead
  //    ???
  private def save(vehicleData: VehicleData):Unit ={
    val newQuadKey = TileSystem.calculateQuadKey(vehicleData.latitude,vehicleData.longitude,levelOfDetails)
    val findOneTileQueries = s"select * from tiles where vehicle_id = ${vehicleData.routeId} limit 1"
    val rowsSet = session.execute(findOneTileQueries).all

    if(rowsSet.size()>0){
      val row =rowsSet.get(0)
      val findOldQuadOne = s"select * from tile_vehicles where quad_key = '${row.getString("quad_key")}' limit 1"
      val findNewQuadOne = s"select * from tile_vehicles where quad_key = '${newQuadKey}' limit 1"
      val  oldRowSet = session.execute(findOldQuadOne).all
      val  newRowSet = session.execute(findNewQuadOne).all
      if(oldRowSet.size() >0){
        println(findOldQuadOne)

        val vehiclesArrayList = oldRowSet.get(0).getSet("vehicle_ids", classOf[java.lang.Integer])
        if(newQuadKey !=  row.getString("quad_key")){
          vehiclesArrayList.removeIf(_==vehicleData.routeId)
          session.execute(s"insert into tile_vehicles(vehicle_ids,insertion_time, quad_key) values({${convertToCassandraSetType(vehiclesArrayList)}},toUnixTimestamp(now()), '${row.getString("quad_key")}')")
        }
      }
      if(newRowSet.size() > 0){
        val newVehiclesIds =newRowSet.get(0).getSet("vehicle_ids", classOf[Integer])
        newVehiclesIds.add(vehicleData.routeId)
        session.execute(s"insert into tile_vehicles(vehicle_ids,insertion_time, quad_key) values({${convertToCassandraSetType(newVehiclesIds)}},toUnixTimestamp(now()), '${newQuadKey}')")
      } else {
        session.execute(s"insert into tile_vehicles(vehicle_ids,insertion_time, quad_key) values({${vehicleData.routeId}},toUnixTimestamp(now()), '${newQuadKey}')")
      }
    } else {
      session.execute(s"insert into tile_vehicles(vehicle_ids,insertion_time, quad_key) values({${vehicleData.routeId}},toUnixTimestamp(now()), '$newQuadKey')")
    }
    session.execute(s"insert into tiles(vehicle_id,insertion_time, quad_key) values(${vehicleData.routeId},toUnixTimestamp(now()), '$newQuadKey')")
    saveTileVehicleCount(newQuadKey)
  }

  def doBusinessLogic(record: ConsumerRecord[String, String])(implicit ec: ExecutionContextExecutor):Future[CompletionStage[AsyncResultSet]]={
    val vehicleData = VehicleData.fromJsonStringToVehicleData(record.value())
    try{
      save(vehicleData)
      Future.apply(saveIfNotExist(vehicleData))
    } catch {
      case e: Exception =>
        Future.failed(e)
    }
  }

  private def convertToCassandraSetType(sets: util.Set[Integer]):AnyRef = {
    sets.toArray.mkString("", ",", "")
  }

  private def saveTileVehicleCount(quad_key:String): Unit = {
//  first check if the quad_key exist in the tile_vehicle_count table,
    //  if it doesn't then perform and insert of the quad_key and  the number of vehicle_ids matching the
    //  quad key after the query on the tile_vehicles is perform.

//    if the quad key exist then perform and update on the vehicleCount from the query response of number of vehicle_ids on tile_vehicles
    val findOneTileVehicleCount = session.execute(s"select * from tile_vehicle_count where quad_key = '$quad_key'").all
    val findlatestQuadkeyVehicleCount = session.execute(s"select * from tile_vehicles where quad_key = '$quad_key' limit 1").all
    if(findlatestQuadkeyVehicleCount.size() > 0){
      val newVehiclesIds = findlatestQuadkeyVehicleCount.get(0).getSet("vehicle_ids", classOf[Integer])
      if(findOneTileVehicleCount.size() > 0){
        session.execute(s"update tile_vehicle_count set vehicle_count = ${newVehiclesIds.size()} where quad_key = '$quad_key'")
      } else {
        session.execute(s"insert into tile_vehicle_count(quad_key, vehicle_count) values('$quad_key', ${newVehiclesIds.size()})")
      }
    } else {
      session.execute(s"insert into tile_vehicle_count(quad_key, vehicle_count) values('$quad_key', 0)")
    }

  }

}
