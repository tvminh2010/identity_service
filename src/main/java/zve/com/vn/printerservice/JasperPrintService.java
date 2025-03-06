package zve.com.vn.printerservice;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;

@Service
public class JasperPrintService {

    @Autowired
    private DataSource dataSource;
    @Value("${printer.name}")
    private String printerName;

    private static final String JASPER_FILE_PATH = "reports/lg_label.jasper";

    /* ---------------------------------------------------------------- */
    public String printJasperReport() {
        try { // ✅ Tự động đóng connection
            
        	Connection connection = dataSource.getConnection();
        	InputStream jasperStream = getClass().getClassLoader().getResourceAsStream(JASPER_FILE_PATH);
            if (jasperStream == null) {
                return "❌ Không tìm thấy file .jasper trong classpath!";
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, getReportParameters(), connection);
            PrintService selectedPrinter = findPrintService(printerName);
            if (selectedPrinter == null) {
                return "❌ Không tìm thấy máy in: " + printerName;
            }

            exportToPrinter(jasperPrint, selectedPrinter);
            return "✅ Successfully printing on: " + selectedPrinter.getName();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error when printing: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }

    /* ---------------------------------------------------------------- */
    private PrintService findPrintService(String printerName) {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }
        return null;
    }
    /* ---------------------------------------------------------------- */
    private void exportToPrinter(JasperPrint jasperPrint, PrintService selectedPrinter) throws Exception {
        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
       // configuration.setPrintRequestAttributeSet(configurePrintAttributes());
        configuration.setPrintService(selectedPrinter);
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
    }
    /* ---------------------------------------------------------------- */
    /*
    private PrintRequestAttributeSet configurePrintAttributes() {
        PrintRequestAttributeSet printAttributes = new HashPrintRequestAttributeSet();
        printAttributes.add(new Copies(1));
        printAttributes.add(MediaSizeName.ISO_A4);
        printAttributes.add(PrintQuality.HIGH);
        printAttributes.add(Sides.ONE_SIDED);
        return printAttributes;
    }
	*/
    /* ---------------------------------------------------------------- */
    private Map<String, Object> getReportParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("sub_title", "Báo cáo từ Spring Boot");
        return parameters;
    }

    /* ---------------------------------------------------------------- */
}
