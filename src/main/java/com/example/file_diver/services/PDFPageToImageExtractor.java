package com.example.file_diver.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
import com.example.file_diver.dto.ResponseToExtraction.FileData;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDFPageToImageExtractor {

    /**
     * Extracts each page from a PDF as an image and returns them as a list of FileData.
     * 
     * @param fileData the PDF file data as a byte array
     * @param imageFormat the format of the output images (e.g., "png", "jpeg")
     * @return a list of FileData objects, each containing an image of a PDF page
     */
    public List<FileData> extractPagesAsImages(byte[] fileData, String imageFormat) {
        List<FileData> pageImages = new ArrayList<>();

        try (PDDocument document = PDDocument.load(fileData)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            for (int i = 0; i < pageCount; i++) {
                // Render each page as an image
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);

                // Convert the image to a byte array
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, imageFormat, baos);
                    baos.flush();

                    // Create a new FileData object and set its properties
                    FileData fileDataObject = new FileData();
                    fileDataObject.setFileName("page_" + (i + 1) + "." + imageFormat);
                    fileDataObject.setContentType("image/" + imageFormat);
                    fileDataObject.setContent(baos.toByteArray());
                    
                    // Add the FileData object to the list
                    pageImages.add(fileDataObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pageImages;
    }
}
