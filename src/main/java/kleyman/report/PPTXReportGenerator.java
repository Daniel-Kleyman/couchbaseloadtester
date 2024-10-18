package kleyman.report;

import kleyman.util.EnvironmentVariableUtils;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The {@code PPTXReportGenerator} class is responsible for creating a PowerPoint report
 * (.pptx) using Apache POI. It generates multiple slides that document the load testing
 * and benchmarking of Couchbase's key-value operations.
 *
 * <p>The report includes:
 * <ul>
 *    <li>Title slide</li>
 *     <li>Introduction slide</li>
 *     <li>Couchbase setup and load testing methodology</li>
 *     <li>Test scenarios and specific scenarios</li>
 *     <li>Results overview</li>
 *     <li>Thread pool results</li>
 *     <li>Connection pool results</li>
 *     <li>Findings, suggestions and conclusion</li>
 *     <li>Thank you slide</li>
 * </ul>
 */
public class PPTXReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(PPTXReportGenerator.class);
    private static int slideWidth;
    private final String filePath;
    private final TableSlideGenerator tableSlideGenerator;


    public PPTXReportGenerator() {
        this.filePath = EnvironmentVariableUtils.getEnv("COUCHBASE_REPORT_PATH");
        this.tableSlideGenerator = new TableSlideGenerator();
    }

    public void createReport() {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            initializeSlideDimensions(ppt);
            createTitleSlide(ppt);
            createIntroductionSlide(ppt);
            createCouchbaseSetupAndLoadTestingSlide(ppt);
            createTestScenariosSlide(ppt);
            createResultsOverviewSlide(ppt);
            createThreadPoolResultsSlide(ppt);
            createFindingsSuggestionsConclusionSlide(ppt);
            createThankYouSlide(ppt);
            saveReport(ppt);
            logger.info("Report created successfully at {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to create report", e);
        }
    }

    private void initializeSlideDimensions(XMLSlideShow ppt) {
        Dimension pageSize = ppt.getPageSize();
        slideWidth = (int) pageSize.getWidth();
        int slideHeight = (int) pageSize.getHeight();
        logger.debug("Slide dimensions initialized: width = {}, height = {}", slideWidth, slideHeight);
    }

    public static XSLFSlide initializeXSLFSlide(XMLSlideShow ppt) {
        return ppt.createSlide();
    }

    public static void createTextBox(XSLFSlide slide, String text, double fontSize, Color color, int anchorY, boolean horizontalCentered) {
        XSLFTextShape textBox = slide.createTextBox();
        textBox.setAnchor(new java.awt.Rectangle((slideWidth / 2) - 250, anchorY, 500, 50));
        XSLFTextRun textRun = textBox.appendText(text, true);
        textRun.setFontColor(color);
        textRun.setFontSize(fontSize);
        textBox.setHorizontalCentered(horizontalCentered);
        logger.debug("Text box created with text: {}", text);
    }

    private void createTitleSlide(XMLSlideShow ppt) {
        XSLFSlide titleSlide = initializeXSLFSlide(ppt);
        createTextBox(titleSlide, "Benchmark Report on Couchbase Load Testing", 25.0, Color.BLACK, 50, true);
        createTextBox(titleSlide, "Performance Evaluation of Key-Value Operations", 20.0, Color.DARK_GRAY, 200, true);
        createTextBox(titleSlide, "Daniel Kleyman \n   October 2024", 16.0, Color.DARK_GRAY, 350, true);
        logger.info("Title slide created");
    }

    private void createIntroductionSlide(XMLSlideShow ppt) {
        logger.info("Creating Introduction slide...");
        XSLFSlide introSlide = initializeXSLFSlide(ppt);
        // Create title
        createTextBox(introSlide, "Introduction", 30.0, Color.BLACK, 50, true);
        // Create purpose section
        createTextBox(introSlide, "Purpose of the Report:", 20.0, Color.BLACK, 110, true);
        createTextBox(introSlide, "This report provides a comprehensive overview of the benchmarking process conducted to evaluate Couchbase's key-value functionality.", 18.0, Color.DARK_GRAY, 140, false);
        // Create objective section
        createTextBox(introSlide, "Objective of Testing:", 20.0, Color.BLACK, 220, true);
        createTextBox(introSlide, "As part of the selection process for an enterprise caching solution, the objective is to develop a load testing strategy to benchmark the key-value functionality of Couchbase.", 18.0, Color.DARK_GRAY, 250, false);
        logger.info("Introduction slide creation complete.");
    }

    private void createCouchbaseSetupAndLoadTestingSlide(XMLSlideShow ppt) {
        logger.info("Creating Couchbase Setup And Load Testing slide...");
        XSLFSlide combinedSlide = initializeXSLFSlide(ppt);

// Create title
        createTextBox(combinedSlide, "Couchbase Setup and Load Testing Methodology", 20.0, Color.BLACK, -10, true);

        createTextBox(combinedSlide, "1. Couchbase Setup:", 14.0, Color.BLACK, 30, true);
        createTextBox(combinedSlide, "• Installation: The Couchbase instance was set up using Docker with default settings.", 12.0, Color.DARK_GRAY, 55, false);
        createTextBox(combinedSlide, "• Bucket Configuration: A bucket named 'loadtest' was created for testing.", 12.0, Color.DARK_GRAY, 80, false);

        createTextBox(combinedSlide, "2. Load Testing Methodology:", 14.0, Color.BLACK, 105, true);
        createTextBox(combinedSlide, "• Multi-threaded Java Application: Each thread uploads a JSON file and retrieves it.", 12.0, Color.DARK_GRAY, 130, false);
        createTextBox(combinedSlide, "• Test Duration: The load tests were executed for 3 minutes.", 12.0, Color.DARK_GRAY, 155, false);
        createTextBox(combinedSlide, "• Operations Performed: Each thread performed one upload followed by three retrieve operations of the uploaded file by key.", 12.0, Color.DARK_GRAY, 180, false);

        createTextBox(combinedSlide, "3. Asynchronous Load Testing:", 14.0, Color.BLACK, 220, true);
        createTextBox(combinedSlide, "• Purpose: Enhance efficiency and scalability of database interactions.", 12.0, Color.DARK_GRAY, 250, false);
        createTextBox(combinedSlide, "• Improved Throughput: Non-blocking I/O allows concurrent processing of multiple requests.", 12.0, Color.DARK_GRAY, 275, false);
        createTextBox(combinedSlide, "• Enhanced Resource Utilization: Optimizes CPU and memory usage by reducing idle time.", 12.0, Color.DARK_GRAY, 300, false);
        createTextBox(combinedSlide, "• Simulated Concurrent Load: Reflects real-world scenarios of multiple simultaneous users.", 12.0, Color.DARK_GRAY, 325, false);
        createTextBox(combinedSlide, "• Expected Outcomes: Higher load capacity for comprehensive performance evaluation.", 12.0, Color.DARK_GRAY, 350, false);

        createTextBox(combinedSlide, "4. Monitoring:", 14.0, Color.BLACK, 375, true);
        createTextBox(combinedSlide, "• Metrics Collection: Micrometer was used to collect application performance metrics.", 12.0, Color.DARK_GRAY, 400, false);
        createTextBox(combinedSlide, "• Metrics Storage: Prometheus was utilized for storing the collected metrics.", 12.0, Color.DARK_GRAY, 425, false);
        createTextBox(combinedSlide, "Report generated using Apache POI.", 12.0, Color.DARK_GRAY, 450, false);

        logger.info("Couchbase Setup And Load Testing slide creation complete.");
    }

    private void createTestScenariosSlide(XMLSlideShow ppt) {
        logger.info("Creating Test Scenarios slide...");
        XSLFSlide scenariosSlide = initializeXSLFSlide(ppt);
        // Create title
        createTextBox(scenariosSlide, "Test Scenarios for Couchbase Load Testing", 23.0, Color.BLACK, 10, true);
        // Create Overview section
        createTextBox(scenariosSlide, "1. Overview of Test Scenarios:", 20.0, Color.BLACK, 60, true);
        createTextBox(scenariosSlide, "• Purpose: To benchmark Couchbase's key-value functionality under various conditions.", 18.0, Color.DARK_GRAY, 90, false);
        // Create Scenario Types section
        createTextBox(scenariosSlide, "2. Scenario Types:", 20.0, Color.BLACK, 145, true);
        createTextBox(scenariosSlide, "• Thread Pool Scenarios: Tests varying the number of threads used for load operations from 1 to 3, use JSON files of 1 kb size, write to unique keys and use Couchbase default connection pool size.", 18.0, Color.DARK_GRAY, 175, false);

        logger.info("Test Scenarios slide creation complete.");
    }

    private void createResultsOverviewSlide(XMLSlideShow ppt) {
        logger.info("Starting to create Results Overview slide...");
        XSLFSlide resultsSlide = initializeXSLFSlide(ppt);

        createTextBox(resultsSlide, "Results Overview", 30.0, Color.BLACK, 0, true);
        createTextBox(resultsSlide, "This section provides an overview of the performance metrics observed during the Couchbase load testing.", 18.0, Color.DARK_GRAY, 50, false);
        createTextBox(resultsSlide, "Key Performance Metrics:", 20.0, Color.BLACK, 100, true);
        createTextBox(resultsSlide, "• Average Latency of PUT Operations: Average latency measured in milliseconds.", 18.0, Color.DARK_GRAY, 130, false);
        createTextBox(resultsSlide, "• Average Latency of GET Operations: Average latency measured in milliseconds.", 18.0, Color.DARK_GRAY, 170, false);
        createTextBox(resultsSlide, "• Overall Average Response Time: Overall average response time measured in milliseconds.", 18.0, Color.DARK_GRAY, 210, false);
        createTextBox(resultsSlide, "• Transactions Per Second (TPS): Total successful transactions processed per second.", 18.0, Color.DARK_GRAY, 250, false);
        createTextBox(resultsSlide, "• Total Error Rate: Total error rate observed during the testing phase in percentage.", 18.0, Color.DARK_GRAY, 290, false);
        createTextBox(resultsSlide, "• Total Number of Successful Operations: Total successful operations executed.", 18.0, Color.DARK_GRAY, 330, false);
        createTextBox(resultsSlide, "These metrics provide insights into Couchbase's performance under load and areas for potential optimization.", 18.0, Color.DARK_GRAY, 370, false);

        logger.info("Results Overview slide creation complete.");
    }

    private void createThreadPoolResultsSlide(XMLSlideShow ppt) {
        tableSlideGenerator.createThreadPoolResultsSlide(ppt);
    }

    private void createFindingsSuggestionsConclusionSlide(XMLSlideShow ppt) {
        logger.info("Creating Conclusion and Suggestions slide...");
        XSLFSlide findingsSlide = initializeXSLFSlide(ppt);

        createTextBox(findingsSlide, "Conclusion:", 20.0, Color.BLACK, 0, true);
        createTextBox(findingsSlide,
                "• The asynchronous load testing of Couchbase demonstrated significantly higher operation throughput compared to synchronous approaches, allowing for a much larger number of operations to be performed simultaneously.",
                16.0, Color.DARK_GRAY, 45, false);
        createTextBox(findingsSlide,
                "• Scenario 2, with 2 threads, achieved the highest number of successful operations and the lowest error rate, highlighting the benefits of asynchronous testing in handling heavy loads more efficiently.",
                16.0, Color.DARK_GRAY, 120, false);
        createTextBox(findingsSlide,
                "• Scenario 3, with 3 threads, showed diminishing returns with a noticeable increase in the error rate (31.03%), despite a slight improvement in throughput.",
                16.0, Color.DARK_GRAY, 175, false);
        createTextBox(findingsSlide,
                "• These findings suggest that while asynchronous testing can handle more concurrent operations, it may result in higher error rates under heavy load.",
                16.0, Color.DARK_GRAY, 230, false);
        createTextBox(findingsSlide,
                "• Due to limited hardware resources, we could not obtain results sufficient for a comprehensive analysis.",
                16.0, Color.DARK_GRAY, 285, false);

        createTextBox(findingsSlide, "Suggestions for Further Testing:", 20.0, Color.BLACK, 350, true);
        createTextBox(findingsSlide,
                "• CPU Utilization, Memory Usage, Disk I/O Performance, Network Latency and Throughput, Couchbase Performance Metrics.",
                16.0, Color.DARK_GRAY, 390, false);

        logger.info("Conclusion and Suggestions slide creation complete.");
    }

    private void createThankYouSlide(XMLSlideShow ppt) {
        logger.info("Creating Thank You slide...");
        XSLFSlide findingsSlide = initializeXSLFSlide(ppt);
        createTextBox(findingsSlide, "THANK YOU FOR YOUR ATTENTION!", 28.0, Color.BLACK, 200, true);
        logger.info("Thank You slide creation complete.");
    }

    private void saveReport(XMLSlideShow ppt) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            ppt.write(out);
        } catch (IOException e) {
            logger.error("Failed to save report", e);
        }
    }
}
