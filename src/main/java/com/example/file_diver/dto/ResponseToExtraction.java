package com.example.file_diver.dto;

import java.util.HashMap;
import java.util.List;

public class ResponseToExtraction {
    private String pdfMetadata ;
    private List<FileData> pdfPagesAsImages ;
    private List<FileData> pdfEmbeddedImages ;
    private List<String> graphicalElements;
    private String extractedText;


    public static class FileData {
        private String fileName;
        private String contentType;
        private byte[] content;

        public byte[] getContent() {
            return content;
        }
        public String getContentType() {
            return contentType;
        }
        public String getFileName() {
            return fileName;
        }
        public void setContent(byte[] content) {
            this.content = content;
        }
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

    }
    public String getExtractedText() {
        return extractedText;
    }
    public List<String> getGraphicalElements() {
        return graphicalElements;
    }
    public List<FileData> getPdfEmbeddedImages() {
        return pdfEmbeddedImages;
    }
    public String getPdfMetadata() {
        return pdfMetadata;
    }
    public List<FileData> getPdfPagesAsImages() {
        return pdfPagesAsImages;
    }
    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
    public void setGraphicalElements(List<String> graphicalElements) {
        this.graphicalElements = graphicalElements;
    }
    public void setPdfEmbeddedImages(List<FileData> pdfEmbeddedImages) {
        this.pdfEmbeddedImages = pdfEmbeddedImages;
    }
    public void setPdfMetadata(String pdfMetadata) {
        this.pdfMetadata = pdfMetadata;
    }
    public void setPdfPagesAsImages(List<FileData> pdfPagesAsImages) {
        this.pdfPagesAsImages = pdfPagesAsImages;
    }

}
