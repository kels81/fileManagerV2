/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component.window;

import com.mx.zoom.filebox.utils.FileFormats;
import com.mx.zoom.filebox.view.schedule.ConvertToPDF;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.vaadin.peter.imagescaler.ImageScaler;
import pl.pdfviewer.PdfViewer;

/**
 *
 * @author Edrd
 */
public class ViewerWindow extends Window {

    private final VerticalLayout viewer;
    private final TabSheet content;

    private final String[] DOC = {"word", "texto", "excel", "powerpoint", "codigo"};

    public ViewerWindow(File file) {

//        addStyleName("viewerwindow");
        Responsive.makeResponsive(this);
        setModal(true);
        setResizable(false);
        setClosable(true);
        center();

        setHeight(90.0f, Unit.PERCENTAGE);
        setWidth(90.0f, Unit.PERCENTAGE);

        content = new TabSheet();
        content.setSizeFull();
        content.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);

        viewer = new VerticalLayout();
        viewer.setCaption("Visor");
        viewer.setSizeFull();
        viewer.setMargin(true);
        content.addComponent(viewer);

        String extension = findExtension(FilenameUtils.getExtension(file.getPath()).toLowerCase());
        //imagen, pdf, doc("word", "texto", "excel", "powerpoint")

        switch (extension) {
            case "doc":
                buildDocToPDF(file);
                break;
            case "imagen":
                buildImagen(file);
                break;
            case "pdf":
                buildPDF(file);
                break;
            default:
                buildEmpty();
                break;
        }

        setContent(content);
//        Page.getCurrent().getStyles().add(".v-verticallayout {border: 1px solid blue;} .v-verticallayout .v-slot {border: 1px solid red;}");
    }

    private void buildDocToPDF(File file) {
        File outputPDF = new ConvertToPDF().getPDF(file);
        System.out.println("outputPDF = " + outputPDF.toString());

        Component viewerPDF = buildViewerPDF(outputPDF);
        addViewer(viewerPDF);
    }

    private void buildImagen(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            Integer width = image.getWidth();
            Integer height = image.getHeight();
            System.out.println("Width: " + width);
            System.out.println("Height: " + height);
            ImageScaler scaler = new ImageScaler();
            scaler.setImage(new FileResource(file), width, height); //w, h
            scaler.setRecalculateOnSizeChangeEnabled(true); // Optional

            viewer.addComponent(scaler);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void buildPDF(File file) {
        Component viewerPDF = buildViewerPDF(file);
        addViewer(viewerPDF);
    }

    private void buildEmpty() {
        Label label = new Label("Lo sentimos, la vista previa no se cargó. Este tipo de archivo puede no ser compatible.");
        label.addStyleName(ValoTheme.LABEL_LIGHT);

        viewer.addComponent(label);
        viewer.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
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

        for (String type : DOC) {
            if (type.equals(formato)) {
                formato = "doc";
                break;
            }
        }

        System.out.println("formato = " + formato);
        return formato;
    }

    private Component buildViewerPDF(File file) {
        PdfViewer viewer = new PdfViewer(file);
        viewer.setSizeFull();

//        viewer.setPreviousPageCaption(FontAwesome.ANGLE_LEFT.getHtml() + " Back");
//        viewer.setNextPageCaption("Next " + FontAwesome.ANGLE_RIGHT.getHtml());
        viewer.setPreviousPageCaption(FontAwesome.ANGLE_LEFT.getHtml());
        viewer.setNextPageCaption(FontAwesome.ANGLE_RIGHT.getHtml());

        viewer.setDecreaseButtonCaption(FontAwesome.SEARCH_MINUS.getHtml());
        viewer.setIncreaseButtonCaption(FontAwesome.SEARCH_PLUS.getHtml());

        viewer.setBackAngleButtonCaption(FontAwesome.ROTATE_LEFT.getHtml());
        viewer.setNextAngleButtonCaption(FontAwesome.ROTATE_RIGHT.getHtml());
        viewer.setDownloadBtnVisible(false);

        return viewer;
    }

    private void addViewer(Component viewerPDF) {
        viewer.addComponent(viewerPDF);
        viewer.setComponentAlignment(viewerPDF, Alignment.MIDDLE_CENTER);
    }

    private Embedded buildViewerPDF2(File file) {

        Embedded viewer = new Embedded(null, new FileResource(file));
        viewer.setMimeType("application/pdf");

        viewer.setType(Embedded.TYPE_BROWSER);
        viewer.setSizeFull();
//        viewer.setHeight("100%");
//        viewer.setWidth("100%");

        return viewer;
    }

}
