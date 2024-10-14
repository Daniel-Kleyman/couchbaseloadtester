# Couchbase Load Tester 
## Description 
Couchbase Load Tester is a performance testing tool designed to evaluate the scalability and responsiveness of Couchbase databases under varying load conditions. This application simulates multiple users interacting with the database, allowing developers and testers to analyze performance metrics and identify potential bottlenecks. 
## Key Features 
- **Customizable Load Scenarios**: Define various load test scenarios to simulate different usage patterns. 
- **Comprehensive Metrics Collection**: Collect performance metrics during tests, including latency, throughput, and error rates. 
- **Reporting**: Generate detailed reports in PPTX format for easy sharing and analysis.
 - **Prometheus Integration**: Monitor metrics in real-time using Prometheus.
## Installation Instructions 
To install Couchbase Load Tester, follow these steps:
1.	**Clone the repository:** 
```bash 
git clone https:// https://github.com/Daniel-Kleyman/couchbaseloadtester
cd couchbaseloadtester
```
2.	**Install dependencies:**
Ensure you have Maven installed. Run the following command to install the required dependencies:
```bash 
mvn install
```
3.	**Set up Couchbase server:**
Make sure you have a Couchbase server running. You will need to create a bucket for testing.
## Configuration 
### Configure environment variables
Couchbase Load Tester is designed to be easily configurable through environment variables. The application retrieves connection settings for Couchbase from the environment, ensuring that sensitive data is not hardcoded in your code.
The following environment variables should be set:
-	COUCHBASE_HOST: The IP address or domain name of your Couchbase server.
-	COUCHBASE_USERNAME: The username for accessing the Couchbase server.
-	COUCHBASE_PASSWORD: The password for accessing the Couchbase server.
-	COUCHBASE_BUCKET_NAME: The name of the bucket you wish to use for load testing.
-	JSON_BIG_PATH: Path to the JSON file used for large load test.
-	JSON_SMALL_PATH: Path to the JSON file used for small load tests.
-	COUCHBASE_REPORT_PATH: Path to the report file.
## Reporting
The application generates detailed reports in PPTX format, summarizing the performance metrics collected during the tests. These reports can be easily shared with stakeholders for further analysis.




