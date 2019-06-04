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
            ],
            "localAuthority": {
                "id": "S100000",
                "name": "Edinburgh Village",
                "code": 300,
                "links": {
                    "id": null,
                    "homepage": "http://www.edinburgh.gov.uk/",
                    "tax": "http://www.edinburgh.gov.uk/info/20020/business_rates/757/non-domestic_business_rates_charges"
                }
            }
        },
        {
            "rv": "5950",
            "address": "(A1)\nVICTORIA QUAY\nEDINBURGH\nEH6 6QQ",
            "occupier": [
                {
                    "name": "BANK OF SCOTLAND PER HBOS GROUP PROPERTY (RATING)\nHBOS PLC\nTRINITY ROAD\nHALIFAX\nWEST YORKSHIRE\nHX1 2RG"
                }
            ],
            "localAuthority": {
                "id": "S100000",
                "name": "Edinburgh Village",
                "code": 300,
                "links": {
                    "id": null,
                    "homepage": "http://www.edinburgh.gov.uk/",
                    "tax": "http://www.edinburgh.gov.uk/info/20020/business_rates/757/non-domestic_business_rates_charges"
                }
            }
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
            ],
            "localAuthority": {
                "id": "S100000",
                "name": "Edinburgh Village",
                "code": 300,
                "links": {
                    "id": null,
                    "homepage": "http://www.edinburgh.gov.uk/",
                    "tax": "http://www.edinburgh.gov.uk/info/20020/business_rates/757/non-domestic_business_rates_charges"
                }
            }
        }
    ]
}
```

