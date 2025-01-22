package com.example.file_diver.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.plaf.metal.MetalScrollBarUI;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDAbstractPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;

import com.example.file_diver.dto.ResponseToExtraction;
import com.example.file_diver.models.FileEntity;
import com.example.file_diver.models.UserEntity;
import com.example.file_diver.repositories.FileRepository;
import com.example.file_diver.repositories.UserRepository;

@Service
public class FileService {
    @Autowired
    FileRepository fileRepository ;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PdfMetadataExtractionService pdfMetadataExtractionService;

    @Autowired
    PDFPageMetadataExtractor pdfPageMetadataExtractor;

    @Autowired 
    PDFPageToImageExtractor pdfPageToImageExtractor;

    @Autowired
    PDFImageExtractor pdfImageExtractor;

    @Autowired
    PDFVectorGraphicsExtractor pdfVectorGraphicsExtractor;

    public ResponseToExtraction responseToExtraction;

    public ResponseEntity<?> saveFile (MultipartFile file,Long userId) throws IOException{
        responseToExtraction = new ResponseToExtraction();
        // Retrieve the user (assuming user existence is already checked)
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate file (size, type, etc.)
        validateFile(file);

        // Map MultipartFile to FileEntity
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileType(file.getContentType());
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileData(file.getBytes()); // Ensure fileData is large enough
        fileEntity.setUploadedBy(user);
        fileRepository.save(fileEntity);
        // Save and return
        // extractResourcesFromFile(fileEntity.getFileData());
        responseToExtraction.setPdfMetadata(listAllObjectsInCrossReference(fileEntity.getFileData()));
        responseToExtraction.setPdfPagesAsImages(pdfPageToImageExtractor.extractPagesAsImages(fileEntity.getFileData(),"jpg"));
        responseToExtraction.setPdfEmbeddedImages(pdfImageExtractor.extractImagesFromPDF(fileEntity.getFileData()));
        responseToExtraction.setGraphicalElements(pdfVectorGraphicsExtractor.extractVectorGraphics(fileEntity.getFileData()));
        responseToExtraction.setExtractedText(extractTextFromPDF(fileEntity.getFileData()));
        return new ResponseEntity<>(responseToExtraction,HttpStatus.OK);
    }

    boolean validateFile(MultipartFile file){
        return true;
    }

    HashSet<COSBase> indirectObjectSet ;
    HashSet<COSBase> preventRepeatSet ;

