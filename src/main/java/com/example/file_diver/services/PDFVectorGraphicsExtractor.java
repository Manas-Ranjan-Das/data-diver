package com.example.file_diver.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDFVectorGraphicsExtractor extends PDFStreamEngine {
    
    private StringBuilder svgBuilder = new StringBuilder();

    public PDFVectorGraphicsExtractor() throws IOException {
        // // Add necessary operators for vector graphics
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.MoveTo());
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.LineTo());
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.CurveTo());
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.ClosePath());
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.FillNonZeroRule());
        // addOperator(new org.apache.pdfbox.contentstream.operator.graphics.StrokePath());
    }

    public List<String> extractVectorGraphics(byte[] fileData){
        List<String> svgElements = new ArrayList<>();
        try {
            PDDocument document = PDDocument.load(fileData);
            PDFVectorGraphicsExtractor extractor = new PDFVectorGraphicsExtractor();

            for (PDPage page : document.getPages()) {
                svgElements.add(extractor.extractVectorGraphics(page));
            }
            
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return svgElements;
    }

    public String extractVectorGraphics(PDPage page) throws IOException {
        svgBuilder.append("<svg xmlns=\"http://www.w3.org/2000/svg\" ");
        svgBuilder.append("width=\"").append(page.getMediaBox().getWidth()).append("\" ");
        svgBuilder.append("height=\"").append(page.getMediaBox().getHeight()).append("\">\n");
        
        processPage(page);
        
        svgBuilder.append("</svg>");
        System.out.println("SVG Output:\n" + svgBuilder.toString());
        return svgBuilder.toString();
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
        String operation = operator.getName();
        PDGraphicsState graphicsState = getGraphicsState();
        Matrix ctm = graphicsState.getCurrentTransformationMatrix();

        switch (operation) {
            case "m": // moveTo
                float x = convertToFloat(operands.get(0));
                float y = convertToFloat(operands.get(1));
                x = ctm.transformPoint(x, y).x;
                y = ctm.transformPoint(x, y).y;
                svgBuilder.append("<path d=\"M").append(x).append(" ").append(y).append(" ");
                break;
        
            case "l": // lineTo
                x = convertToFloat(operands.get(0));
                y = convertToFloat(operands.get(1));
                x = ctm.transformPoint(x, y).x;
                y = ctm.transformPoint(x, y).y;
                svgBuilder.append("L").append(x).append(" ").append(y).append(" ");
                break;
        
            case "c": // curveTo
                float x1 = convertToFloat(operands.get(0));
                float y1 = convertToFloat(operands.get(1));
                float x2 = convertToFloat(operands.get(2));
                float y2 = convertToFloat(operands.get(3));
                float x3 = convertToFloat(operands.get(4));
                float y3 = convertToFloat(operands.get(5));
        
                x1 = ctm.transformPoint(x1, y1).x;
                y1 = ctm.transformPoint(x1, y1).y;
                x2 = ctm.transformPoint(x2, y2).x;
                y2 = ctm.transformPoint(x2, y2).y;
                x3 = ctm.transformPoint(x3, y3).x;
                y3 = ctm.transformPoint(x3, y3).y;
        
                svgBuilder.append("C").append(x1).append(" ").append(y1).append(", ")
                          .append(x2).append(" ").append(y2).append(", ")
                          .append(x3).append(" ").append(y3).append(" ");
                break;
        
            case "h": // closePath
                svgBuilder.append("Z\" fill=\"none\" stroke=\"black\"/>\n");
                break;
        
            case "f": // fill
                svgBuilder.append("\" fill=\"black\"/>\n");
                break;
        
            case "S": // stroke
                svgBuilder.append("\" fill=\"none\" stroke=\"black\"/>\n");
                break;
        
            default:
                break;
        }
    }

    private float convertToFloat(COSBase base) {
        if (base instanceof COSFloat) {
            return ((COSFloat) base).floatValue();
        } else if (base instanceof COSInteger) {
            return ((COSInteger) base).intValue();
        } else {
            throw new IllegalArgumentException("Unexpected operand type: " + base.getClass().getSimpleName());
        }
    }

    


}

