# Business Rates Service

A REST service for retrieving the ratable values for business properties

## Useage

### Search by postcode

GET [http://devbeta.digital.gov.uk/address/?search={postcode}]

Replace {postcode} with a full or partial Scottish postcode.

#### Response

```json
{
    "message": "Found",
    "properties": [
        {
            "rv": "3900000",
            "address": "(A)\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ",
            "occupier": [
                {
                    "name": "SCOTTISH GOVERNMENT\nPROPERTY ADVICE DIVISION\nAREA 3G - NORTH\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ"
                }
            ]
        },
        {
            "rv": "5950",
            "address": "(A1)\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ",
            "occupier": [
                {
                    "name": "BANK OF SCOTLAND PER HBOS GROUP PROPERTY (RATING)\nHBOS PLC\nTRINITY ROAD\nHALIFAX\nWEST YORKSHIRE\nHX1 2RG"
                }
            ]
        }
    ]
}
```

### Search by postcode

GET [http://devbeta.digital.gov.uk/address/?search={address}]

Replace {address} with a full or partial Scottish business address.  The address can be the "address" property retrieved from the postcode search.

#### Response

```json
{
    "message": "Found",
    "properties": [
        {
            "rv": "3900000",
            "address": "(A)\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ",
            "occupier": [
                {
                    "name": "SCOTTISH GOVERNMENT\nPROPERTY ADVICE DIVISION\nAREA 3G - NORTH\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ"
                }
            ]
        }
    ]
}
```

## Local Installation

Clone the code from 


ssh://git@stash.digital.gov.uk:7999/mgv/mygov-org.mygovscot-business-rates.git


The code can be run using 

mvn spring-boot:run

or

mvn clean install
java -jar target/org.mygovscot-business-rates*.jar

To create a Debian apt installation file build with the debian profile

mvn clean install -Pdebian

