<a href="http://predixdev.github.io/rmd-orchestration/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/rmd-orchestration" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>
##RMD Orchestration
As data arrives (or changes) an event is placed on a messaging queue. This project reads messages off those queues, decides which Analytic Orchestration to invoke, grabs the BPMN and Analytic Configuration and passes that to the Predix Orchestration engine which in turn invokes [Analytic microservices](https://github.com/predixdev/rmd-analytics).

<img src='https://github.com/predixdev/predix-rmd-ref-app/raw/master/images/RefApp-AnalyticsFlow.png' >

## Sample XML Data placed in the queue
```json
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<ns10:fieldChangedEvent xmlns="http://ge.com/predix/entity/identifier" xmlns:ns2="http://ge.com/predix/entity/solution/identifier/solutionidentifier" xmlns:ns3="http://ge.com/predix/entity/eventAsset/eventAssetidentifier" xmlns:ns4="http://ge.com/predix/entity/field/fieldidentifier" xmlns:ns5="http://ge.com/predix/entity/eventAsset" xmlns:ns6="http://ge.com/predix/entity/fieldidentifiervalue" xmlns:ns7="http://ge.com/predix/entity/assetselector" xmlns:ns8="http://ge.com/predix/entity/fieldchanged" xmlns:ns9="http://ge.com/predix/entity/util/map" xmlns:ns10="http://ge.com/predix/event/fieldchanged">
<ns8:fieldChangedList>
    <ns8:fieldChanged>
        <ns2:solutionIdentifier>
            <id xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:int">1001</id>
            <name>Predix RMD Reference Application</name>
        </ns2:solutionIdentifier>
        <ns5:assetList>
            <ns5:asset>
                <ns3:assetIdentifier>
                    <id xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">/asset/compressor-2015</id>
                    <name>/asset/compressor-2015</name>
                </ns3:assetIdentifier>
                <ns5:assetIdFieldIdentifier>
                    <id xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">/asset/assetId</id>
                    <name>/asset/assetId</name>
                    <ns4:source>PREDIX_ASSET</ns4:source>
                </ns5:assetIdFieldIdentifier>
            </ns5:asset>
        </ns5:assetList>
        <ns6:fieldIdentifierValueList>
            <ns6:fieldIdentifierValue>
                <ns4:fieldIdentifier>
                    <id xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" xsi:type="xs:string">/asset/assetTag/crank-frame-dischargepressure</id>
                    <name>/asset/assetTag/crank-frame-dischargepressure</name>
                    <ns4:source>PREDIX_ASSET</ns4:source>
                </ns4:fieldIdentifier>
            </ns6:fieldIdentifierValue>
        </ns6:fieldIdentifierValueList>
        <ns8:timeChanged>2012-09-11T07:16:13.000Z</ns8:timeChanged>
        <ns9:externalAttributeMap>
				<ns9:entry>
					<ns9:key>/asset/assetUri</ns9:key>
					<ns9:value>/asset/compressor-2015</ns9:value>
				</ns9:entry>
        </ns9:externalAttributeMap>
    </ns8:fieldChanged>
</ns8:fieldChangedList>
</ns10:fieldChangedEvent>
```

## JSON version of FieldChangedEvent for reference
```json
{
	"fieldChangedList": {
		"fieldChanged": [{
			"assetList": {
				"asset": [{
					"assetIdentifier": {
						"complexType": "AssetIdentifier",
						"id": "/asset/compressor-2015",
						"name": "/asset/compressor-2015"
					},
					"assetIdFieldIdentifier": {
						"complexType": "FieldIdentifier",
						"id": "/asset/assetId",
						"name": "/asset/assetId",
						"source": "PREDIX_ASSET"
					}
				}]
			},
			"fieldIdentifierValueList": {
				"fieldIdentifierValue": [{
					"fieldIdentifier": {
						"complexType": "FieldIdentifier",
						"id": "/asset/assetTag/crank-frame-dischargepressure",
						"name": "/asset/assetTag/crank-frame-dischargepressure",
						"source": "PREDIX_ASSET"
					}
				}]
			},
			"timeChanged": 1347347773000,
			"externalAttributeMap": {
				"entry": [{
					"key": "/asset/assetUri",
					"value": "/asset/compressor-2015"
				}]
			}
		}]
	}
}
```
##Tech Stack
- Spring
- SpringBoot
- SpringTest
- Maven
- Rabbit MQ

##Microcomponents
- [AssetBootstrap](https://github.com/predixdev/asset-bootstrap)
- [TimeseriesBootstrap](https://github.com/predixdev/timeseries-bootstrap)
- [PredixMicroServiceTemplates](https://github.com/PredixDev/predix-microservice-templates)
- [PredixRestClient](https://github.com/predixdev/predix-rest-client)

### More Details
* [More GE resources](http://github.com/predixdev/predix-rmd-ref-app/docs/resources.md)
* [RMD Reference App](http://github.com/predixdev/predix-rmd-ref-app)

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/rmd-orchestration/readme?pixel)](https://github.com/PredixDev)
