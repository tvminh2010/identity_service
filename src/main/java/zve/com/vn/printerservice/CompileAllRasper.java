package zve.com.vn.printerservice;

import java.io.File;
import java.util.Objects;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperCompileManager;

@Service
public class CompileAllRasper {
    
    private static final String JRXML_DIR = "/reports/";
    private static final String OUTPUT_DIR = "target/classes/reports/";

    /* ---------------------------------------------------------------- */
    @PostConstruct
    public void compileAllReports() {
        try {
            File reportsFolder = new File(Objects.requireNonNull(getClass().getResource(JRXML_DIR)).toURI());

            if (!reportsFolder.exists() || !reportsFolder.isDirectory()) {
                throw new RuntimeException("❌ Report folder could not be found!");
            }

            File[] jrxmlFiles = reportsFolder.listFiles((dir, name) -> name.endsWith(".jrxml"));

            if (jrxmlFiles == null || jrxmlFiles.length == 0) {
                System.out.println("⚠️ Could not find .jrxml in folder: " + JRXML_DIR);
                return;
            }

            for (File jrxmlFile : jrxmlFiles) {
                compileReport(jrxmlFile);
            }

            System.out.println("✅ All .jrxml file has been compiled!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error compiling files: " + e.getMessage());
        }
    }
    
    /* ---------------------------------------------------------------- */
    private void compileReport(File jrxmlFile) {
        try {
            String outputFilePath = OUTPUT_DIR + jrxmlFile.getName().replace(".jrxml", ".jasper");
            File jasperFile = new File(outputFilePath);
            jasperFile.getParentFile().mkdirs();

            JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), outputFilePath);
            System.out.println("✅ Successfully compiling the file: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("❌ Error compiling the file: " + jrxmlFile.getName() + " - " + e.getMessage());
        }
    }
    /* ---------------------------------------------------------------- */
}
