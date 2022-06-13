package fun.exercise.repository

import com.vividsolutions.jts.geom.Coordinate
import fun.exercise.model.VehicleMessage.VehicleName

import scala.collection.mutable

class InMemoryVehicleRepository extends VehicleRepository {

  val vehiclePosition = new mutable.HashMap[String, Coordinate]()


  override def storeVehicle(name: VehicleName, coordinate: Coordinate): Unit = vehiclePosition.put(name, coordinate)

  //TODO is this the best return type?
  override def getAll: Seq[(VehicleName, Coordinate)] = vehiclePosition.iterator.toSeq
}
