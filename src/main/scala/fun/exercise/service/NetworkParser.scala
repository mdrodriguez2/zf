package fun.exercise.service

import fun.exercise.model.{NetworkMap, Segment}
import com.vividsolutions.jts.geom.Coordinate
import io.circe._
import io.circe.generic.auto._

object NetworkParser {

  def toNetworkMap(json: Json): Either[Error, NetworkMap] =
    json.as[FeatureCollection].map { featureCollection =>
      val edges = featureCollection.features.flatMap {
        case Feature(geometry, properties) if properties.bidir == 1 =>
          val edges = edgesFromGeometry(geometry)
          val reversedEdges = edges.map(_.reversed)
          edges ++ reversedEdges
        case Feature(geometry, _) =>
          edgesFromGeometry(geometry)
      }
      val coordinateToId = edges.flatMap(_.coordinates).distinct.zipWithIndex.map {
        case (c, idx) => (c, idx + 1)
      }.toMap
      val nodesList = edges
        .flatMap(edge => {
          val nodes = edge.coordinates.flatMap(coordinateToId.get)
          nodes.zip(nodes.tail)
        })
        .toList.distinct
      NetworkMap(coordinateToId.map(_.swap), nodesList.map(Segment.tupled))
    }

  private final case class Geometry(coordinates: Seq[Seq[Double]])
  private final case class Property(bidir: Int)
  private final case class Feature(geometry: Geometry, properties: Property)
  private final case class FeatureCollection(features: Seq[Feature])

  private case class Edge(from: Coordinate, to: Coordinate) {
    def reversed: Edge = Edge(to, from)
    def coordinates: Seq[Coordinate] = List(from, to)
  }

  private def edgesFromGeometry(geometry: Geometry): Seq[Edge] = {
    val coordinates = geometry.coordinates.map(coordinate => new Coordinate(coordinate.head, coordinate(1)))
    coordinates.zip(coordinates.tail).map(Edge.tupled)
  }

}
