package service

import scala.collection.mutable

/**
 * Project working on lunatech-aakash-choudhary
 * New File created by ani in  lunatech-aakash-choudhary @ 28/09/2022  11:36
 */

object TileSystem {
  private val EarthRadius:Double = 6378137;
  private val MinLatitude: Double = -85.05112878;
  private val MaxLatitude: Double = 85.05112878;
  private val MinLongitude : Double = -180;
  private val MaxLongitude : Double = 180;

  private def Clip(n: Double, minValue: Double, maxValue: Double) = Math.min(Math.max(n, minValue), maxValue)

  private def mapSize(levelOfDetail: Int): Long ={
     256 << levelOfDetail;
    }
  private def  latLongToPixelXY(latitude: Double, longitude : Double,  levelOfDetail:Int):Map[String, Double]= {
    val lat = Clip(latitude, MinLatitude, MaxLatitude);
    val long = Clip(longitude, MinLongitude, MaxLongitude);

    val x = (lat + 180) / 360;
    val sinLatitude = Math.sin(long * Math.PI / 180);
    val y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

    val mapS = mapSize(levelOfDetail);
    val pixelX = Clip(x * mapS + 0.5, 0, mapS - 1.0);
    val pixelY = Clip(y * mapS + 0.5, 0, mapS - 1.0);
    Map("pixelX"-> pixelX, "pixelY" -> pixelY)
  }

 private def pixelXYToTileXY(latitude: Double, longitude : Double,  levelOfDetail:Int):Map[String, Int] = {
    val xy = latLongToPixelXY(latitude, longitude, levelOfDetail)
    Map("tileX"-> (xy("pixelX") / 256).asInstanceOf[Int], "tileY" -> (xy("pixelY") / 256).asInstanceOf[Int])
  }

 private def TileXYToQuadKey(tileX: Int, tileY: Int, levelOfDetail: Int): String = {
    val quadKey: mutable.StringBuilder = new mutable.StringBuilder
    for (i <- levelOfDetail until 0 by -1) {
      var digit = '0'.asDigit
      val mask: Int = 1 << (i - 1)
      if ((tileX & mask) != 0) digit += 1
      if ((tileY & mask) != 0) {
        digit += 1
        digit += 1
      }
      quadKey.append(digit)
    }
    quadKey.toString()
  }
  def calculateQuadKey (latitude: Double, longitude : Double,  levelOfDetail:Int) : String ={
    val mapXy = pixelXYToTileXY(latitude, longitude, levelOfDetail)
    TileXYToQuadKey(mapXy("tileX"),mapXy("tileY"), levelOfDetail)
  }
}
