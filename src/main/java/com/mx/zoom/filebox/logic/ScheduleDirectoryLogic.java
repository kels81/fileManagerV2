/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.logic;

import com.mx.zoom.filebox.utils.Notifications;
import com.mx.zoom.filebox.view.schedule.FilesView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Edrd
 */
public class ScheduleDirectoryLogic {

    private final FilesView view;

    private final Notifications notification = new Notifications();

    public ScheduleDirectoryLogic(FilesView view) {
        this.view = view;
    }

    public void moveDirectory(Path sourceDir, Path targetDir, File directory) {
        try {
            //Files.move(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);  //REEMPLAZAR EXISTENTE
            System.out.println("move directory");
            Files.move(sourceDir, targetDir);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se movio el archivo correctamente: " + directory.getName());
        } catch (FileAlreadyExistsException ex) {
            notification.createFailure("Ya existe un archivo con el mismo nombre en esta carpeta");
        } catch (IOException ex) {
            notification.createFailure("Problemas al mover el archivo");
        }
    }

    public void copyDirectory(Path sourceDir, Path targetDir, File directory) {
        System.out.println("copy directory");
        try {
            Files.copy(sourceDir, targetDir);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se copio el archivo correctamente: " + directory.getName());
        } catch (FileAlreadyExistsException ex) {
            notification.createFailure("Ya existe un archivo con el mismo nombre en esta carpeta");
        } catch (IOException ex) {
            notification.createFailure("Problemas al copiar el archivo");
        }
    }

    public void deleteDirectory(Path sourceDir, File directory) {
        System.out.println("delete directory");
        try {
            FileUtils.deleteDirectory(directory);
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se eliminó el archivo correctamente: " + directory.getName());
        } catch (IOException ex) {
            notification.createFailure("No se elimino el archivo");
        }
    }

    public void renameDirectory(Path sourceDir, File oldDirectory, File newDirectory) {
        System.out.println("rename directory");
        try {
            oldDirectory.renameTo(newDirectory);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se renombró el archivo correctamente: " + oldDirectory.getName());
        } catch (Exception ex) {
            notification.createFailure("No se renombró el archivo");
        }
    }

    public void cleanAndDisplay(File file) {
        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        this.view.cleanAndBuild(file);
        this.view.displaySubDirectoryContents(file);
    }

    public void displaySubdirectory(File file) {
        this.view.displaySubDirectoryContents(file);
    }

}
