<a href="http://predixdev.github.io/rmd-orchestration/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/rmd-orchestration" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>
##RMD Orchestration- How this works
- Orchestration, by design, will be listening on a specific queue for a specific TYPE of event. Queue name and the connection to the queue will be established at the deployment time.
- Once the event is read from the queue, listener with the help of routing rules decide which orchestration to execute based on the event's message content. 
- Predix Orchestration Runtime executes the orchestration, which in turn, can invoke one or more analytics based on the content of message.

<img src='https://github.com/predixdev/predix-rmd-ref-app/raw/master/images/RefApp-AnalyticsFlow.png' >

## Sample FieldChangedEvent json
```json
{
        "fieldChangedList": {
                "fieldChanged": [{
                        "assetList": {
                                "asset": [{
                                        "uri": "/asset/compressor-2017",
                                        "assetType": "asset",
                                        "fieldList": {
                                                "field": [{
                                                        "fieldKey": "/asset/assetTag/crank-frame-dischargepressure",
                                                        "fieldValue": "",
                                                        "fieldType": "assetTag",
                                                        "timeChanged": "2012-09-11T07:16:13.000Z"
                                                }]
                                        }
                                }]
                        },
                        "externalAttributeMap": {
                                "entry": [{
                                        "key": "",
                                        "value": ""
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
