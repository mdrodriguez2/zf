package fun.exercise.service

import com.vividsolutions.jts.algorithm.CGAlgorithms3D.distance
import fun.exercise.model.TopologicalMap.{SegmentSize, StationID, StationName}
import fun.exercise.model.{NetworkMap, Segment, TopologicalMap}
import fun.exercise.repository.VehicleRepository
import io.circe.parser.parse

import java.nio.file.{Files, Paths}

class TopologicalMapService(vehicleRepository: VehicleRepository) {

  val mapPath = "src/main/resources/lc-track0-21781.geojson"

  def getTopologicalMap: TopologicalMap = {
    val networkMap: NetworkMap                = parseNetworkMap(mapPath)
    val stations: Map[StationID, StationName] = extractStations(networkMap)
    val segments: List[SegmentSize]           = extractSegments(networkMap)
    val vehicles                              = vehicleRepository.getAll
    TopologicalMap(stations, segments, vehicles)
  }

  private def parseNetworkMap(path: String): NetworkMap = {
    val res = for {
      json <- parse(Files.readString(Paths.get(path)))
      map  <- NetworkParser.toNetworkMap(json)
    } yield map
    res.right.get
  }

  private[service] def extractStations(networkMap: NetworkMap): Map[StationID, StationName] =
    networkMap.nodes.map(node => (node._1, s"${node._2.x}-${node._2.y}"))

  private[service] def extractSegments(networkMap: NetworkMap): List[SegmentSize] = {
    val rawSegments       = networkMap.segments.map(segment => segmentSize(segment, networkMap))
    val maxLength: Double = rawSegments.map(_.size).max
    val normalizedSegments =
      rawSegments.map(segmentSize => SegmentSize(segmentSize.segment, segmentSize.size / maxLength))
    normalizedSegments
  }

  //TODO this method is unsafe, crashes if the segment origin is not in the network map
  private[service] def segmentSize(segment: Segment, networkMap: NetworkMap): SegmentSize =
    SegmentSize(segment, distance(networkMap.nodes(segment.origin), networkMap.nodes(segment.destination)))

}
