package fun.exercise.service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.vividsolutions.jts.geom.Coordinate
import fun.exercise.model.{NetworkMap, Segment, VehicleMessage}

import scala.concurrent.duration._
import scala.math._

object VehiclesService {
  private val rng = new scala.util.Random(0)
  final case class NetworkLocation(segment: Segment, position: Double)

  def apply(network: NetworkMap, vehicleCount: Int)(
      consumer: ActorRef[VehicleMessage]
  ): Behavior[Unit] = {
    val names: Array[String] =
      ('a' to 'z').take(vehicleCount).map(_.toString).toArray

    val locations: Array[NetworkLocation] = (0 to vehicleCount).map { _ =>
      val edge = network.segments(rng.nextInt(network.segments.size))
      NetworkLocation(edge, 0.0)
    }.toArray

    Behaviors.withTimers { scheduler =>
      scheduler.startTimerAtFixedRate((), 250.milliseconds)
      Behaviors.receiveMessage(_ => {
        (0 until vehicleCount).foreach { i =>
          consumer ! VehicleMessage(
            names(i),
            simulateCoordinates(network, locations, i)
          )
        }
        Behaviors.same
      })
    }
  }

  private def simulateCoordinates(
      network: NetworkMap,
      locations: Array[NetworkLocation],
      i: Int
  ) = {
    val NetworkLocation(Segment(source, target), pos0) = locations(i)

    val distance = (1 + rng.nextInt(8)).toDouble / 100
    val pos1 = min(pos0 + distance, 1.0)

    if (pos1 >= 1.0) {
      val edges = network.segments.filter { case Segment(src, _) =>
        src == target
      }
      val edge = edges(rng.nextInt(edges.size))
      locations.update(i, NetworkLocation(edge, 0.0))
    } else {
      locations.update(
        i,
        NetworkLocation(Segment(source, target), pos1)
      )
    }

    val sourceCoord = network.nodes(source)
    val targetCoord = network.nodes(target)

    val dx = sourceCoord.x - targetCoord.x
    val dy = sourceCoord.y - targetCoord.y
    val length = sqrt(dx * dx + dy * dy) * pos1

    def round(d: Double, i: Int) = rint(d * i) / 100

    val theta = atan2(
      targetCoord.y - sourceCoord.y,
      targetCoord.x - sourceCoord.x
    )

    new Coordinate(
      round(sourceCoord.x + (cos(theta) * length), 100),
      round(sourceCoord.y + (sin(theta) * length), 100),
      0
    )
  }
}
