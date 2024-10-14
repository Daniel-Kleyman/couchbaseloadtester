package kleyman.report;

import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

/**
 * Generates slides for a PowerPoint presentation containing table data.
 * This class handles the creation of slides that display the results of
 * thread pool and connection pool tests in a structured tabular format.
 */
public class TableSlideGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TableSlideGenerator.class);
    private final MetricsTableDataGenerator metricsTableGenerator;

    public TableSlideGenerator() {
        this.metricsTableGenerator = new MetricsTableDataGenerator();
    }

    public void createThreadPoolResultsSlide(XMLSlideShow ppt) {
        logger.info("Creating Thread Pool Tests Results slide...");
        XSLFSlide threadPoolResultsSlide = PPTXReportGenerator.initializeXSLFSlide(ppt);
        PPTXReportGenerator.createTextBox(threadPoolResultsSlide, "Thread Pool Tests Results", 24.0, Color.BLACK, 0, true);
        String[][] threadPoolTableData = metricsTableGenerator.generateThreadPoolMetricsTableData();
        addTableToSlide(threadPoolResultsSlide, threadPoolTableData, 60);
        logger.info("Thread Pool Tests Results slide creation complete.");
    }

    public void createConnectionPoolResultsSlide(XMLSlideShow ppt) {
        logger.info("Creating Connection Pool Tests Results slide...");
        XSLFSlide connectionPoolResultsSlide = PPTXReportGenerator.initializeXSLFSlide(ppt);
        PPTXReportGenerator.createTextBox(connectionPoolResultsSlide, "Connection Pool Tests Results", 30.0, Color.BLACK, 0, true);
        String[][] connectionPoolTableData = metricsTableGenerator.generateConnectionPoolMetricsTableData();
        addTableToSlide(connectionPoolResultsSlide, connectionPoolTableData, 100);
        logger.info("Connection Pool Tests Results slide creation complete.");
    }

    private void addTableToSlide(XSLFSlide slide, String[][] tableData, int y) {
        XSLFTable table = createTable(slide, y);
        populateTableData(table, tableData);
        setColumnWidths(table, tableData[0].length);
    }

    private XSLFTable createTable(XSLFSlide slide, int y) {
        XSLFTable table = slide.createTable();
        table.setAnchor(new Rectangle(110, y, 400, 300));
        return table;
    }

    private void populateTableData(XSLFTable table, String[][] tableData) {
        for (int row = 0; row < tableData.length; row++) {
            XSLFTableRow tableRow = table.addRow();
            for (int col = 0; col < tableData[row].length; col++) {
                XSLFTableCell cell = createCell(tableRow, tableData[row][col], row == 0);
                applyCellStyles(cell);
            }
        }
    }

    /**
     * Creates a cell in the specified row with the given value and styles it
     * as a header if specified.
     *
     * @param tableRow  the row to which the cell will be added
     * @param cellValue the text to be set in the cell
     * @param isHeader  true if the cell is a header, false otherwise
     * @return the created XSLFTableCell object
     */
    private XSLFTableCell createCell(XSLFTableRow tableRow, String cellValue, boolean isHeader) {
        XSLFTableCell cell = tableRow.addCell();
        cell.setText(cellValue);
        cell.setFillColor(isHeader ? Color.LIGHT_GRAY : Color.WHITE);
        return cell;
    }

    private void applyCellStyles(XSLFTableCell cell) {
        setCellBorders(cell);
        setCellFont(cell);
        setCellFontColor(cell);
    }

    private void setCellBorders(XSLFTableCell cell) {
        cell.setBorderColor(TableCell.BorderEdge.top, Color.BLACK);
        cell.setBorderColor(TableCell.BorderEdge.bottom, Color.BLACK);
        cell.setBorderColor(TableCell.BorderEdge.left, Color.BLACK);
        cell.setBorderColor(TableCell.BorderEdge.right, Color.BLACK);
    }

    /**
     * Sets the font size for the text in the specified cell.
     *
     * @param cell the cell for which to set the font size
     */
    private void setCellFont(XSLFTableCell cell) {
        for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            for (XSLFTextRun textRun : paragraph.getTextRuns()) {
                textRun.setFontSize(8.0);
            }
        }
    }

    /**
     * Sets the font color for the text in the specified cell.
     *
     * @param cell the cell for which to set the font color
     */
    private void setCellFontColor(XSLFTableCell cell) {
        for (XSLFTextParagraph paragraph : cell.getTextParagraphs()) {
            for (XSLFTextRun textRun : paragraph.getTextRuns()) {
                textRun.setFontColor(Color.BLACK);
            }
        }
    }

    /**
     * Sets the width for each column in the specified table.
     *
     * @param table   the table for which to set column widths
     * @param numCols the number of columns in the table
     */
    private void setColumnWidths(XSLFTable table, int numCols) {
        for (int col = 0; col < numCols; col++) {
            table.setColumnWidth(col, 70);
        }
    }
}
