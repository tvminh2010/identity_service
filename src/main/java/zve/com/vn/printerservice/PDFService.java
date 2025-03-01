package zve.com.vn.printerservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class PDFService {

    public String createPdf(String textContent) throws IOException {
    
        Path folderPath = Path.of("pdf-files");
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        String filePath = "pdf-files/generated_" + System.currentTimeMillis() + ".pdf";
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(textContent);
        contentStream.endText();
        contentStream.close();

        document.save(filePath);
        document.close();

        return filePath;
    }
}
