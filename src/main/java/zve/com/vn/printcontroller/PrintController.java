package zve.com.vn.printcontroller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import zve.com.vn.printerservice.CustomPrintService;
import zve.com.vn.printerservice.ExcelPrintService;

@RestController
@RequestMapping("/print")
public class PrintController {
	
	 private final CustomPrintService customPrintService;
	 private final ExcelPrintService  excelPrintService;
	 
	 /* --------------------------------------------------------------------- */
	 public PrintController (ExcelPrintService excelPrintService, CustomPrintService customPrintService) {
	    this.excelPrintService = excelPrintService;
	    this.customPrintService = customPrintService;
	 }
	 
	 /* --------------------------------------------------------------------- */
	 @GetMapping
	 public String print() {
		 customPrintService.print();
		 return "In thành công!";
	 }
	 /* --------------------------------------------------------------------- */
     @PostMapping("/excel")
     public ResponseEntity<String> printExcel() {
    	 String result = excelPrintService.printExcel();
    	 return ResponseEntity.ok(result);
     }
	 /* --------------------------------------------------------------------- */
}








