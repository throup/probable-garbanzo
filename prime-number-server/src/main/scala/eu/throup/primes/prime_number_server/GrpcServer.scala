package eu.throup
package primes
package prime_number_server

import com.typesafe.scalalogging.Logger
import io.grpc.ServerServiceDefinition
import io.grpc.netty.NettyServerBuilder

object GrpcServer {
  private val Log = Logger(this.getClass)

  def apply(serviceDefinition: ServerServiceDefinition, serverName: String = "GrpcServer", port: Int = 8080): Unit = {
    Log.info("Building {}", serverName)
    val server = buildServer(serviceDefinition, port)
    Log.info("Launching {} on port {}", serverName, port)
    server.start
    Log.info("{} started", serverName)
    server.awaitTermination()
    Log.info("{} terminated", serverName)
  }

  private def buildServer(serviceDefinition: ServerServiceDefinition, port: Int) =
    NettyServerBuilder.forPort(port).addService(serviceDefinition).build
}
