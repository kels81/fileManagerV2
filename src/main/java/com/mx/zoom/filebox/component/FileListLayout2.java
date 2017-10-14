/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.component.contextmenu.FileContextMenu;
import com.google.common.eventbus.Subscribe;
import com.mx.zoom.filebox.event.DashboardEvent.BrowserResizeEvent;
import com.mx.zoom.filebox.event.DashboardEventBus;
import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.GridContextMenu;
import com.vaadin.addon.contextmenu.GridContextMenu.GridContextMenuOpenListener.GridContextMenuOpenEvent;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.FocusPreserveExtension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Edrd
 */
//public class FileListLayout extends Table {
public class FileListLayout2 extends VerticalLayout implements View {

    private File file;
    private ThemeResource iconResource;
    private Image icon;
    private final Components component = new Components();

    Object selectedItemInTheRow;

    private IndexedContainer idxCont;
    //private Table table;
    private Grid grid;

    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    private final String COL_FILE = "file";
    private final String COL_ICON = "icon";
    private final String COL_NOMBRE = "nombre";
    private final String COL_TAMANIO = "tamanio";
    private final String COL_MODIFICADO = "modificado";
    private final String COL_CONTEXT_MENU = "contextMenu";

    public final Object[] COLUMNS_VISIBLES = {COL_ICON, COL_NOMBRE, COL_TAMANIO, COL_MODIFICADO};
    public final String[] COLUMNS_HEADERS = {"", "Nombre", "Tamaño", "Modificado"};
    public final String[] DEFAULT_COLLAPSIBLE = {COL_TAMANIO, COL_MODIFICADO};

    public FileListLayout2(ScheduleFileLogic mosaicoFileLogic, ScheduleDirectoryLogic mosaicoDirectoryLogic, File file) {
        this.viewLogicFile = mosaicoFileLogic;
        this.viewLogicDirectory = mosaicoDirectoryLogic;

        setSizeFull();
        addStyleName("listView");
        DashboardEventBus.register(this);   //NECESARIO PARA CONOCER LA ORIENTACION Y RESIZE DEL BROWSER

        addComponent(buildGrid(file));
        browserResized();
        System.out.println("width-->" + Page.getCurrent().getBrowserWindowWidth());
        System.out.println("height-->" + Page.getCurrent().getBrowserWindowHeight());
    }

    //METODO NECESARIO PARA CONOCER LA ORIENTACION Y RESIZE DEL BROWSER
    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        DashboardEventBus.unregister(this);
    }

    private Grid buildGrid(File file) {
        grid = new Grid();
        grid.setContainerDataSource(crearContenedor(file));
        grid.setSizeFull();
        grid.setHeightByRows(11);
        grid.setHeightMode(HeightMode.ROW);
        grid.setImmediate(true);
        grid.setSelectionMode(SelectionMode.SINGLE);
//        grid.addStyleName(ValoTheme.TABLE_BORDERLESS);
//        grid.addStyleName(ValoTheme.TABLE_NO_STRIPES);
//        grid.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
//        grid.addStyleName(ValoTheme.TABLE_SMALL);
        grid.addStyleName("noselect");

        grid.setColumns(COLUMNS_VISIBLES);
        setColumnHeaders();

        grid.setEditorEnabled(false);

        //PARA HACER RESPONSIVO LA TABLA
        grid.setColumnReorderingAllowed(false);
        //table.setColumnCollapsible(COL_NOMBRE, false);

        //grid.getColumn(COL_ICON).setExpandRatio(1);
        grid.getColumn(COL_NOMBRE).setExpandRatio(6);
        grid.getColumn(COL_TAMANIO).setExpandRatio(2);
        grid.getColumn(COL_MODIFICADO).setExpandRatio(2);

        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.getItem() != null) {
                    File file = new File(event.getItem().getItemProperty(COL_FILE).getValue().toString());
                    if (event.isDoubleClick()) {
                        if (file.isDirectory()) {
                            viewLogicFile.cleanAndDisplay(file);
                        } else if (file.isFile()) {
                            Notification.show("Ver archivo: " + file.getName());
                            //downloadContents(file);
//                        Window w = new ViewerWindow(file);;
//                        UI.getCurrent().addWindow(w);
//                        w.focus();
                        }
                    }
                }

            }
        });

        grid.getColumn(COL_ICON).setRenderer(new com.vaadin.ui.renderers.ImageRenderer());
