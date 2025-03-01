package zve.com.vn.printerservice;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;

import javax.print.PrintServiceLookup;
import javax.print.PrintService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.springframework.stereotype.Service;


@Service
public class CustomPrintService implements Printable {
	
	/* --------------------------------------------------------------------- */
	public void print() {
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
	            System.out.println("Không tìm thấy máy in: " + desiredPrinterName);
	            return;
	        }

	        PrinterJob job = PrinterJob.getPrinterJob();
	        job.setPrintService(selectedPrinter);
	        job.setPrintable(this);

	        job.print();
	        System.out.println("In thành công trên máy in: " + selectedPrinter.getName());
	    } catch (PrinterException e) {
	        e.printStackTrace();
	    }
	}
	
	/* --------------------------------------------------------------------- */
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		 if (pageIndex > 0) {
	            return NO_SUCH_PAGE;		//hằng số được định nghĩa trong interface Printable của Java = 1
	        }

	        Graphics2D g2d = (Graphics2D) g;
	        g2d.translate(pf.getImageableX(), pf.getImageableY());

	        g.setFont(new Font("Arial", Font.PLAIN, 12));
	        g.drawString("Spring Boot - Bản in thử nghiệm!", 100, 100);

	        return PAGE_EXISTS;				//hằng số được định nghĩa trong interface Printable của Java = 0
	    }
	/* --------------------------------------------------------------------- */
	
	 public String printPdf(String filePath) {
	        try {
	            File pdfFile = new File(filePath);
	            if (!pdfFile.exists()) {
	                return "Lỗi: File PDF không tồn tại!";
	            }

	            PDDocument document = PDDocument.load(pdfFile);

	            PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
	            if (defaultPrinter == null) {
	                return "Không tìm thấy máy in mặc định!";
	            }

	            PrinterJob job = PrinterJob.getPrinterJob();
	            job.setPrintService(defaultPrinter);
	            job.setPageable(new PDFPageable(document));

	            job.print();
	            document.close();
	            return "In PDF thành công!";
	        } catch (Exception e) {
	            return "Lỗi khi in: " + e.getMessage();
	        }
	    }
	 /* --------------------------------------------------------------------- */
}

