/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.view.schedule;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFamily;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.aspose.cells.PageSetup;
import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.mx.zoom.filebox.utils.Constantes;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.server.ThemeResource;

import java.io.File;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Edrd
 */
public class ConvertToPDF {

    private File output;

    public ConvertToPDF() {
    }

    public File getPDF(File file) {
        File outputPDF = null;
        String extension = findExtension(FilenameUtils.getExtension(file.getPath()).toLowerCase());
        String namePDF = FilenameUtils.getBaseName(file.getName()).concat(".pdf");

        Boolean bnd = checkServerAvailability(Constantes.OPENOFFICE_HOST, Constantes.OPENOFFICE_PORT);
        System.out.println("bnd= " + bnd);
        if (bnd) {
            switch (extension) {
                case "word":
                    outputPDF = convertDOCtoPDF(file, namePDF);
                    break;
                case "texto":
                    outputPDF = convertDOCtoPDF(file, namePDF);
                    break;
                case "codigo":
                    outputPDF = convertDOCtoPDF(file, namePDF);
                    break;
                case "excel":
                    outputPDF = convertXLStoPDF_Aspose(file, namePDF);
                    break;
                case "powerpoint":
                    outputPDF = convertPPTtoPDF(file, namePDF);
                    break;

            }
        }
        return outputPDF;
    }

    private File convertXLStoPDF_Aspose(File inputFile, String outputPDF) {
        output = new File(Constantes.OUTPUT_PDF.concat(outputPDF));
        System.out.println("outputXls = " + output);
        try {

            Workbook workbook = new Workbook(inputFile.getPath());

            Worksheet worksheet = workbook.getWorksheets().get(0);

            PageSetup pageSetup = worksheet.getPageSetup();
            //pageSetup.setOrientation(PageOrientationType.LANDSCAPE);
            pageSetup.setBottomMargin(1);
            pageSetup.setLeftMargin(1);
            pageSetup.setRightMargin(1);
            pageSetup.setTopMargin(1);
            // Setting the number of pages to which the width of the worksheet will be spanned
            pageSetup.setFitToPagesWide(1);
            // Setting the number of pages to which the length of the worksheet will be spanned
            pageSetup.setFitToPagesTall(1);
            pageSetup.setCenterHorizontally(true);
            //pageSetup.setCenterVertically(true);

            //workbook.save(namePDF, FileFormatType.PDF);
            workbook.save(output.getPath(), SaveFormat.PDF);
        } catch (Exception ex) {

        }
        return output;
    }

    private File convertDOCtoPDF(File inputFile, String outputPDF) {
        output = new File(Constantes.OUTPUT_PDF.concat(outputPDF));
        System.out.println("outputDoc = " + output);
        try {
            OpenOfficeConnection connection = null;
            // connect to an OpenOffice.org instance running on port 8100
            connection = new SocketOpenOfficeConnection(Constantes.OPENOFFICE_PORT);
            connection.connect();
            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            // convert (doc,xls,ppt)
            //converter.convert(inputFile, outputFile);

            //convert (doc, docx, xlsx, pptx)
            final DocumentFormat docx = new DocumentFormat("Microsoft Word 2007 XML", DocumentFamily.TEXT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
            converter.convert(inputFile, docx, output, null);

            // close the connection
            connection.disconnect();
        } catch (ConnectException ex) {
            Logger.getLogger(ConvertToPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    private File convertPPTtoPDF(File inputFile, String outputPDF) {
        output = new File(Constantes.OUTPUT_PDF.concat(outputPDF));
        System.out.println("outputPpt = " + output);
        try {
            OpenOfficeConnection connection = null;
            // connect to an OpenOffice.org instance running on port 8100
            connection = new SocketOpenOfficeConnection(8100);
            connection.connect();
            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            // convert (doc,xls,ppt)
            //converter.convert(inputFile, outputFile);

            //convert (doc, docx, xlsx, pptx)
            final DocumentFormat pptx = new DocumentFormat("Microsoft Power Point 2007 XML", DocumentFamily.PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
            converter.convert(inputFile, pptx, output, null);

            // close the connection
            connection.disconnect();
        } catch (ConnectException ex) {
            Logger.getLogger(ConvertToPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    private File convertCODtoPDF(File inputFile, String outputPDF) {
        output = new File(Constantes.OUTPUT_PDF.concat(outputPDF));
        System.out.println("outputDoc = " + output);
        try {
            OpenOfficeConnection connection = null;
            // connect to an OpenOffice.org instance running on port 8100
            connection = new SocketOpenOfficeConnection(Constantes.OPENOFFICE_PORT);
            connection.connect();
            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            // convert (doc,xls,ppt)
            final DocumentFormat cod = new DocumentFormat("HTML", DocumentFamily.TEXT, "text/html", "html");
            cod.setExportFilter(DocumentFamily.TEXT, "HTML (StarWriter)");
            DocumentFormat outputDocumentFormat = new DocumentFormat("Portable Document Format", DocumentFamily.TEXT, "application/pdf", "pdf");
            outputDocumentFormat.setExportFilter(DocumentFamily.TEXT, "writer_pdf_Export");
            converter.convert(inputFile, cod, output, outputDocumentFormat);

            // close the connection
            connection.disconnect();
        } catch (ConnectException ex) {
            Logger.getLogger(ConvertToPDF.class.getName()).log(Level.SEVERE, null, ex);
        }
        return output;
    }

    private String findExtension(String extension) {
        String formato = "desconocido";

        List<String[]> allFileFormats = new ArrayList<>();
        for (FileFormats fileFormats : FileFormats.values()) {
            allFileFormats.add(fileFormats.getArrayFileFormats());
        }

        for (String[] array : allFileFormats) {
            if (ArrayUtils.contains(array, extension)) {
                formato = FileFormats.values()[allFileFormats.indexOf(array)].toString().toLowerCase();
                break;
            }
        }
        System.out.println("formato = " + formato);
        return formato;
    }

    private boolean checkServerAvailability(String host, int port) {
        try {
            Socket testSocket = new Socket(host, port);
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }
}
