/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.Menu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Edrd
 */
public class FileGridLayout extends CssLayout implements LayoutClickListener {

    private VerticalLayout frame;
    private HorizontalLayout mosaicoLayout;
    private ThemeResource iconResource;
    private Image icon;
    private VerticalLayout fileDetails;
    private final File file;

    private Label fileName;
    private Label numberOfElementsAndFileSize;
    
//    private Button fileName;
//    private Button numberOfElementsAndFileSize;

    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    public FileGridLayout(ScheduleFileLogic mosaicoFileLogic, ScheduleDirectoryLogic mosaicoDirectoryLogic, File file) {
        super();
        this.viewLogicFile = mosaicoFileLogic;
        this.viewLogicDirectory = mosaicoDirectoryLogic;
        this.file = file;
        
        addStyleName("mainPanel");
        addComponent(buildFrame());
    }

    private VerticalLayout buildFrame() {
        frame = new VerticalLayout();
        frame.addStyleName("frame");
        frame.setMargin(true);
        frame.addStyleName(ValoTheme.LAYOUT_CARD);
        frame.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        frame.addLayoutClickListener(this);
        frame.addComponent(buildMosaico());

        ContextMenu contextMenu = new ContextMenu(this, false);
        createMenu(contextMenu, file);
        contextMenu.setAsContextMenuOf(frame);
        return frame;
    }

    private HorizontalLayout buildMosaico() {
        mosaicoLayout = new HorizontalLayout();
        //mosaicoLayout.setSpacing(true);
        //mosaicoLayout.setDescription(file.getName());

        Component icon = buildIcon();
        Component details = buildFileDetails();
        mosaicoLayout.addComponent(icon);
        mosaicoLayout.addComponent(details);
        mosaicoLayout.setExpandRatio(icon, 0.30f);
        mosaicoLayout.setExpandRatio(details, 0.70f);

        return mosaicoLayout;
    }

    private Image buildIcon() {
        icon = new Image(null, getIconExtension());
//        icon.setWidth(50.0f, Unit.PIXELS);
//        icon.setHeight(55.0f, Unit.PIXELS);
        icon.setWidth(55.0f, Unit.PIXELS);
        icon.setHeight(60.0f, Unit.PIXELS);
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

    private VerticalLayout buildFileDetails() {
        Component fileName = getFileName();
        Component numberOfElements = getNumberOfElementsAndFileSize();

        fileDetails = new VerticalLayout();
        fileDetails.addComponent(fileName);
        fileDetails.setComponentAlignment(fileName, Alignment.BOTTOM_LEFT);
        fileDetails.addComponent(numberOfElements);
        fileDetails.setComponentAlignment(numberOfElements, Alignment.BOTTOM_LEFT);
        return fileDetails;
    }

    private Label getFileName() {
        String name = file.getName();
        name = (name.length() > 18 ? name.substring(0, 16) + "..." : name);
        fileName = new Label(name);
        fileName.addStyleName("labelInfo");
        fileName.addStyleName("noselect");
        fileName.addStyleName(ValoTheme.LABEL_BOLD);
        //fileName.setReadOnly(true);
        
//        fileName = new Button(name);
//        fileName.addStyleName(ValoTheme.BUTTON_SMALL);
//        fileName.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        
        return fileName;
    }

    private Label getNumberOfElementsAndFileSize() {
        long fileSize = file.length();
        String fileSizeDisplay = FileUtils.byteCountToDisplaySize(fileSize);
        String label = (file.isDirectory()
                ? String.valueOf(file.list().length == 0
                        ? "" : file.list().length) + (file.list().length > 1
                        ? " elementos" : file.list().length == 0
                                ? "vacío" : " elemento")
                : fileSizeDisplay);

        numberOfElementsAndFileSize = new Label(label);
        numberOfElementsAndFileSize.addStyleName("labelInfo");
        numberOfElementsAndFileSize.addStyleName("noselect");
        numberOfElementsAndFileSize.addStyleName(ValoTheme.LABEL_TINY);
        //numberOfElementsAndFileSize.setReadOnly(true);
        
//        numberOfElementsAndFileSize = new Button(label);
//        numberOfElementsAndFileSize.addStyleName(ValoTheme.BUTTON_TINY);
//        numberOfElementsAndFileSize.addStyleName(ValoTheme.BUTTON_BORDERLESS);

        return numberOfElementsAndFileSize;
    }

    private void createMenu(ContextMenu menu, File file) {
        //DESCARGAR
        MenuItem descargar = menu.addItem("Descargar", e -> {
            Notification.show("descargar");
            //Window w = createWindowEdit(file);
            //UI.getCurrent().addWindow(w);
            //w.focus();
        });
        descargar.setIcon(FontAwesome.DOWNLOAD);
        //EDITAR
        MenuItem editar = menu.addItem("Editar", e -> {
            EditWindow editWindow = new EditWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = editWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        editar.setIcon(FontAwesome.PENCIL);
        //BORRAR
        MenuItem borrar = menu.addItem("Eliminar", new Menu.Command() {
            @Override
            public void menuSelected(MenuItem e) {
                ConfirmWindow confirmWindow = new ConfirmWindow(viewLogicFile, viewLogicDirectory, file);
                Window w = confirmWindow;
                UI.getCurrent().addWindow(w);
                w.focus();
            }
        });
        borrar.setIcon(FontAwesome.TRASH);

        // SEPARADOR
        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).addSeparator();
        }
        //MOVER-COPIAR
        MenuItem moverCopiar = menu.addItem("Mover o Copiar", e -> {
            DirectoryTreeFolderWindow directoryTreeWindow = new DirectoryTreeFolderWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = directoryTreeWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        moverCopiar.setIcon(FontAwesome.COPY);
        //ZIP
        MenuItem zip = menu.addItem("Zip", e -> {
            Path source = Paths.get(file.getAbsolutePath());

            if (file.isDirectory()) {
                viewLogicDirectory.zipDirectory(source, file);
            } else {
                viewLogicFile.zipFile(source, file);
            }

        });
        zip.setIcon(FontAwesome.FOURSQUARE);

//        MenuItem item6 = menu.addItem("Submenu", e -> {
//        });
//        item6.addItem("Subitem", e -> Notification.show("SubItem"));
//        item6.addSeparator();
//        item6.addItem("Subitem", e -> Notification.show("SubItem"))
//                .setDescription("Test");
    }

    private void downloadContents(File file) {
        FileResource resPath = new FileResource(new File(file.getAbsolutePath()));
        /*
                            FileDownloader fileDownloader = new FileDownloader(resPath);
                            fileDownloader.markAsDirty();
                            fileDownloader.extend(frame);
         */
        Page.getCurrent().open(resPath, null, false);
    }

    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        //if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
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
