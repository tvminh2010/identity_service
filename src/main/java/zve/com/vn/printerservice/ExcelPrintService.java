package zve.com.vn.printerservice;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExcelPrintService implements Printable {
   
    @Value("${excel.files.base-path}")
    private String excelBasePath;
    private static final String FOLDER_NAME = "MS11"; 
    private static final String FILE_NAME = "printing.xlsx";

    /* --------------------------------------------------------------------- */
    public String printExcel() {
	    try {
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
	        	return ("Không tìm thấy máy in: " + desiredPrinterName);
	        }

	        PrinterJob job = PrinterJob.getPrinterJob();
	        job.setPrintService(selectedPrinter);
	        job.setPrintable(this);

	        job.print();
	        return ("In thành công trên máy in: " + selectedPrinter.getName());
	        
	    } catch (PrinterException e) {
	        e.printStackTrace();
	        return "Lỗi khi in: " + e.getMessage();
	    }
    }
    /* --------------------------------------------------------------------- */
	//@Override
	public int print2(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
            return NO_SUCH_PAGE; 
        }
		
		try {
			/*/////////////////////////////////////////////////////////////////// */
			 String excelFilePath = Paths.get(excelBasePath, FOLDER_NAME, FILE_NAME).toString();
			 File fileToPrint = new File(excelFilePath);
			 if (!fileToPrint.exists()) {
	                System.err.println("File không tồn tại: " + excelFilePath);
	                return NO_SUCH_PAGE;
	         }

			 
			 FileInputStream fis = new FileInputStream(fileToPrint);
	         Workbook workbook;
	         if (FILE_NAME.toLowerCase().endsWith(".xls")) {
	        	  workbook = new HSSFWorkbook(fis);
	         } else if (FILE_NAME.toLowerCase().endsWith(".xlsx")) {
	        	 workbook = new XSSFWorkbook(fis);
	         } else {
	        	 System.err.println("Định dạng file không hợp lệ: " + FILE_NAME);
	             return NO_SUCH_PAGE;
	         }
	         Sheet sheet = workbook.getSheetAt(0); 
	         
	         Graphics2D g2d = (Graphics2D) g;
	         g2d.translate((int) pf.getImageableX(), (int) pf.getImageableY());
	         
	         int y = 50; // Vị trí bắt đầu in   
	         for (Row row : sheet) {
	                StringBuilder rowData = new StringBuilder();
	                for (Cell cell : row) {
	                    rowData.append(cell.toString()).append("  |  ");
	                }
	                g2d.drawString(rowData.toString(), 50, y); 
	                y += 20; 
	         }
	         
	         workbook.close();
	         return PAGE_EXISTS;
	    /*/////////////////////////////////////////////////////////////////// */    
		} catch (Exception e){
			 e.printStackTrace();
			 return NO_SUCH_PAGE;
		}
	}
	/* --------------------------------------------------------------------- */
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
            return NO_SUCH_PAGE; 
        }
		
		try {
			/*-------------------------------------------------------------------*/
			 String excelFilePath = Paths.get(excelBasePath, FOLDER_NAME, FILE_NAME).toString();
			 File fileToPrint = new File(excelFilePath);
			 if (!fileToPrint.exists()) {
	                System.err.println("File không tồn tại: " + excelFilePath);
	                return NO_SUCH_PAGE;
	         }

			 FileInputStream fis = new FileInputStream(fileToPrint);
	         Workbook workbook;
	         if (FILE_NAME.toLowerCase().endsWith(".xls")) {
	             workbook = new HSSFWorkbook(fis); // File Excel 97-2003 (.xls)
	         } else if (FILE_NAME.toLowerCase().endsWith(".xlsx")) {
	             workbook = new XSSFWorkbook(fis); // File Excel hiện đại (.xlsx)
	         } else {
	             System.err.println("Định dạng file không hợp lệ: " + FILE_NAME);
	             return NO_SUCH_PAGE;
	         }

	         Sheet sheet = workbook.getSheetAt(0); 
	         Graphics2D g2d = (Graphics2D) g;
	         g2d.translate((int) pf.getImageableX(), (int) pf.getImageableY());
	         int startX = 50; // Vị trí X bắt đầu in
	         int startY = 50; // Vị trí Y bắt đầu in
	         int rowHeight = 20; // Chiều cao mỗi dòng
	         int colWidth = 100; // Chiều rộng mặc định của mỗi cột
	         int y = startY;

	         for (Row row : sheet) {
	             int x = startX;
	             for (Cell cell : row) {
	                 String cellValue = cell.toString();

	                 // In nội dung của ô
	                 g2d.drawString(cellValue, x + 5, y + 15);

	                 // Lấy thông tin style của ô
	                 CellStyle cellStyle = cell.getCellStyle();
	                 BorderStyle borderTop = cellStyle.getBorderTop();
	                 BorderStyle borderBottom = cellStyle.getBorderBottom();
	                 BorderStyle borderLeft = cellStyle.getBorderLeft();
	                 BorderStyle borderRight = cellStyle.getBorderRight();

	                 // Định dạng đường viền dựa vào thông tin từ Excel
	                 g2d.setStroke(new java.awt.BasicStroke(1));
	                 if (borderTop != BorderStyle.NONE) g2d.drawLine(x, y, x + colWidth, y);
	                 if (borderBottom != BorderStyle.NONE) g2d.drawLine(x, y + rowHeight, x + colWidth, y + rowHeight);
	                 if (borderLeft != BorderStyle.NONE) g2d.drawLine(x, y, x, y + rowHeight);
	                 if (borderRight != BorderStyle.NONE) g2d.drawLine(x + colWidth, y, x + colWidth, y + rowHeight);
	                 x += colWidth;
	             }
	             y += rowHeight;
	         }

	         workbook.close();
	         return PAGE_EXISTS;
	        /*-------------------------------------------------------------------*/
		} catch (Exception e) {
			 e.printStackTrace();
			 return NO_SUCH_PAGE;
		}
	}
	/* --------------------------------------------------------------------- */
}

