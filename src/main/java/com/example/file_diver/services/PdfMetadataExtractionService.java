package com.example.file_diver.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSDictionary;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Service
public class PdfMetadataExtractionService {

    public void extractAllMetadata(byte[] file) {
        try (PDDocument document = PDDocument.load(file)) {
            // Extract standard document information
            PDDocumentInformation info = document.getDocumentInformation();
            
            // Extract all custom metadata properties
            for (COSName key : info.getCOSObject().keySet()) {
                
                    System.out.println(key + ": " + info.getCOSObject().getString(COSName.getPDFName(key.getName())));
                
            }

            // Extract XMP metadata if available
            PDMetadata metadata = document.getDocumentCatalog().getMetadata();
            if (metadata != null) {
                try (InputStream xmlInputStream = metadata.createInputStream()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    org.w3c.dom.Document xmlDocument = builder.parse(xmlInputStream);
                    xmlDocument.getDocumentElement().normalize();

                    System.out.println("XMP Metadata:");
                    extractXMPMetadata(xmlDocument.getDocumentElement(), "");
                }
            } else {
                System.out.println("No XMP metadata found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatDate(Calendar date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            return sdf.format(date.getTime());
        }
        return "Unknown";
    }

    private void extractXMPMetadata(org.w3c.dom.Node node, String indent) {
        // Recursively extract and print all elements in the XMP XML metadata
        System.out.println(indent + node.getNodeName() + ": " + node.getTextContent().trim());
        org.w3c.dom.Node child = node.getFirstChild();
        while (child != null) {
            extractXMPMetadata(child, indent + "  ");
            child = child.getNextSibling();
        }
    }
}
