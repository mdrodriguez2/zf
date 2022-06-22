package fun.exercise

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.vividsolutions.jts.geom.Coordinate
import fun.exercise.model._
import fun.exercise.repository.InMemoryVehicleRepository
import fun.exercise.service.{TopologicalMapService, VehicleTracker, VehiclesService}

object WebServer {
  val squareNetwork: NetworkMap = NetworkMap(
    nodes = Map(
      (1, new Coordinate(0, 0)),
      (2, new Coordinate(10, 0)),
      (3, new Coordinate(0, 10)),
      (4, new Coordinate(10, 10))
    ),
    segments = List(
      Segment(1, 3),
      Segment(3, 4),
      Segment(4, 2),
      Segment(2, 1)
    )
  )

  val vehicleStorage        = new InMemoryVehicleRepository
  val tracker               = new VehicleTracker(vehicleStorage, squareNetwork)
  val topologicalMapService = new TopologicalMapService(vehicleStorage)

  val service = Behaviors.setup[Unit] { context =>
    tracker.initConsumer(context)
    VehiclesService(squareNetwork, vehicleCount = 3)(tracker.consumer)
  }

  val route = {
    import akka.http.scaladsl.server.Directives._
    concat(
      path("map") {
        get {
          complete(topologicalMapService.getTopologicalMap.toString) //TODO MANUEL hacer esto JSON
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
