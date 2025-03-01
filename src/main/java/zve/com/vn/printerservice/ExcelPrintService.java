package zve.com.vn.printerservice;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ExcelPrintService implements Printable {

    private static final String MS11 = "excel-templates/MS11/V0002_Pallet_label.xls";
    private static final String OUTPUT_PATH = "excel-output-templates/filled_template.xlsx";

    /* --------------------------------------------------------------------- */
    public String fillExcelTemplate(String name, int age, String address) throws IOException {
    	InputStream inputStream = new ClassPathResource(MS11).getInputStream();
    	Workbook workbook = new XSSFWorkbook(inputStream);
    	
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(1);
        if (row == null) row = sheet.createRow(1);
        row.createCell(0).setCellValue(name);
        row.createCell(1).setCellValue(age);
        row.createCell(2).setCellValue(address);
        inputStream.close();

        File outputFolder = new File("output");
        if (!outputFolder.exists()) outputFolder.mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_PATH);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();

        return OUTPUT_PATH;
    }
    /* --------------------------------------------------------------------- */
    
    public String printExcel() {
        try {
        	String filePath = "MS11";
            File file = new File(filePath);
            if (!file.exists()) return "Lỗi: File không tồn tại!";
            
	        String desiredPrinterName = "iR-ADV 4551 III"; 
	        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
	        PrintService selectedPrinter = null;

	        for (PrintService printer : printServices) {
	            if (printer.getName().equalsIgnoreCase(desiredPrinterName)) {
	                selectedPrinter = printer;
	                break;
	            }
	        }

	        if (selectedPrinter == null) {
	            return "Không tìm thấy máy in: " + desiredPrinterName;
	        }

	        PrinterJob job = PrinterJob.getPrinterJob();
	        job.setPrintService(selectedPrinter);
	        job.setPrintable(this);

	        job.print();
	        return ("In Excel thành công trên máy in: " + selectedPrinter.getName());
	        
        } catch (Exception e) {
            return "Lỗi khi in: " + e.getMessage();
        }
    }

    /* --------------------------------------------------------------------- */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE; // Chỉ in 1 trang
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        g.drawString("In file Excel: " + OUTPUT_PATH, 100, 100);

        return PAGE_EXISTS;
    }
    /* --------------------------------------------------------------------- */
}

