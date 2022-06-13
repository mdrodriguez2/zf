package fun.exercise.repository

import com.vividsolutions.jts.geom.Coordinate
import fun.exercise.model.VehicleMessage.VehicleName

trait VehicleRepository {
  def storeVehicle(name: VehicleName, coordinate: Coordinate): Unit

  def getAll: Seq[(VehicleName, Coordinate)]
}
