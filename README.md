# alpaca-api-performance-testing

Performance testing for Alpaca APIs.

## Prerequisites

* JDK 11
* Maven

## How to compile

Execute `mvn clean install` command from the root directory of the project/repository (i.e. from
folder `alpaca-api-performance-testing`).

## How to execute Gatling simulation

After compiling, execute `mvn gatling:test` command from the root directory of the project/repository (i.e. from
folder `alpaca-api-performance-testing`).

After execution has been complete, the location of the output HTML file will be specified by the log line with
prefix `Please open the following file: `. Example log output:
> Reports generated in 0s.  
Please open the following file: /Users/kpatel/Documents/ker/alpaca-api-performance-testing/target/gatling/alpacaapimastersimulation-20220313204703145/index.html  
[INFO] ------------------------------------------------------------------------  
[INFO] BUILD SUCCESS  
[INFO] ------------------------------------------------------------------------  
[INFO] Total time:  10.246 s  
[INFO] Finished at: 2022-03-13T13:47:06-07:00  
[INFO] ------------------------------------------------------------------------

## Note

* Alpaca API credentials are required to run the simulation successfully. 