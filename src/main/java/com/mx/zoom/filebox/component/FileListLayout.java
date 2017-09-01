/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Edrd
 */
public class FileListLayout extends Table {

    private final File file;
    private ThemeResource iconResource;
    private Image icon;
    private final Components component = new Components();
    private final ScheduleFileLogic viewLogic;

    public static final String[] DEFAULT_COLLAPSIBLE = {"fecha", "tamaño"};

    public FileListLayout(ScheduleFileLogic listaFileLogic, File file) {
        this.viewLogic = listaFileLogic;
        this.file = file;

        setSizeFull();
        addStyleName(ValoTheme.TABLE_BORDERLESS);
        addStyleName(ValoTheme.TABLE_NO_STRIPES);
        addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        addStyleName(ValoTheme.TABLE_SMALL);
        setSelectable(false);
        setImmediate(true);
        setSortEnabled(false);
        
        //setColumnAlignment("tamaño", Align.RIGHT);
        //setColumnAlignment("fecha", Align.RIGHT);
        setColumnHeader("icon", "");
        setColumnHeader("nombre", "Nombre");
        setColumnHeader("fecha", "Fecha Creación");
        setColumnHeader("tamaño", "Tamaño");

        //PARA HACER RESPONSIVO LA TABLA
        setColumnCollapsingAllowed(true);
        setColumnCollapsible("nombre", false);

        setColumnExpandRatio("nombre", 0.60f );   
        setColumnExpandRatio("fecha", 0.20f );
        setColumnExpandRatio("tamaño", 0.20f );
        refreshRowCache();
        //setRowHeaderMode(Table.RowHeaderMode.INDEX);          //PARA ENUMERAR LAS FILAS
        setContainerDataSource(crearContenedor());
    }

//    private boolean defaultColumnsVisible() {
//        boolean result = true;
//        for (String propertyId : DEFAULT_COLLAPSIBLE) {
//            System.out.println("propertyId = " + propertyId);
//            if (isColumnCollapsed(propertyId) == Page.getCurrent()
//                    .getBrowserWindowWidth() < 800) {
//                result = false;
//            }
//        }
//        return result;
//    }
//
//    @Subscribe
//    public void browserResized(final BrowserResizeEvent event) {
//        // Some columns are collapsed when browser window width gets small
//        // enough to make the table fit better.
//        if (defaultColumnsVisible()) {
//            for (String propertyId : DEFAULT_COLLAPSIBLE) {
//                System.out.println("propertyId2 = " + propertyId);
//                setColumnCollapsed(propertyId, Page.getCurrent()
//                        .getBrowserWindowWidth() < 800);
//            }
//        }
//    }

    public IndexedContainer crearContenedor() {

        IndexedContainer idxCont = new IndexedContainer();

        idxCont.addContainerProperty("icon", Image.class, "");
        idxCont.addContainerProperty("nombre", Button.class, "");
        idxCont.addContainerProperty("fecha", String.class, "");
        idxCont.addContainerProperty("tamaño", String.class, "");

        List<File> files = component.directoryContents(file);

        if (!files.isEmpty()) {
            for (File file : files) {
                String fileSize = FileUtils.byteCountToDisplaySize(file.length());

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty("icon").setValue(buildIcon(file));
                item.getItemProperty("nombre").setValue(buildButtonLink(file));
                item.getItemProperty("fecha").setValue(getAtributos());
                item.getItemProperty("tamaño").setValue(fileSize);
            }
        }

        return idxCont;
    }

    private Image buildIcon(File file) {
        icon = new Image(null, getIconExtension(file));
        icon.setWidth(30.0f, Unit.PIXELS);
        icon.setHeight(31.0f, Unit.PIXELS);
        return icon;
    }

    private ThemeResource getIconExtension(File file) {

        String extension = FilenameUtils.getExtension(file.getPath()).toLowerCase();
        if (file.isDirectory()) {
            iconResource = new ThemeResource("img/file_manager/folder_" + (file.list().length == 0 ? "empty" : "full") + ".png");
        } else {
            iconResource = new ThemeResource("img/file_manager/" + extension + ".png");
        }
        return iconResource;
    }
    
    private Button buildButtonLink(File file){
        Button btnLink = new Button(file.getName());
        btnLink.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        btnLink.addStyleName(ValoTheme.BUTTON_SMALL);
        btnLink.addStyleName(ValoTheme.BUTTON_LINK);
        
        return btnLink;
    }
    
     private String getAtributos() {
        String fechaCreacion = "";
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime date = attr.creationTime();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy   hh:mm:ss a");
            fechaCreacion = df.format(date.toMillis());
        } catch (IOException ex) {
        }

        return fechaCreacion;
    }

    
}
