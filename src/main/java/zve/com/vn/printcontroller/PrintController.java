package zve.com.vn.printcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import zve.com.vn.printerservice.JasperPrintService;

@RestController
@RequestMapping("/print")
public class PrintController {
	
	 private final JasperPrintService jasperPrintService;
	 
	 /* --------------------------------------------------------------------- */
	 public PrintController (JasperPrintService jasperPrintService) {

	    this.jasperPrintService = jasperPrintService;
	 }
	 /* --------------------------------------------------------------------- */
     @PostMapping("/jasper")
     public ResponseEntity<String> printJasper() {
    	 String result = jasperPrintService.printJasperReport();
    	 return ResponseEntity.ok(result);
     }
	 /* --------------------------------------------------------------------- */
}








