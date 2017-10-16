/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.google.common.eventbus.Subscribe;
import com.mx.zoom.filebox.component.contextmenu.ButtonContextMenu;
import com.mx.zoom.filebox.event.DashboardEvent.BrowserResizeEvent;
import com.mx.zoom.filebox.event.DashboardEventBus;
import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
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
public class FileListLayout extends VerticalLayout implements View {

    private File file;
    private ThemeResource iconResource;
    private Image icon;
    private final Components component = new Components();

    private IndexedContainer idxCont;
    private Table table;
    private final Button downloadInvisibleButton = new Button();

    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    private final String COL_FILE = "file";
    private final String COL_ICON = "icon";
    private final String COL_NOMBRE = "nombre";
    private final String COL_TAMANIO = "tamanio";
    private final String COL_MODIFICADO = "modificado";
    private final String COL_CONTEXT_MENU = "contextMenu";

    private final Object[] COLUMNS_VISIBLES = {COL_ICON, COL_NOMBRE, COL_TAMANIO, COL_MODIFICADO, COL_CONTEXT_MENU};
    private final String[] COLUMNS_HEADERS = {"", "Nombre", "Tamaño", "Modificado", ""};
    private final String[] DEFAULT_COLLAPSIBLE = {COL_TAMANIO, COL_MODIFICADO};

    public FileListLayout(ScheduleFileLogic mosaicoFileLogic, ScheduleDirectoryLogic mosaicoDirectoryLogic, File file) {
        this.viewLogicFile = mosaicoFileLogic;
        this.viewLogicDirectory = mosaicoDirectoryLogic;

        setSizeFull();
        addStyleName("listView");
        DashboardEventBus.register(this);   //NECESARIO PARA CONOCER LA ORIENTACION Y RESIZE DEL BROWSER

        Component table = buildTable(file);
        addComponent(table);
        setExpandRatio(table, 1);
        browserResized();
//        System.out.println("width-->" + Page.getCurrent().getBrowserWindowWidth());
//        System.out.println("height-->" + Page.getCurrent().getBrowserWindowHeight());
        
        //BUTTON PARA PODER DESCARGAR ARCHIVOS POR MEDIO DEL CONTEXT MENU
        downloadInvisibleButton.setId("DownloadButtonId");
        downloadInvisibleButton.addStyleName("InvisibleButton");
        addComponent(downloadInvisibleButton);
    }

    //METODO NECESARIO PARA CONOCER LA ORIENTACION Y RESIZE DEL BROWSER
    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        DashboardEventBus.unregister(this);
    }

    private Table buildTable(File file) {
        table = new Table();
        table.setContainerDataSource(crearContenedor(file));
        table.setSizeFull();
//        table.setPageLength(8);
        table.setImmediate(true);
        table.setSelectable(true);
//        table.setMultiSelect(true);
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.addStyleName("noselect");

        table.setVisibleColumns(COLUMNS_VISIBLES);
        table.setColumnHeaders(COLUMNS_HEADERS);

        table.setSortEnabled(false);
        table.setColumnAlignment(COL_MODIFICADO, Align.RIGHT);
        table.setColumnAlignment(COL_CONTEXT_MENU, Align.CENTER);

        //PARA HACER RESPONSIVO LA TABLA
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsible(COL_NOMBRE, false);

        table.setColumnExpandRatio(COL_NOMBRE, 0.50f);
        table.setColumnExpandRatio(COL_MODIFICADO, 0.20f);
        table.setColumnExpandRatio(COL_TAMANIO, 0.20f);
        table.setColumnExpandRatio(COL_CONTEXT_MENU, 0.10f);
        table.refreshRowCache();
        //setRowHeaderMode(Table.RowHeaderMode.INDEX);          //PARA ENUMERAR LAS FILAS

        //setContainerDataSource(new BeanItemContainer<>(crearContenedorFile(file)));
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getItem() != null) {
                    File file = new File(event.getItem().getItemProperty(COL_FILE).getValue().toString());
                    if (event.isDoubleClick()) {
                        //table.select(selectedItemInTheRow);
                        if (file.isDirectory()) {
                            viewLogicFile.cleanAndDisplay(file);
                        } else if (file.isFile()) {
                            Notification.show("Ver archivo: " + file.getName());
//                        Window w = new ViewerWindow(file);;
//                        UI.getCurrent().addWindow(w);
//                        w.focus();
                        }
                    }
                }

            }
        });

//        table.setCellStyleGenerator((Table source, Object itemId, Object propertyId) -> {
//            String stilo = "";
//            if (propertyId != null
//                    && (COL_CONTEXT_MENU.equals((String) propertyId))) {
//                
//                stilo = "black";
//                menuBar.addStyleName(ValoTheme.BUTTON_DANGER);
//            }
//            return stilo;
//        });
        return table;
    }

    private IndexedContainer crearContenedor(File directory) {
        idxCont = new IndexedContainer();

        idxCont.addContainerProperty(COL_ICON, Image.class, "");
        idxCont.addContainerProperty(COL_NOMBRE, Label.class, "");
        idxCont.addContainerProperty(COL_TAMANIO, String.class, "");
        idxCont.addContainerProperty(COL_MODIFICADO, String.class, "");
        idxCont.addContainerProperty(COL_CONTEXT_MENU, MenuBar.class, "");
        idxCont.addContainerProperty(COL_FILE, File.class, "");

        List<File> files = component.directoryContents(directory);
        if (!files.isEmpty()) {
            for (File fileRow : files) {
                this.file = fileRow;

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty(COL_ICON).setValue(buildIcon());
                item.getItemProperty(COL_NOMBRE).setValue(getFileName());
                item.getItemProperty(COL_TAMANIO).setValue(getNumberOfElementsAndFileSize());
                item.getItemProperty(COL_MODIFICADO).setValue(getAtributos());
                item.getItemProperty(COL_CONTEXT_MENU).setValue(createButtonContextMenu());
                item.getItemProperty(COL_FILE).setValue(file);
            }
        }

        return idxCont;
    }

//    private List<File> crearContenedorFile(File directory) {
//        List<File> files = component.directoryContents(directory);
//        return files;
//    }
    private Image buildIcon() {
        icon = new Image(null, getIconExtension());
        icon.setWidth((file.isDirectory() ? 35.0f : 31.0f), Unit.PIXELS);
        icon.setHeight((file.isDirectory() ? 33.0f : 39.0f), Unit.PIXELS);
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

    private Label getFileName() {
        Label lblName = new Label(file.getName());
        lblName.addStyleName(ValoTheme.LABEL_BOLD);
        lblName.addStyleName("noselect");

        return lblName;
    }

    private String getNumberOfElementsAndFileSize() {
        long fileSize = file.length();
        String fileSizeDisplay = FileUtils.byteCountToDisplaySize(fileSize);
        String elementos = (file.isDirectory()
                ? String.valueOf(file.list().length == 0
                        ? "" : file.list().length) + (file.list().length > 1
                        ? " elementos" : file.list().length == 0
                                ? "vacío" : " elemento")
                : fileSizeDisplay);

        return elementos;
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

    public MenuBar createButtonContextMenu() {
        return new ButtonContextMenu(downloadInvisibleButton, file, viewLogicFile, viewLogicDirectory);
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        browserResized();
    }

    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
//            table.setColumnCollapsingAllowed(true);
            if (table.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    private void browserResized() {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : DEFAULT_COLLAPSIBLE) {
                table.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
    
}
