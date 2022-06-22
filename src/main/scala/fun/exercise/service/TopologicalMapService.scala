package fun.exercise.service

import com.vividsolutions.jts.algorithm.CGAlgorithms3D.distance
import fun.exercise.model.TopologicalMap.{SegmentSize, StationID, StationName}
import fun.exercise.model._
import fun.exercise.repository.VehicleRepository
import io.circe.parser.parse

import java.nio.file.{Files, Paths}

class TopologicalMapService(vehicleRepository: VehicleRepository) {

  var networkMap: NetworkMap                = _
  var stations: Map[StationID, StationName] = _
  var segments: List[SegmentSize]           = _

  def init(mapPath: String): Either[AppError, Unit] =
    parseNetworkMap(mapPath).map { network =>
      networkMap = network
      stations   = extractStations(networkMap)
      segments   = extractSegments(networkMap)
    }

  def getTopologicalMap: TopologicalMap =
    TopologicalMap(stations, segments, vehicleRepository.getAll)

  private def parseNetworkMap(path: String): Either[AppError, NetworkMap] =
    (for {
      json <- parse(Files.readString(Paths.get(path)))
      map  <- NetworkParser.toNetworkMap(json)
    } yield map).left.map(error => MapCannotBeParsed(path, error.getMessage))

  private[service] def extractStations(networkMap: NetworkMap): Map[StationID, StationName] =
    networkMap.nodes.map(node => (node._1, s"${node._2.x}-${node._2.y}"))

  private[service] def extractSegments(networkMap: NetworkMap): List[SegmentSize] = {
    val rawSegments       = networkMap.segments.map(segment => segmentSize(segment, networkMap))
    val maxLength: Double = rawSegments.map(_.size).max
    val normalizedSegments =
      rawSegments.map(segmentSize => SegmentSize(segmentSize.segment, segmentSize.size / maxLength))
    normalizedSegments
  }

  private[service] def segmentSize(segment: Segment, networkMap: NetworkMap): SegmentSize =
    SegmentSize(segment, distance(networkMap.nodes(segment.origin), networkMap.nodes(segment.destination)))
}
