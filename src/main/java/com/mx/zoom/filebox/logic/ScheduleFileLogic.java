/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.logic;

import com.mx.zoom.filebox.utils.Notifications;
import com.mx.zoom.filebox.view.schedule.FilesView;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.themes.ValoTheme;
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
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;

/**
 *
 * @author Edrd
 */
public class ScheduleFileLogic implements Serializable {

    private final FilesView view;

    private final Notifications notification = new Notifications();
    private final ProgressBar progressBar = new ProgressBar(0.0f);

    public ScheduleFileLogic(FilesView view) {
        this.view = view;
    }
    
    public void downloadFile(File file, Button dwnldInvisibleBtn) {
        FileDownloader fileDownloader;
            if (!dwnldInvisibleBtn.getExtensions().isEmpty()) {
                fileDownloader = (FileDownloader) dwnldInvisibleBtn.getExtensions().toArray()[0];
                if (dwnldInvisibleBtn.getExtensions().contains(fileDownloader)) {
                    dwnldInvisibleBtn.removeExtension(fileDownloader);
                }
            }
            fileDownloader = new FileDownloader(new FileResource(file));
            fileDownloader.extend(dwnldInvisibleBtn);
            Page.getCurrent().getJavaScript().execute("document.getElementById('DownloadButtonId').click();");
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

    public void zipFile(Path sourceFile, File fileToZip) {
        
        try {
            // Create the ZIP file
            String nameFile = FilenameUtils.getBaseName(fileToZip.getName());
            String zipFileName = nameFile + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(fileToZip.getParent() + "\\" + zipFileName));

            //for (int i = 0; i < source.length; i++) {
            FileInputStream in = new FileInputStream(fileToZip);

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(fileToZip.getName()));

            // Transfer bytes from the file to the ZIP file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            // Complete the entry
            out.closeEntry();
            in.close();
            //}

            // Complete the ZIP file
            out.close();

            System.out.println("Done");

            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceFile.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se comprimio el archivo correctamente: " + fileToZip.getName());
        } catch (IOException ex) {
            notification.createFailure("No se comprimio el archivo");
        }
    }
    
    public Plupload uploadFile(File directory) {

        String uploadPath = directory.getAbsolutePath();
        Plupload uploader = new Plupload("Cargar", FontAwesome.UPLOAD);
        //uploader.addFilter(new PluploadFilter("music", "mp3, flac"));
        uploader.setPreventDuplicates(true);
        uploader.addStyleName(ValoTheme.BUTTON_PRIMARY);
        uploader.addStyleName(ValoTheme.BUTTON_SMALL);
        uploader.setUploadPath(uploadPath);
        uploader.setMaxFileSize("15mb");

//show notification after file is uploaded
        uploader.addFileUploadedListener(new Plupload.FileUploadedListener() {
            @Override
            public void onFileUploaded(PluploadFile file) {

                /**
                 * CAMBIAR EL NOMBRE DEL ARCHIVO QUE SE SUBE, YA QUE NO RESPETA
                 * EL NOMBRE DEL ARCHIVO ORIGINAL
                 */
                File uploadedFile = (File) file.getUploadedFile();
                // NOMBRE CORRECTO
                String realName = file.getName();
                System.out.println("realName = " + realName);
                // NOMBRE INCORRECTO
                String falseName = uploadedFile.getName();
                // PATH DEL ARCHIVO
                String pathFile = uploadedFile.getAbsolutePath();
                pathFile = pathFile.substring(0, pathFile.lastIndexOf("\\"));
                System.out.println("pathFile = " + pathFile);
                // SE CREAN LOS OBJETIPOS DE TIPO FILE DE CADA UNO
                File fileFalse = new File(pathFile + "\\" + falseName);
                File fileReal = new File(pathFile + "\\" + realName);
                // SE REALIZA EL CAMBIO DE NOMBRE DEL ARCHIVO
                boolean cambio = fileFalse.renameTo(fileReal);

                // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
                cleanAndDisplay(new File(uploadPath));
                notification.createSuccess("Se cargó el archivo: " + file.getName());
            }
        });

//update upload progress
        uploader.addUploadProgressListener(new Plupload.UploadProgressListener() {
            @Override
            public void onUploadProgress(PluploadFile file) {

                progressBar.setWidth("128px");
                //progressBar.setStyleName(ValoTheme.PROGRESSBAR_POINT);
                progressBar.setVisible(true);

                progressBar.setValue(new Long(file.getPercent()).floatValue() / 100);
                progressBar.setDescription(file.getPercent() + "%");

                System.out.println("I'm uploading " + file.getName()
                        + "and I'm at " + file.getPercent() + "%");
            }
        });

//autostart the uploader after addind files
        uploader.addFilesAddedListener(new Plupload.FilesAddedListener() {
            @Override
            public void onFilesAdded(PluploadFile[] files) {
                progressBar.setValue(0f);
                progressBar.setVisible(true);
                uploader.start();
            }
        });

//notify, when the upload process is completed
        uploader.addUploadCompleteListener(new Plupload.UploadCompleteListener() {
            @Override
            public void onUploadComplete() {
                System.out.println("upload is completed!");
            }
        });

//handle errors
        uploader.addErrorListener(new Plupload.ErrorListener() {
            @Override
            public void onError(PluploadError error) {
                Notification.show("There was an error: "
                        + error.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });

        return uploader;
    }

    public void cleanAndDisplay(File file) {
        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        this.view.cleanAndDisplay(file);
    }

}
