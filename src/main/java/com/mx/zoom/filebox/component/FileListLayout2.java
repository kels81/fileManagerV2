/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Edrd
 */
public class FileListLayout2 extends Table {

    private File file;
    private ThemeResource iconResource;
    private Image icon;
    private final Components component = new Components();
    private final ScheduleFileLogic viewLogic;

    private final String COL_FILE = "file";
    private final String COL_ICON = "icon";
    private final String COL_NOMBRE = "nombre";
    private final String COL_MODIFICADO = "modificado";
    private final String COL_TAMANIO = "tamanio";

    //public final Object[] COLUMNS_VISIBLES = {"icon", "nombre", "modificado", "tamanio"};
    public final Object[] COLUMNS_VISIBLES = {COL_ICON, COL_NOMBRE, COL_MODIFICADO, COL_TAMANIO};
    public static final String[] COLUMNS_HEADERS = {"", "Nombre", "Modificado", "tamaño"};
    public static final String[] DEFAULT_COLLAPSIBLE = {"modificado", "tamanio"};

    public FileListLayout2(ScheduleFileLogic listaFileLogic, File file) {
        this.viewLogic = listaFileLogic;
        //this.file = file;

        setSizeFull();
        addStyleName(ValoTheme.TABLE_BORDERLESS);
        addStyleName(ValoTheme.TABLE_NO_STRIPES);
        addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        addStyleName(ValoTheme.TABLE_SMALL);
        setSelectable(true);

        setImmediate(true);
        setSortEnabled(false);

        setColumnAlignment(COL_MODIFICADO, Align.RIGHT);
        setColumnAlignment(COL_TAMANIO, Align.RIGHT);

        setColumnHeader(COL_ICON, "");
        setColumnHeader(COL_NOMBRE, "Nombre");
        setColumnHeader(COL_MODIFICADO, "Modificado");
        setColumnHeader(COL_TAMANIO, "Tamaño");

        //PARA HACER RESPONSIVO LA TABLA
        setColumnCollapsingAllowed(true);
        setColumnCollapsible(COL_NOMBRE, false);

        setColumnExpandRatio(COL_NOMBRE, 0.60f);
        setColumnExpandRatio(COL_MODIFICADO, 0.20f);
        setColumnExpandRatio(COL_TAMANIO, 0.20f);
        refreshRowCache();
        //setRowHeaderMode(Table.RowHeaderMode.INDEX);          //PARA ENUMERAR LAS FILAS
        setContainerDataSource(crearContenedor(file));
        //setContainerDataSource(new BeanItemContainer<>(crearContenedorFile(file)));
        setVisibleColumns(COLUMNS_VISIBLES);

        addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                //Notification.show(event.getItemId().toString());
                Notification.show(event.getItem().getItemProperty(COL_FILE).getValue().toString());
            }
        });
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
    private IndexedContainer crearContenedor(File directory) {

        IndexedContainer idxCont = new IndexedContainer();

        idxCont.addContainerProperty(COL_FILE, File.class, "");
        idxCont.addContainerProperty(COL_ICON, Image.class, "");
        //idxCont.addContainerProperty(COL_NOMBRE, Button.class, "");
        idxCont.addContainerProperty(COL_NOMBRE, String.class, "");
        idxCont.addContainerProperty(COL_MODIFICADO, String.class, "");
        idxCont.addContainerProperty(COL_TAMANIO, String.class, "");

        List<File> files = component.directoryContents(directory);

        if (!files.isEmpty()) {
            for (File fileRow : files) {
                this.file = fileRow;
                String fileSize = FileUtils.byteCountToDisplaySize(file.length());

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty(COL_FILE).setValue(file);
                item.getItemProperty(COL_ICON).setValue(buildIcon());
                //item.getItemProperty(COL_NOMBRE).setValue(buildButtonLink());
                item.getItemProperty(COL_NOMBRE).setValue(buildName());
                item.getItemProperty(COL_MODIFICADO).setValue(getAtributos());
                item.getItemProperty(COL_TAMANIO).setValue(fileSize);
            }
        }

        return idxCont;
    }

    private List<File> crearContenedorFile(File directory) {
        List<File> files = component.directoryContents(directory);
        return files;
    }

    private Image buildIcon() {
        icon = new Image(null, getIconExtension());
        icon.setWidth(30.0f, Unit.PIXELS);
        icon.setHeight(31.0f, Unit.PIXELS);
        return icon;
    }

    private ThemeResource getIconExtension() {

        String extension = FilenameUtils.getExtension(file.getPath()).toLowerCase();
        if (file.isDirectory()) {
            iconResource = new ThemeResource("img/file_manager/folder_" + (file.list().length == 0 ? "empty" : "full") + ".png");
        } else {
            //documento
            //iconResource = new ThemeResource("img/file_manager/" + extension + ".png");
            iconResource = findExtension(extension);
        }
        return iconResource;
    }

    private ThemeResource findExtension(String extension) {
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

        return new ThemeResource("img/file_manager/" + formato + ".png");
    }

    private Button buildButtonLink2() {
        Button btnLink = new Button(file.getName());
        btnLink.addStyleName(ValoTheme.BUTTON_SMALL);
        btnLink.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        //btnLink.addStyleName(ValoTheme.BUTTON_LINK);

        return btnLink;
    }

    private String buildName() {
        return file.getName();
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
