package fun.exercise

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.vividsolutions.jts.geom.Coordinate
import fun.exercise.model._
import service.VehiclesService

object WebServer {
  val squareNetwork = NetworkMap(
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

  val service = Behaviors.setup[Unit] { context =>
    val consumer =
      context.spawn[VehicleMessage](
        Behaviors.receiveMessage(message => {
          println(message.name, message.coordinate)
          Behaviors.same
        }),
        "consumer"
      )
    VehiclesService(squareNetwork, vehicleCount = 3)(consumer)
  }

  val route = {
    import akka.http.scaladsl.server.Directives._
    pathSingleSlash {
      get {
        complete {
          "Hello world"
        }
      }
    }
  }

  def main(args: Array[String]) {
    implicit val system: ActorSystem[Unit] =
      ActorSystem[Unit](service, "service")
    Http()
      .newServerAt("localhost", 8080)
      .bindFlow(route)
  }

}
