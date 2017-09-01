/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.logic;

import com.mx.zoom.filebox.utils.Notifications;
import com.mx.zoom.filebox.view.schedule.FilesView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Edrd
 */
public class ScheduleFileLogic implements Serializable {

    private final FilesView view;

    private final Notifications notification = new Notifications();

    public ScheduleFileLogic(FilesView view) {
        this.view = view;
    }

    public void moveFile(Path sourceDir, Path targetDir, File file) {
        try {
            //Files.move(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);  //REEMPLAZAR EXISTENTE
            Files.move(sourceDir, targetDir);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se movio el archivo correctamente: " + file.getName());
        } catch (FileAlreadyExistsException ex) {
            notification.createFailure("Ya existe un archivo con el mismo nombre en esta carpeta");
        } catch (IOException ex) {
            notification.createFailure("Problemas al mover el archivo");
        }
    }

    public void copyFile(Path sourceDir, Path targetDir, File file) {
        try {
            Files.copy(sourceDir, targetDir);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se copio el archivo correctamente: " + file.getName());
        } catch (FileAlreadyExistsException ex) {
            notification.createFailure("Ya existe un archivo con el mismo nombre en esta carpeta");
        } catch (IOException ex) {
            notification.createFailure("Problemas al copiar el archivo");
        }
    }

    public void deleteFile(Path sourceDir, File file) {
        try {
            Files.delete(sourceDir);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se eliminó el archivo correctamente: " + file.getName());
        } catch (IOException ex) {
            notification.createFailure("No se elimino el archivo");
        }
    }

    public void renameFile(Path sourceDir, File oldFile, File newFile) {
        try {
            oldFile.renameTo(newFile);
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se renombró el archivo correctamente: " + oldFile.getName());
        } catch (Exception ex) {
            notification.createFailure("No se renombró el archivo");
        }
    }

    public void zipFile(Path sourceDir, File inputFile) {
        byte[] buf = new byte[1024];
        try {
            // Create the ZIP file
            String nameFile = FilenameUtils.getBaseName(inputFile.getName());
            String fileZip = nameFile + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(sourceDir.getParent().toString() + "\\" + fileZip));

            //for (int i = 0; i < source.length; i++) {
            FileInputStream in = new FileInputStream(inputFile);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(inputFile.getName()));

            // Transfer bytes from the file to the ZIP file
            int bytesRead;
            while ((bytesRead = in.read(buf)) > 0) {
                out.write(buf, 0, bytesRead);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
            //}

            // Complete the ZIP file
            out.close();

            System.out.println("Done");

            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se comprimio el archivo correctamente: " + inputFile.getName());
        } catch (IOException ex) {
            notification.createFailure("No se comprimio el archivo");
        }
    }

    public void createFolder(Path sourceDir, String name) {
        File directory = new File(sourceDir + "\\" + name);
        directory.mkdir();

        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        String dir = sourceDir.toString();
        cleanAndDisplay(new File(dir));
        notification.createSuccess("Se cargó con éxito");
    }
    
    
    
 //DIRECTORY METHODS
//    public void moveDirectory(Path sourceDir, Path targetDir, File file) {
//        try {
//            //Files.move(sourceDir, targetDir, StandardCopyOption.REPLACE_EXISTING);  //REEMPLAZAR EXISTENTE
//            Files.move(sourceDir, targetDir);
//            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
//            String dir = sourceDir.getParent().toString();
//            cleanAndDisplay(new File(dir));
//            notification.createSuccess("Se movio el archivo correctamente: " + file.getName());
//        } catch (FileAlreadyExistsException ex) {
//            notification.createFailure("Ya existe un archivo con el mismo nombre en esta carpeta");
//        } catch (IOException ex) {
//            notification.createFailure("Problemas al mover el archivo");
//        }
//    }
//    
//    public void renameDirectory(Path sourceDir, File oldFile, File newFile) {
//        System.out.println("rename directory");
//        try {
//            oldFile.renameTo(newFile);
//            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
//            String dir = sourceDir.getParent().toString();
//            cleanAndDisplay(new File(dir));
//            notification.createSuccess("Se renombró el archivo correctamente: " + oldFile.getName());
//        } catch (Exception ex) {
//            notification.createFailure("No se renombró el archivo");
//        }
//    }
    
    

    public void cleanAndDisplay(File file) {
        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        this.view.cleanAndBuild(file);
        this.view.displaySubDirectoryContents(file);
    }
    
    public void displaySubdirectory(File file) {
        this.view.displaySubDirectoryContents(file);
    }

}
