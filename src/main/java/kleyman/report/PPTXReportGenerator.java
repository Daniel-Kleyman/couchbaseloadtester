package kleyman.report;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;

public class PPTXReportGenerator {

    private String filePath;

    public PPTXReportGenerator(String filePath) {
        this.filePath = filePath;
    }

    public void createReport() {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            XSLFSlide titleSlide = ppt.createSlide();
            XSLFTextShape title = titleSlide.createTextBox();
            title.setAnchor(new java.awt.Rectangle(50, 50, 500, 100));

            XSLFTextRun titleRun = title.addTextRun();
            titleRun.setText("Couchbase Benchmark Report");
            titleRun.setFontColor(Color.BLACK);
            titleRun.setFontSize(24.0);

            addMetricsSlide(ppt, "Read Operations", "1000");
            addMetricsSlide(ppt, "Write Operations", "500");

            saveReport(ppt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addMetricsSlide(XMLSlideShow ppt, String metricName, String metricValue) {
        XSLFSlide metricsSlide = ppt.createSlide();
        XSLFTextShape metricText = metricsSlide.createTextBox();
        metricText.setAnchor(new java.awt.Rectangle(50, 50, 500, 50));

        XSLFTextRun metricRun = metricText.addTextRun();
        metricRun.setText(metricName + ": " + metricValue);
        metricRun.setFontColor(Color.BLACK);
        metricRun.setFontSize(20.0);

        metricText.setFillColor(Color.WHITE);
        metricText.setLineColor(Color.BLACK);
    }

    private void saveReport(XMLSlideShow ppt) {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            ppt.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
