# Trips
Trips calculator

## Run
Assuming you are executing on a POSIX box, Windows machines are very similar. Install [Java 21](https://www.oracle.com/au/java/technologies/downloads/#java21). Go to the trips project and run `./gradlew clean build` to build this project. This will download all dependencies and create the jar in `./build/libs` folder.

To run this project type `java -jar ./build/libs/trips-1.0.jar  <input-file-location>`. This will execute the program. There is an input file in the test resources folder so you could use the following command
```
java -jar ./build/libs/trips-1.0.jar ./src/test/resources/taps1.csv
```
The output is fed to the screen like a standard UNIX command.

To execute the program as required please use the `./run.sh  <input-file-location> <output-file-location>`

## Testing
The results of testing can be viewed after the running `./gradlew clean build` by viewing `./build/reports/tests/test/index.html` and `./build/reports/jacoco/test/html/index.html` the for code coverage.

This project was rather small, so I chose not to use Mockito, seeing as I got 94% code coverage without it. Mockito would help me get excellent code coverage for larger projects where we use non-in-memory versions for things like CompletedTripCosts and IncompleteTripCosts.
## Design
The major observation is that Completed Trip Costs can be represented by a 2D array and the Incomplete Trip Costs can be represented by a normal array. They are currently in-memory arrays in CompletedTripCosts.java and IncompleteTripCosts.java.

In a proper system they can be replaced by a SQL or noSQL service (if we need an immediate update), or by a S3 file and a redeploy, depending on requirements i.e. if time is not a factor. Obviously the interface to the new Completed Trip Costs and Incomplete Trips Cost, the STOP_ID_MAP map will need to be updated or replaced. 

The TapParser take the taps and subdivides them into customers. Customers being a PAN, busId and companyId combo. We then go over each of these subdivisions and create Trips out of them.

Trips are create by sorting through each subdivision by the remaining values (excluding id). The most important value being dateTimeUTC. We then need to determine whether we are using 1 or 2 Taps to calculate a trip.

We decide this by looking at the second Tap's TapType: TapType.Off means we consume 2 Taps else only 1 Tap. The Trip is calculated with the TripCalculator using the already mentioned Completed Trip Costs and Incomplete Trip Costs.

The jar file works like a standard UNIX command line utility, which can be scripted in a standard bash script, as demo'ed in the `run.sh` file.

## Assumptions
* That the input file is well-formed and is not missing data.
* OFF without a corresponding ON tap will result in an Incomplete trip and will be billed the largest amount.
* There isn't a time limit on cancelling a trip.
* There aren't time limits on the length of time spent on getting to a stop.
* That the machine this service is run on is on UTC time.
* That taps are equivalent if they have the same PAN, BusID, CompanyId.
* That the output files in the example given was wrong. I've fixed the dates for Started and Finished to be 2023 and the DurationSecs to be 300.

## Output csv
The `output.csv` can be found at `./src/test/resources/output.csv` and is the result of running the app against `./src/test/resources/taps1.csv` the required input file.