//        grid.getColumn(COL_CONTEXT_MENU).setRenderer(new ButtonRenderer(e ->
//            Notification.show("Clicked " + grid.getContainerDataSource()
//                .getContainerProperty(e.getItemId(), COL_NOMBRE))));

        return grid;
    }

    private void setColumnHeaders() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        int i = 0;
        for (Object pid : COLUMNS_VISIBLES) {
            map.put(pid.toString(), COLUMNS_HEADERS[i]);
            i++;
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Grid.Column bornColumn = grid.getColumn(entry.getKey());
            bornColumn.setHeaderCaption(entry.getValue());
        }

//        HeaderRow mainHeader = grid.getDefaultHeaderRow();
//        mainHeader.getCell(COL_ICON).setText("");
//        mainHeader.getCell(COL_NOMBRE).setText("Nombres");
//        mainHeader.getCell(COL_TAMANIO).setText("Tamaños");
//        mainHeader.getCell(COL_MODIFICADO).setText("Modificados");
    }

    private IndexedContainer crearContenedor(File directory) {
        idxCont = new IndexedContainer();

        idxCont.addContainerProperty(COL_ICON, Resource.class, "");
        idxCont.addContainerProperty(COL_NOMBRE, String.class, "");
        idxCont.addContainerProperty(COL_TAMANIO, String.class, "");
        idxCont.addContainerProperty(COL_MODIFICADO, String.class, "");
//        idxCont.addContainerProperty(COL_CONTEXT_MENU, String.class, "");
        idxCont.addContainerProperty(COL_FILE, File.class, "");

        List<File> files = component.directoryContents(directory);

        if (!files.isEmpty()) {
            for (File fileRow : files) {
                this.file = fileRow;

                Item item = idxCont.getItem(idxCont.addItem());
                item.getItemProperty(COL_ICON).setValue(getIconExtension());
                item.getItemProperty(COL_NOMBRE).setValue(file.getName());
                item.getItemProperty(COL_TAMANIO).setValue(getNumberOfElementsAndFileSize());
                item.getItemProperty(COL_MODIFICADO).setValue(getAtributos());
//                item.getItemProperty(COL_CONTEXT_MENU).setValue("holaaaasss");
                item.getItemProperty(COL_FILE).setValue(file);
            }
        }

        GridContextMenu contextMenu = new GridContextMenu(grid);
        contextMenu.addGridHeaderContextMenuListener(e -> {
            contextMenu.removeItems();     //PARA NO MOSTRAR CONTEXT MENU EN HEADERS
        });
        contextMenu.addGridBodyContextMenuListener(e -> {
            contextMenu.removeItems();
            new FileContextMenu().getFileContextMenu(contextMenu, getFileSelected(e), viewLogicFile, viewLogicDirectory);

        });

        return idxCont;
    }

    private File getFileSelected(GridContextMenuOpenEvent e) {
        return new File(grid.getContainerDataSource().getContainerProperty(e.getItemId(), COL_FILE).getValue().toString());
    }

    private ThemeResource getIconExtension() {

        String extension = FilenameUtils.getExtension(file.getPath()).toLowerCase();
        if (file.isDirectory()) {
            iconResource = new ThemeResource("img/file_manager/small/folder_" + (file.list().length == 0 ? "empty" : "full") + ".png");
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

        return new ThemeResource("img/file_manager/small/" + formato + ".png");
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

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        browserResized();
    }

    private void browserResized() {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        for (String propertyId : DEFAULT_COLLAPSIBLE) {
            Grid.Column column = grid.getColumn(propertyId);
            column.setHidden(Page.getCurrent().getBrowserWindowWidth() < 800);
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
