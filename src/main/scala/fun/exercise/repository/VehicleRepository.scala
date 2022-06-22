package fun.exercise.repository

import fun.exercise.model.TopologicalMap.Vehicle

trait VehicleRepository {
  def storeVehicle(vehicle: Vehicle): Unit

  def getAll: List[Vehicle]
}
