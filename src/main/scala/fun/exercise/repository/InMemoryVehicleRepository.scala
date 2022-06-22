package fun.exercise.repository

import fun.exercise.model.TopologicalMap.Vehicle

import scala.collection.mutable

class InMemoryVehicleRepository extends VehicleRepository {

  val vehiclePosition = new mutable.HashMap[String, Vehicle]()

  override def storeVehicle(vehicle: Vehicle): Unit = vehiclePosition.put(vehicle.name, vehicle)

  override def getAll: List[Vehicle] = vehiclePosition.values.toList
}
