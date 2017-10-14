/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component.contextmenu;

import com.mx.zoom.filebox.component.ConfirmWindow;
import com.mx.zoom.filebox.component.DirectoryTreeFolderWindow;
import com.mx.zoom.filebox.component.EditWindow;
import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import javax.imageio.ImageIO;

/**
 *
 * @author Edrd
 */
public class ButtonContextMenu extends MenuBar {

    public ButtonContextMenu(Button dwnldInvisibleBtn, File file, ScheduleFileLogic viewLogicFile, ScheduleDirectoryLogic viewLogicDirectory) {
        addStyleName(ValoTheme.MENUBAR_SMALL);
        addStyleName("btn-contextmenu");

        MenuBar.MenuItem menu = addItem("", FontAwesome.ELLIPSIS_H, null);
//        //DESCARGAR
        MenuBar.MenuItem descargar = menu.addItem("Descargar", FontAwesome.DOWNLOAD, e -> {
            //Path source = Paths.get(file.getAbsolutePath());
            if (file.isDirectory()) {
                viewLogicDirectory.downloadDirectory(file, dwnldInvisibleBtn);
            } else {
                viewLogicFile.downloadFile(file, dwnldInvisibleBtn);
            }
        });
        //EDITAR
        MenuBar.MenuItem editar = menu.addItem("Editar", FontAwesome.PENCIL, e -> {
            EditWindow editWindow = new EditWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = editWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        //BORRAR
        MenuBar.MenuItem borrar = menu.addItem("Eliminar", FontAwesome.TRASH, e -> {
            ConfirmWindow confirmWindow = new ConfirmWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = confirmWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });

        menu.addSeparator();
        //MOVER-COPIAR
        MenuBar.MenuItem moverCopiar = menu.addItem("Mover o Copiar", FontAwesome.COPY, e -> {
            DirectoryTreeFolderWindow directoryTreeWindow = new DirectoryTreeFolderWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = directoryTreeWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        //ZIP
        MenuBar.MenuItem zip = menu.addItem("Zip", FontAwesome.FOURSQUARE, e -> {
            Path source = Paths.get(file.getAbsolutePath());
            if (file.isDirectory()) {
                viewLogicDirectory.zipDirectory(source, file);
            } else {
                viewLogicFile.zipFile(source, file);
            }
        });

    }

}
