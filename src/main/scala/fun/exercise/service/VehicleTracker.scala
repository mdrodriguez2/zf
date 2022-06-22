package fun.exercise.service

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.vividsolutions.jts.algorithm.CGAlgorithms3D.distance
import com.vividsolutions.jts.geom.{Coordinate, LineSegment}
import fun.exercise.model.TopologicalMap.Vehicle
import fun.exercise.model.{NetworkMap, Segment, VehicleMessage, VehicleNotInSegmentError}
import fun.exercise.repository.VehicleRepository

class VehicleTracker(storage: VehicleRepository, network: NetworkMap) {
  val tolerance: Double                  = 1
  var consumer: ActorRef[VehicleMessage] = _

  val linearSegments: Seq[(LineSegment, Segment)] =
    network.segments.map { segment =>
      val origin      = network.nodes(segment.origin)
      val destination = network.nodes(segment.destination)
      (new LineSegment(origin, destination), segment)
    }

  def initConsumer(context: ActorContext[Unit]): Unit = {
    consumer = context.spawn[VehicleMessage](
      Behaviors.receiveMessage(message => {
        onMessageReceived(message)
        Behaviors.same
      }),
      "consumer"
    )
  }

  def onMessageReceived(message: VehicleMessage): Unit = {
    val (name, coordinate)                           = (message.name, message.coordinate)
    val maybeSegment: Option[(LineSegment, Segment)] = segmentForVehicle(message.coordinate)
    val segment                                      = maybeSegment.getOrElse(throw VehicleNotInSegmentError(name, coordinate))
    val position: Double                             = positionForVehicle(coordinate, segment._1)
    val vehicle: Vehicle                             = Vehicle(segment._2, name, position)

    storage.storeVehicle(vehicle)

    println(message.name, message.coordinate, s"stored vehicle $vehicle")
  }

  def segmentForVehicle(vehiclePosition: Coordinate): Option[(LineSegment, Segment)] =
    linearSegments.find(elem => elem._1.distance(vehiclePosition) < tolerance)

  def positionForVehicle(vehiclePosition: Coordinate, segment: LineSegment): Double =
    distance(vehiclePosition, segment.p0) / distance(segment.p0, segment.p1)

}
