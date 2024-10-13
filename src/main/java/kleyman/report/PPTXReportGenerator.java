package kleyman.report;

import kleyman.util.EnvironmentVariableUtils;
import org.apache.poi.sl.usermodel.TableCell;
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
 *     <li>Title slide</li>
 *     <li>Introduction slide</li>
 *     <li>Couchbase setup and load testing methodology</li>
 *     <li>Test scenarios and specific scenarios</li>
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
            createSpecificScenariosSlide(ppt);
            createResultsOverviewSlide(ppt);
            createThreadPoolResultsSlide(ppt);
            createConnectionPoolResultsSlide(ppt);
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
        createTextBox(combinedSlide, "Couchbase Setup and Load Testing Methodology", 23.0, Color.BLACK, 10, true);
        // Create Couchbase Setup section
        createTextBox(combinedSlide, "1. Couchbase Setup:", 20.0, Color.BLACK, 60, true); // 100 -> 60
        createTextBox(combinedSlide, "• Installation: The Couchbase instance was set up using Docker with default settings.", 18.0, Color.DARK_GRAY, 90, false); // 130 -> 90
        createTextBox(combinedSlide, "• Bucket Configuration: A bucket named 'loadtest' was created for testing.", 18.0, Color.DARK_GRAY, 135, false); // 175 -> 135
        // Create Load Testing Methodology section
        createTextBox(combinedSlide, "2. Load Testing Methodology:", 20.0, Color.BLACK, 190, true); // 230 -> 190
        createTextBox(combinedSlide, "• Multi-threaded Java Application: Each thread uploads a JSON file and retrieves it.", 18.0, Color.DARK_GRAY, 220, false); // 260 -> 220
        createTextBox(combinedSlide, "• Test Duration: The load tests were executed for 3 minutes.", 18.0, Color.DARK_GRAY, 265, false); // 305 -> 265
        createTextBox(combinedSlide, "• Operations Performed: Each thread performed one upload followed by three retrieve operations of the uploaded file by key.", 18.0, Color.DARK_GRAY, 290, false); // 330 -> 290
        // Add monitoring details
        createTextBox(combinedSlide, "3. Monitoring:", 20.0, Color.BLACK, 345, true); // 385 -> 345
        createTextBox(combinedSlide, "• Metrics Collection: Micrometer was used to collect application performance metrics.", 18.0, Color.DARK_GRAY, 370, false); // 410 -> 370
        createTextBox(combinedSlide, "• Metrics Storage: Prometheus was utilized for storing the collected metrics.", 18.0, Color.DARK_GRAY, 420, false); // 435 -> 395
        createTextBox(combinedSlide, "Report generated using Apache POI.", 18.0, Color.DARK_GRAY, 470, false);
        logger.info("Couchbase Setup And Load Testing slide creation complete.");
    }

    private void createTestScenariosSlide(XMLSlideShow ppt) {
        logger.info("Creating Test Scenarios slide...");
        XSLFSlide scenariosSlide = initializeXSLFSlide(ppt);
        // Create title with adjusted font and anchor
        createTextBox(scenariosSlide, "Test Scenarios for Couchbase Load Testing", 23.0, Color.BLACK, 10, true);
        // Create Overview section with adjusted font and anchor
        createTextBox(scenariosSlide, "1. Overview of Test Scenarios:", 20.0, Color.BLACK, 60, true); // Align with previous slide's section header size and anchor
        createTextBox(scenariosSlide, "• Purpose: To benchmark Couchbase's key-value functionality under various conditions.", 18.0, Color.DARK_GRAY, 90, false);
        // Create Scenario Types section with adjusted font and anchor
        createTextBox(scenariosSlide, "2. Scenario Types:", 20.0, Color.BLACK, 135, true); // Adjusted to match section headers in other slides
        createTextBox(scenariosSlide, "• Thread Pool Scenarios: Tests varying the number of threads used for load operations from 5 to 15, use 2 JSON files of different sizes (1 kb and 25 kb), write to unique or shared keys and use Couchbase default connection pool size.", 18.0, Color.DARK_GRAY, 165, false);
        createTextBox(scenariosSlide, "• Connection Pool Scenarios: Tests evaluating performance with a fixed number of threads (10), use JSON files of same size (25 kb), unique keys, connection pool size vary from 5 to 15.", 18.0, Color.DARK_GRAY, 265, false);
        logger.info("Test Scenarios slide creation complete.");
    }

    private void createSpecificScenariosSlide(XMLSlideShow ppt) {
        logger.info("Creating Specific Scenarios slide...");
        // Create a new slide for specific scenarios
        XSLFSlide scenariosSlide = initializeXSLFSlide(ppt);
        // Create title for the scenarios
        createTextBox(scenariosSlide, "Specific Scenarios", 28.0, Color.BLACK, 0, true); // Title size 28
        // Add each scenario with adjusted anchors
        createTextBox(scenariosSlide, "  Scenario 1: 5 threads, 25 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 60, false);
        createTextBox(scenariosSlide, "  Scenario 2: 5 threads, 25 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 90, false);
        createTextBox(scenariosSlide, "  Scenario 3: 10 threads, 25 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 120, false);
        createTextBox(scenariosSlide, "  Scenario 4: 10 threads, 25 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 150, false);
        createTextBox(scenariosSlide, "  Scenario 5: 15 threads, 25 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 180, false);
        createTextBox(scenariosSlide, "  Scenario 6: 15 threads, 25 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 210, false);
        createTextBox(scenariosSlide, "  Scenario 7: 5 threads, 1 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 240, false);
        createTextBox(scenariosSlide, "  Scenario 8: 5 threads, 1 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 270, false);
        createTextBox(scenariosSlide, "  Scenario 9: 10 threads, 1 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 300, false);
        createTextBox(scenariosSlide, "  Scenario 10: 10 threads, 1 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 330, false);
        createTextBox(scenariosSlide, "  Scenario 11: 15 threads, 1 kb JSON, unique keys.", 14.0, Color.DARK_GRAY, 360, false);
        createTextBox(scenariosSlide, "  Scenario 12: 15 threads, 1 kb JSON, shared key.", 14.0, Color.DARK_GRAY, 390, false);
        createTextBox(scenariosSlide, "  Scenario 13: 10 threads, 25 kb JSON, unique key, connection pool size 5.", 14.0, Color.DARK_GRAY, 420, false);
        createTextBox(scenariosSlide, "  Scenario 14: 10 threads, 25 kb JSON, unique key, connection pool size 10.", 14.0, Color.DARK_GRAY, 450, false);
        createTextBox(scenariosSlide, "  Scenario 15: 15 threads, 25 kb JSON, unique key, connection pool size 15.", 14.0, Color.DARK_GRAY, 480, false);
        logger.info("Specific Scenarios slide creation complete.");
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
        createTextBox(resultsSlide, "• Maximum Latency of PUT Operations: Maximum latency recorded in milliseconds.", 18.0, Color.DARK_GRAY, 330, false);
        createTextBox(resultsSlide, "• Maximum Latency of GET Operations: Maximum latency recorded in milliseconds.", 18.0, Color.DARK_GRAY, 370, false);
        createTextBox(resultsSlide, "• Total Number of Successful Operations: Total successful operations executed.", 18.0, Color.DARK_GRAY, 410, false);
        createTextBox(resultsSlide, "These metrics provide insights into Couchbase's performance under load and areas for potential optimization.", 18.0, Color.DARK_GRAY, 450, false);

        logger.info("Results Overview slide creation complete.");
    }

    private void createThreadPoolResultsSlide(XMLSlideShow ppt) {
        tableSlideGenerator.createThreadPoolResultsSlide(ppt);
    }

    private void createConnectionPoolResultsSlide(XMLSlideShow ppt) {
        tableSlideGenerator.createConnectionPoolResultsSlide(ppt);
    }

    private void saveReport(XMLSlideShow ppt) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            ppt.write(out);
        } catch (IOException e) {
            logger.error("Failed to save report", e);
        }
    }
}
