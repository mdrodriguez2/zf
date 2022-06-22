package fun.exercise

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import fun.exercise.repository.InMemoryVehicleRepository
import fun.exercise.service.{TopologicalMapService, VehicleTracker, VehiclesService}
import io.circe.generic.auto._
import io.circe.syntax._

import scala.sys.exit

object WebServer {
  val mapPath = "src/main/resources/lc-track0-21781.geojson"

  val vehicleStorage        = new InMemoryVehicleRepository
  val topologicalMapService = new TopologicalMapService(vehicleStorage)

  topologicalMapService.init(mapPath) match {
    case Left(err) =>
      println(err.msg)
      exit(1)
    case Right(_) =>
  }

  val tracker = new VehicleTracker(vehicleStorage, topologicalMapService.networkMap)

  val service = Behaviors.setup[Unit] { context =>
    tracker.initConsumer(context)
    VehiclesService(topologicalMapService.networkMap, vehicleCount = 3)(tracker.consumer)
  }

  val route = {
    import akka.http.scaladsl.server.Directives._
    concat(
      path("map") {
        get {
          complete(topologicalMapService.getTopologicalMap.asJson.toString)
        }
      },
      path("eta") {
        get {
          parameters(Symbol("vehicle").as[String]) { vehicle =>
            complete {
              s"ETA for requested vehicle $vehicle for all stations"
            }
          }
        }
      }
    )
  }

  def main(args: Array[String]) {
    implicit val system: ActorSystem[Unit] =
      ActorSystem[Unit](service, "service")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(route)
  }

}
