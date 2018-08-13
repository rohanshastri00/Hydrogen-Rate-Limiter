# Hydrogen-Rate-Limiter
Tool that adjusts the ingestion rate of new product information into Nordstrom's Solr implementation via metrics-based system analysis.

Files within this repo do not comprise the entire Hydrogen application - only the files created/modified by myself are included, for viewing purposes. The classes I've built are as follows:

RateLimiterHelper - checks to see if Solr is active, and if so starts the rate limiter process.

RateLimiterConfigHelper - triggers MetricProcessor to begin reading from config file and gather metric(s), later requests rate from RateCalculator.

RateCalculator - responsible for returning a predetermined double value based on the three RiskLevel enum values (GOOD, MEDIUM, BAD).

MetricsProcessor - reads the metric.yml config file, requests for the appropriate MetricService and MetricModel to be used and returned, resulting in a RiskLevel enum for each metric specified in the config.

metric.yml - the config file responsible for including information on desired metrics for adjusting ingestion of product data into Solr. Must include query name for rest API implementation, information on thresholds, and respective metric provider.

MetricServiceFactory - Design tool used to select the appropriate metric agents/providers (Datadog, Solr, Influx, etc.) based on the provider name(s) in metric.yml

MetricService - Interface that is implemented by all metric provider service classes (DataDogService, etc.). All service classes will pull various JSONs, parse and calculate values, and create a new MetricModel object.

DatadogService - Service class which pulls desired metric(s) from Datadog, using REST API integration.

MetricModel - The class used to populate any metric pulled from a metric provider. Must include name of the metric, as well as it's calculated value as it corresponds to the threshold. 

RiskLevel - The enum values associated with the 3-tier rate limitation system, responsible for signifying the overall system health of Solr.

![alt text](https://i.imgur.com/BRl90mm.png)
