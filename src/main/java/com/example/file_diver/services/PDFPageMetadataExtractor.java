package com.example.file_diver.services;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.awt.geom.Rectangle2D;

@Service
public class PDFPageMetadataExtractor {

    public void extractPageMetadata(byte[] file) {
        try (PDDocument document = PDDocument.load(file)) {
            int pageCount = document.getNumberOfPages();
            System.out.println("Total Pages: " + pageCount);
            int i = 0;
            for (PDPage page : document.getPages()) {
                i++;
                System.out.println("\nPage " + (i) + ":");

                // Page dimensions (media box)
                PDRectangle mediaBox = page.getMediaBox();
                System.out.println("MediaBox (Dimensions): " + mediaBox);

                // Page rotation
                int rotation = page.getRotation();
                System.out.println("Rotation: " + rotation);

                // Resources (Fonts, Images, etc.)
                PDResources resources = page.getResources();

                // Fonts (Safely access fonts)
                for (COSName fontName : resources.getFontNames()) {
                    try {
                        PDFont font = resources.getFont(fontName);
                        System.out.println("Font: " + font.getName());
                    } catch (IOException e) {
                        System.out.println("Error extracting font: " + fontName);
                    }
                }

                // Images
                for (COSName xObjectName : resources.getXObjectNames()) {
                    if (resources.isImageXObject(xObjectName)) {
                        PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);
                        System.out.println("Image: " + xObjectName + ", Width: " + image.getWidth() + ", Height: " + image.getHeight());
                    }
                }

                // Annotations (e.g., Comments, Form Fields)
                List<PDAnnotation> annotations = page.getAnnotations();
                for (PDAnnotation annotation : annotations) {
                    System.out.println("Annotation Type: " + annotation.getSubtype());
                    System.out.println("Annotation Contents: " + annotation.getContents());
                    System.out.println("Annotation Rectangle: " + annotation.getRectangle());
                    // If more details are needed based on type
                    if ("Link".equals(annotation.getSubtype())) {
                        System.out.println("Link Annotation: " + annotation.getCOSObject());
                    }
                }

                // Extract text from the page (optional)
                PDFTextStripperByArea textStripper = new PDFTextStripperByArea();
                // Convert PDRectangle mediaBox to Rectangle2D
                Rectangle2D rect = new Rectangle2D.Float(
                    mediaBox.getLowerLeftX(),
                    mediaBox.getLowerLeftY(),
                    mediaBox.getWidth(),
                    mediaBox.getHeight()
                );
                // Add region using the converted Rectangle2D
                textStripper.addRegion("content", rect);
                textStripper.extractRegions(page);
                String text = textStripper.getTextForRegion("content");
                System.out.println("Extracted Text:\n" + text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error processing PDF document: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
