# Business Rates Service

A REST service for retrieving the ratable values for business properties

## Installation

Clone the code from 


ssh://git@stash.digital.gov.uk:7999/mgv/mygov-org.mygovscot-business-rates.git


The code can be run using 

mvn spring-boot:run

or

mvn clean install
java -jar target/org.mygovscot-business-rates*.jar

## Useage

### Search for postcode

GET from / (root) with parameter search e.g. http://localhost:9090/?search=EH66QQ

