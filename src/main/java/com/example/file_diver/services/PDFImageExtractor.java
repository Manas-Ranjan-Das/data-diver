package com.example.file_diver.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import com.example.file_diver.dto.ResponseToExtraction.FileData;

import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.cos.COSName;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PDFImageExtractor {

    public List<FileData> extractImagesFromPDF(byte[] fileData) {
        List<byte[]> imageBytesList = new ArrayList<>();
        List<FileData> files = new ArrayList<>();
        FileData temp;
        try (PDDocument document = PDDocument.load(fileData)) {
            int pageCount = document.getNumberOfPages();

            for (int i = 0; i < pageCount; i++) {
                PDPage page = document.getPage(i);
                PDResources resources = page.getResources();

                // Track image names for unique naming
                Map<String, Integer> imageCount = new HashMap<>();

                for (COSName xObjectName : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(xObjectName);

                    // Check if the XObject is an image
                    if (xObject instanceof PDImageXObject) {
                        PDImageXObject image = (PDImageXObject) xObject;

                        // Generate a unique name for the image
                        String imageName = "image_" + (i + 1) + "_" + xObjectName.getName();
                        int count = imageCount.getOrDefault(imageName, 0) + 1;
                        imageCount.put(imageName, count);
                        imageName += "_" + count;

                        // Convert the image to a byte array
                        BufferedImage bImage = image.getImage();
                        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                            ImageIO.write(bImage, "png", baos);
                            baos.flush();
                            imageBytesList.add(baos.toByteArray());
                            temp = new FileData();
                            temp.setFileName(imageName);
                            temp.setContentType("Image/png");
                            temp.setContent(baos.toByteArray());
                            files.add(temp);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }
}