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
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.Menu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Edrd
 */
public class FileContextMenu {
    
    public void getFileContextMenu(Menu menu, File file, ScheduleFileLogic viewLogicFile, ScheduleDirectoryLogic viewLogicDirectory) {
        //DESCARGAR
        MenuItem descargar = menu.addItem("Descargar", FontAwesome.DOWNLOAD, e -> {
            Notification.show("descargar");
            //Window w = createWindowEdit(file);
            //UI.getCurrent().addWindow(w);
            //w.focus();
        });
        //EDITAR
        MenuItem editar = menu.addItem("Editar", FontAwesome.PENCIL, e -> {
            EditWindow editWindow = new EditWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = editWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        //BORRAR
        MenuItem borrar = menu.addItem("Eliminar", FontAwesome.TRASH, new Menu.Command() {
            @Override
            public void menuSelected(MenuItem e) {
                ConfirmWindow confirmWindow = new ConfirmWindow(viewLogicFile, viewLogicDirectory, file);
                Window w = confirmWindow;
                UI.getCurrent().addWindow(w);
                w.focus();
            }
        });
        // SEPARADOR
        if (menu instanceof ContextMenu) {
            ((ContextMenu) menu).addSeparator();
        }
        //MOVER-COPIAR
        MenuItem moverCopiar = menu.addItem("Mover o Copiar", FontAwesome.COPY, e -> {
            DirectoryTreeFolderWindow directoryTreeWindow = new DirectoryTreeFolderWindow(viewLogicFile, viewLogicDirectory, file);
            Window w = directoryTreeWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });
        //ZIP
        MenuItem zip = menu.addItem("Zip", FontAwesome.FOURSQUARE, e -> {
            Path source = Paths.get(file.getAbsolutePath());

            if (file.isDirectory()) {
                viewLogicDirectory.zipDirectory(source, file);
            } else {
                viewLogicFile.zipFile(source, file);
            }

        });

//        MenuItem item6 = menu.addItem("Submenu", e -> {
//        });
//        item6.addItem("Subitem", e -> Notification.show("SubItem"));
//        item6.addSeparator();
//        item6.addItem("Subitem", e -> Notification.show("SubItem"))
//                .setDescription("Test");
    }


    
}
