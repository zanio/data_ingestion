package com.api.model

case class VehicleLocation
(
  vehicleId: String,
  latitude: Double,
  longitude:Double
)

case class VehicleCount
(
  quadKey: String,
  vehicleCount: Int
)