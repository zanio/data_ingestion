package com.consumer.config

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.`type`.codec.TypeCodecs
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import com.typesafe.config.{Config, ConfigFactory}

import java.net.InetSocketAddress

/**
 * Project working on lunatech-aakash-choudhary
 * New File created by ani in  lunatech-aakash-choudhary @ 27/09/2022  13:28
 */

case object DbConfig {
  val config: Config = ConfigFactory.load()
  val host: String = config.getString("cassandra.host")
  val port: Int = config.getInt("cassandra.port")

  def getSession:CqlSession={

    CqlSession.builder()
      .withKeyspace("data_digestion")
      .addContactPoint(new InetSocketAddress(host,port))
      .withLocalDatacenter("datacenter1")
      .addTypeCodecs(TypeCodecs.INT)
      .addTypeCodecs(TypeCodecs.BIGINT)
      .addTypeCodecs(TypeCodecs.DECIMAL)

      .build()
  }


}