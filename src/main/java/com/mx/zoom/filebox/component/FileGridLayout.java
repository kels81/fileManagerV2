/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.component.contextmenu.ButtonContextMenu;
import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.FileFormats;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
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
//public class FileGridLayout extends Panel implements LayoutClickListener {

    private HorizontalLayout frame;
    private HorizontalLayout mosaicoLayout;
    private ThemeResource iconResource;
    private Image icon;
    private VerticalLayout fileDetails;
    private File file;
    private CssLayout mainPanel;

    private Label fileName;
    private Label numberOfElementsAndFileSize;

    private final int widthPage;
    private final Button downloadInvisibleButton = new Button();
    private final Components component = new Components();

    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    public FileGridLayout(ScheduleFileLogic mosaicoFileLogic, ScheduleDirectoryLogic mosaicoDirectoryLogic, File file) {
        super();
        this.viewLogicFile = mosaicoFileLogic;
        this.viewLogicDirectory = mosaicoDirectoryLogic;
        this.widthPage = Page.getCurrent().getBrowserWindowWidth();

        addStyleName("gridView");
        setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        Responsive.makeResponsive(this);

        File currentDir = new File(file.getAbsolutePath());
        List<File> files = (List<File>) component.directoryContents(currentDir);

        for (File fileComp : files) {
            this.file = fileComp;
            addComponent(buildGrid());
        }

        System.out.println("width-->" + Page.getCurrent().getBrowserWindowWidth());
        System.out.println("height-->" + Page.getCurrent().getBrowserWindowHeight());

        //BUTTON PARA PODER DESCARGAR ARCHIVOS POR MEDIO DEL CONTEXT MENU
        downloadInvisibleButton.setId("DownloadButtonId");
        downloadInvisibleButton.addStyleName("InvisibleButton");
        addComponent(downloadInvisibleButton);
    }

    private Component buildGrid() {
        mainPanel = new CssLayout();
        mainPanel.addStyleName("mainPanel");
        mainPanel.addComponent(buildFrame());

        return mainPanel;
    }

    private HorizontalLayout buildFrame() {
        frame = new HorizontalLayout();
        frame.addStyleName("frame");
        frame.setMargin(true);
        frame.addStyleName(ValoTheme.LAYOUT_CARD);
        frame.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        frame.addLayoutClickListener(this);
        frame.addComponent(buildMosaico());

        MenuBar btnContextMenu = new ButtonContextMenu(downloadInvisibleButton, file, viewLogicFile, viewLogicDirectory);
        frame.addComponent(btnContextMenu);
        frame.setComponentAlignment(btnContextMenu, Alignment.TOP_RIGHT);

//        ContextMenu contextMenu = new ContextMenu(this, false);
//        new FileContextMenu().getFileContextMenu(contextMenu, file, viewLogicFile, viewLogicDirectory);
//        contextMenu.setAsContextMenuOf(frame);
        return frame;
    }

    private HorizontalLayout buildMosaico() {
        mosaicoLayout = new HorizontalLayout();
        //mosaicoLayout.setSpacing(true);
        //mosaicoLayout.setDescription(file.getName());

        Component icon = buildIcon();
        Component details = buildFileDetails();
        mosaicoLayout.addComponent(icon);
        mosaicoLayout.setComponentAlignment(icon, Alignment.MIDDLE_CENTER);
        mosaicoLayout.addComponent(details);
        mosaicoLayout.setExpandRatio(icon, 0.30f);
        mosaicoLayout.setExpandRatio(details, 0.70f);

        return mosaicoLayout;
    }

    private Image buildIcon() {
        icon = new Image(null, getIconExtension());
        icon.setWidth((file.isDirectory() ? 48.0f : 40.0f), Unit.PIXELS);
        icon.setHeight((file.isDirectory() ? 43.0f : 49.0f), Unit.PIXELS);
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
        name = (widthPage >= 1200 && widthPage < 1300 ? (name.length() > 19 ? name.substring(0, 16) + "..." : name)
                : widthPage > 1300 ? (name.length() > 20 ? name.substring(0, 17) + "..." : name) : name);
        fileName = new Label(name);
        fileName.addStyleName("labelName");
        fileName.addStyleName("noselect");
        fileName.addStyleName(ValoTheme.LABEL_BOLD);

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

        return numberOfElementsAndFileSize;
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
