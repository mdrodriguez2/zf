# ZF Scala assignment

Our Fleet Orchestration Platform is all about optimizing mobility. From public transit to autonomous robot-taxi, our platform is able to manage in real-time any kind of transportation.

In this exercise, we provided a primitive model defined in `model/package.scala`, which includes the domain model you will use.
You will also find a simple service defined in `service/VehiclesService.scala` which generates random vehicle movements for a given `NetworkMap` and publishes them to a consumer actor.
In `service/NetworkParser.scala` you have a parser for the GeoJSON file that you can use to create a NetworkMap from attached GeoJSON file (`src/main/resources/lc-track0-21781.geojson`). Alternatively, in `WebServer.scala` there is a hardcoded `squareNetwork` that you might want to use for debugging purposes.

*The main point of this assignment is to give you a way to present your abilities to produce production-ready software and your techniques to achieve this.*

## Exercise goal:

1. Create a REST endpoint that expose a `TopologicalMap` generated out of the provided GeoJSON file and the live position of the vehicles.
2. Create an endpoint that returns the ETA for a requested vehicle for all stations
3. Create a WebSocket that allow retrieving the real-time *location* (which is not its absolute position!) of vehicles, which consist of the id of the segment on which the vehicle is, and the relative position of this vehicle on his segment

## Some small rules
- You should use git and record the history of your changes.
- You should not have to change the code from `VehiclesService`, you can experiment if needed but the data format shouldn't change.
- You can use any external library you want

## Very important, please read this carefully
We know you have a busy life and we don't want to make it harder. 
However we need to see what you can do with your keyboard when you are producing (almost) production-ready code. 
That's why we would ideally like you to try to complete points 1, 2 and maybe even 3 above. However, if it turns out that it would take you more than ~4 hours, do not worry and send us what you already have. We can always discuss the unfinished stuff later on.

Feel free to provide any relevant feedback about this assignment and how we could improve it :-)

Enjoy!

Cheers,

The ZF Scala engineers
