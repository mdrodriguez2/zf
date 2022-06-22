package fun.exercise.service

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import com.vividsolutions.jts.algorithm.CGAlgorithms3D.distance
import com.vividsolutions.jts.geom.{Coordinate, LineSegment}
import fun.exercise.model.TopologicalMap.Vehicle
import fun.exercise.model.{NetworkMap, Segment, VehicleMessage}
import fun.exercise.repository.VehicleRepository

//TODO esto como class no me mola mucho
class VehicleTracker(storage: VehicleRepository, network: NetworkMap) {

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
    val segment: (LineSegment, Segment) = segmentForVehicle(message.coordinate)
    val name                            = message.name
    val position: Double                = positionForVehicle(message.coordinate, segment._1)
    val vehicle: Vehicle                = Vehicle(segment._2, name, position)
    storage.storeVehicle(vehicle)

    println(message.name, message.coordinate, s"stored vehicle $vehicle")
  }

  def segmentForVehicle(vehiclePosition: Coordinate): (LineSegment, Segment) =
    linearSegments.find(elem => elem._1.distance(vehiclePosition) == 0).get //TODO .get peligro

  def positionForVehicle(vehiclePosition: Coordinate, segment: LineSegment): Double =
    distance(vehiclePosition, segment.p0) / distance(segment.p0, segment.p1)

}