    public String listAllObjectsInCrossReference(byte[] file) {
        StringBuilder jsonString = new StringBuilder();
        StringBuilder temp ;
        indirectObjectSet = new HashSet<>();
        preventRepeatSet = new HashSet<>();
        try (PDDocument document = PDDocument.load(file)) {
            COSDocument cosDoc = document.getDocument();
            List<COSObject> allObjects = cosDoc.getObjects(); // This gives you all objects in the cross-reference table
            for (COSObject cosObject : allObjects) {
                indirectObjectSet.add(cosObject.getCOSObject());
            }
            System.out.println("All objects in the cross-reference table:");
            jsonString.append("{");
            for (COSObject obj : allObjects) {
                // System.out.println("Object: " + obj.getObjectNumber() + " Generation: " + obj.getGenerationNumber());
                indirectObjectSet.remove(obj);
                temp=traverseCOSObject(obj.getCOSObject());
                // System.out.println(temp); // Print the actual COSBase object
                indirectObjectSet.add(obj);
                jsonString.append("\"")
                    .append(obj.getObjectNumber())
                    .append(" ")
                    .append(obj.getGenerationNumber())
                    .append(" R\" : ");
                jsonString.append(temp).append(",");
            }
            jsonString.setCharAt(jsonString.length()-1, '}');
            System.out.println(jsonString);
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonString.toString();
    }

    public StringBuilder traverseCOSObject(COSBase cosObject) {
        StringBuilder output = new StringBuilder();
        if (cosObject instanceof COSObject) {
            COSObject cosObj = (COSObject) cosObject;
            if (indirectObjectSet.contains(cosObj)) {  // Directly check for COSObject
                output.append("\"").append(cosObj.getObjectNumber()).append(" ").append(cosObj.getGenerationNumber()).append(" R\"");
                return output;
            }
            cosObject = cosObj.getObject();
        }
        
        

        if (cosObject instanceof COSDictionary) {
            preventRepeatSet.add(cosObject);
            COSDictionary dict = (COSDictionary) cosObject;
            output.append("{ ");
            for (Map.Entry<COSName, COSBase> entry : dict.entrySet()) {
                output.append("\"").append(entry.getKey().getName()).append("\" : ");
                output.append(traverseCOSObject(entry.getValue())).append(" ,");
            }
            if (cosObject instanceof COSStream) {
                COSStream stream = (COSStream) cosObject;
                
                    output.append("\"Stream\" : \"").append("Stream Data").append("\",");
                
                // catch(IOException e){
                //     e.printStackTrace();
                // }
            }
            output.setCharAt(output.length()-1, '}');
        } else if (cosObject instanceof COSArray) {
            preventRepeatSet.add(cosObject);
            output.append("[ ");
            COSArray array = (COSArray) cosObject;
            for (COSBase item : array) {
                output.append(traverseCOSObject(item)).append(",");
            }
            output.setCharAt(output.length()-1, ']');
        } else if (cosObject instanceof COSName) {
            COSName name = (COSName) cosObject;
            output.append("\"").append(name.getName()).append("\"");
        } else if (cosObject instanceof COSInteger) {
            COSInteger integer = (COSInteger) cosObject;
            output.append(integer.intValue());
        } else if (cosObject instanceof COSFloat) {
            COSFloat cosFloat = (COSFloat) cosObject;
            output.append(cosFloat.floatValue());
        } else if (cosObject instanceof COSString) {
            COSString cosString = (COSString) cosObject;
            output.append("\"").append(cosString.getString()).append("\"");
        } else if (cosObject instanceof COSBoolean) {
            COSBoolean cosBoolean = (COSBoolean) cosObject;
            output.append(cosBoolean.getValue());
        } else {
            output = null;
            System.out.println("Found an unknown COS object type: " + cosObject.getClass().getSimpleName());
        }
        // System.out.println(output + "\n\n\n");
        return output;
    }

    public void extractResourcesFromPDF(byte[] file) {
        try (PDDocument document = PDDocument.load(file)) {
            int pageCount = document.getNumberOfPages();
            System.out.println("Total Pages: " + pageCount);
            
            for (int i = 0; i < pageCount; i++) {
                PDPage page = document.getPage(i);
                System.out.println("Page " + (i + 1) + ":");

                // Get the resources of the current page
                PDResources resources = page.getResources();
                
                // Extract fonts
                resources.getFontNames().forEach(fontName -> {
                    try {
                        PDFont font = resources.getFont(fontName);
                        System.out.println(" - Font: " + font.getName() + ", Subtype: " + font.getSubType());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // Extract images
                resources.getXObjectNames().forEach(xObjectName -> {
                    try {
                        if (resources.getXObject(xObjectName) instanceof PDImageXObject) {
                            PDImageXObject image = (PDImageXObject) resources.getXObject(xObjectName);
                            System.out.println(" - Image: " + xObjectName.getName() + 
                                               ", Width: " + image.getWidth() + 
                                               ", Height: " + image.getHeight());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
                // Extract patterns
                resources.getPatternNames().forEach(patternName -> {
                    try {
                        PDAbstractPattern pattern = resources.getPattern(patternName);
                        System.out.println(" - Pattern: " + patternName.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                
                // Extract shadings
                resources.getShadingNames().forEach(shadingName -> {
                    try {
                        PDShading shading = resources.getShading(shadingName);
                        System.out.println(" - Shading: " + shadingName.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readCOSStreamAsString(COSStream cosStream) throws IOException {
        // Get the decoded stream
        try (InputStream stream = cosStream.createInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {

            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");  // Append each line and add a newline
            }
            return content.toString();
        }
    }

    public String extractTextFromPDF(byte[] pdfFileBytes) {
        try (PDDocument document = PDDocument.load(pdfFileBytes)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
