package fun.exercise.model

import fun.exercise.model.TopologicalMap._
import fun.exercise.model.VehicleMessage.VehicleName
import com.vividsolutions.jts.geom.Coordinate

/** A network map encodes the representation of the road network together with points of interest
  */
final case class NetworkMap(
    nodes: Map[Int, Coordinate],
    segments: List[Segment]
)

/** Directed segments connecting stations
  * @param origin  Source station identifier
  * @param destination Target station identifier
  */
final case class Segment(origin: Int, destination: Int)

/** A topological map encode an abstract representation of the transport network.
  *
  * Only stations, segments (connections between stations) and vehicles are displayed.
  *
  * Segment size is relative (fixed or proportional) and does not reflect the scale of the real world network.
  *
  * Example: https://en.wikipedia.org/wiki/Topological_map
  */
final case class TopologicalMap(
    stations: Map[StationID, StationName],
    segments: List[SegmentSize],
    vehicles: List[Vehicle]
)
object TopologicalMap {
  type StationID = Int
  type StationName = String

  /** Segment size
    * @param segment segment
    * @param size Relative size (between 0.0 and 1.0)
    */
  final case class SegmentSize(segment: Segment, size: Double)

  final case class Vehicle(
      segment: Segment,
      name: VehicleName,
      position: Double
  )
}

/** A vehicle message encode all information sent periodically by a running vehicle.
  */
final case class VehicleMessage(
    name: VehicleName,
    coordinate: Coordinate
)
object VehicleMessage {
  type VehicleName = String
}
