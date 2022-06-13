package fun.exercise.service

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import fun.exercise.model.VehicleMessage
import fun.exercise.repository.VehicleRepository

//TODO esto como class no me mola mucho
class VehicleTracker(storage: VehicleRepository) {

  var consumer: ActorRef[VehicleMessage] = _

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
    println(message.name, message.coordinate)
    storage.storeVehicle(message.name, message.coordinate)
  }


}
