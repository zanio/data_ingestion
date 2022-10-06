package com.api.utils

import com.api.model.{VehicleCount, VehicleLocation}

abstract class CustomHttpResponse
(
  code: String,
  message: String,
  queryResult: Any
)

abstract class CustomHttpMessageResponse
(
  code: String,
  message: String
)

// case class for message response
case class HttpMessageResponse
(code: String,message: String) extends CustomHttpMessageResponse(code,message)

case class StringListHttpResponse
(code: String,message: String, queryResult: Seq[Integer]) extends CustomHttpResponse(code,message,queryResult)
case class StringVehicleListHttpResponse
(code: String,message: String, queryResult: Seq[String]) extends CustomHttpResponse(code,message,queryResult)

case class VehicleLocationHttpResponse
(code: String,message: String, queryResult: VehicleLocation) extends CustomHttpResponse(code,message,queryResult)
case class VehicleCountHttpResponse
(code: String,message: String, queryResult: Seq[VehicleCount]) extends CustomHttpResponse(code,message,queryResult)