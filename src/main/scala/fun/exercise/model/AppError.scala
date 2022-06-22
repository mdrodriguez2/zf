package fun.exercise.model

import com.vividsolutions.jts.geom.Coordinate

trait AppError extends Throwable {
  val msg: String
}

case class VehicleNotInSegmentError(name: String, coordinate: Coordinate) extends AppError {
  override val msg: String = s"vehicle $name with coordinate ${coordinate.toString} cannot be asigned to any segment"
}

case class MapCannotBeParsed(path: String, error: String) extends AppError {
  override val msg: String = s"map $path cannot be parsed. Error: $error"
}
