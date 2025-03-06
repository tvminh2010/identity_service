package zve.com.vn.printerservice;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.JasperCompileManager;

@Service
public class CompileRasper {
	
	private static final String JRXML_PATH = "/reports/lg_label/lg_label.jrxml";
    private static final String JASPER_OUTPUT = "target/classes/reports/lg_label.jasper";
	/* ---------------------------------------------------------------- */
	 @PostConstruct
	    public void compileReport() {
		 	
	        try {
	            InputStream jrxmlStream = getClass().getResourceAsStream(JRXML_PATH);
	            if (jrxmlStream == null) {
	                throw new RuntimeException("❌ Không tìm thấy file .jrxml!");
	            }

	            Path tempJrxml = Files.createTempFile("temp_report", ".jrxml");
	            Files.copy(jrxmlStream, tempJrxml, StandardCopyOption.REPLACE_EXISTING);
	           
	            File jasperFile = new File(JASPER_OUTPUT);
	            jasperFile.getParentFile().mkdirs();

	            JasperCompileManager.compileReportToFile(tempJrxml.toString(), JASPER_OUTPUT);

	            System.out.println("✅ Successfully compiled the file: " + JASPER_OUTPUT);
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.err.println("❌ Error compile the file: " + e.getMessage());
	        }
	    }
	/* ---------------------------------------------------------------- */
}
