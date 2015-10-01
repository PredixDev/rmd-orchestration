##RMD Orchestration

As data arrives (or changes) an event is placed on a messaging topic.   This project reads messages off those queues.
=======
(future)RMD Orchestration

As data arrives (or changes) an event is placed on a messaging topic. This project reads messages off those queues, decides which Analytic Orchestration to invoke, grabs the BPMN and Analytic Configuration and passes that to the Predix Orchestration engine which in turn invokes [Analytic microservices](https://github.com/predixdev/rmd-analytics).

<img src='https://github.com/predixdev/predix-rmd-ref-app/blob/master/images/RefApp-AnalyticsFlow.png' >


##Tech Stack
- Spring
- SpringBoot
- SpringTest
- Maven

##Microcomponents
- [AssetBootstrap](https://github.com/predixdev/asset-bootstrap)
- [TimeseriesBootstrap](https://github.com/predixdev/timeseries-bootstrap)
- [PredixBoot](https://github.com/predixdev/predix-boot)
- [PredixRestClient](https://github.com/predixdev/predix-boot)

### More Details
* [More GE resources](http://github.com/predixdev/predix-rmd-ref-app/docs/resources.md)
* [RMD Reference App](http://github.com/predixdev/predix-rmd-ref-app)
