/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.logic;

import com.mx.zoom.filebox.utils.Notifications;
import com.mx.zoom.filebox.utils.ZipUtils;
import com.mx.zoom.filebox.view.schedule.FilesView;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Edrd
 */
public class ScheduleDirectoryLogic {

    private final FilesView view;

    private final Notifications notification = new Notifications();

    private final List<String> filesListInDir = new ArrayList<>();

    public ScheduleDirectoryLogic(FilesView view) {
        this.view = view;
    }

    public void downloadDirectory(File directory, Button dwnldInvisibleBtn) {
        try {
            File tmp = Files.createTempDirectory("zip").toFile();
            Path sourceTemp = Paths.get(tmp.getAbsolutePath());
            Boolean result = ZipUtils.zipDirectory(sourceTemp, directory);
            System.out.println("result = " + result);

            FileDownloader fileDownloader;
            if (!dwnldInvisibleBtn.getExtensions().isEmpty()) {
                fileDownloader = (FileDownloader) dwnldInvisibleBtn.getExtensions().toArray()[0];
                if (dwnldInvisibleBtn.getExtensions().contains(fileDownloader)) {
                    dwnldInvisibleBtn.removeExtension(fileDownloader);
                    File[] files = tmp.getParentFile().listFiles();
                    if (files.length != 0) {
                        for (File file : files) {
                            if (!tmp.getName().equals(file.getName())) {
                                FileUtils.deleteDirectory(file);
                            }
                        }
                    }
                }
            }
            String zipFile = tmp.getPath().concat(File.separator).concat(directory.getName().concat(".zip"));
            fileDownloader = new FileDownloader(new FileResource(new File(zipFile)));
            fileDownloader.extend(dwnldInvisibleBtn);
            Page.getCurrent().getJavaScript().execute("document.getElementById('DownloadButtonId').click();");
            //FileUtils.deleteDirectory(tmp);
        } catch (IOException ex) {
            Logger.getLogger(ScheduleDirectoryLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
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
            //FileUtils.copyDirectoryToDirectory(sourceDir.toFile(), targetDir.toFile());
            FileUtils.copyDirectory(sourceDir.toFile(), targetDir.toFile());
            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDir.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se copio el archivo correctamente: " + directory.getName());
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

//    public void zipDirectory(Path sourceDirectory, File directoryToZip) {
//        try {
//            Path source = Paths.get(directoryToZip.getParent());
//            Boolean result = ZipUtils.zipDirectory(source, directoryToZip);
//            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
//            String dir = sourceDirectory.getParent().toString();
//            cleanAndDisplay(new File(dir));
//            if (result) {
//                notification.createSuccess("Se comprimio el archivo correctamente: " + directoryToZip.getName());
//            } else {
//                notification.createFailure("No se comprimio el archivo");
//            }
//        } catch (Exception e) {
//        }
//
//    }
    public void zipDirectory(Path sourceDirectory, File directoryToZip) {
        try {
            String zipDirectoryName = directoryToZip.getName() + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(directoryToZip.getParent() + "\\" + zipDirectoryName));

            String directoryName = directoryToZip.getName() + File.separator;

            File[] files = directoryToZip.listFiles();
            if (files.length != 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        zipFile(file, directoryName, out);
                    } else if (file.isDirectory()) {
                        zipFolder(file, directoryName, out);
                    }
                }
            } else {
                //PARA CUANDO SE QUIERE COMPRIMIR UNA CARPETA VACIA
                out.putNextEntry(new ZipEntry((directoryName.substring(0, directoryName.length() - 1)) + "/"));
                out.closeEntry();
            }
            out.close();

            System.out.println("Done");

            // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
            String dir = sourceDirectory.getParent().toString();
            cleanAndDisplay(new File(dir));
            notification.createSuccess("Se comprimio el archivo correctamente: " + directoryToZip.getName());
        } catch (IOException e) {
            notification.createFailure("No se comprimio el archivo");
        }
    }

    public void zipFolder(File inputFolder, String parentName, ZipOutputStream zipOutputStream) throws IOException {
        String directoryName = parentName + inputFolder.getName() + "\\";

        File[] contents = inputFolder.listFiles();
        if (contents.length != 0) {
            for (File file : contents) {
                if (file.isFile()) {
                    zipFile(file, directoryName, zipOutputStream);
                } else if (file.isDirectory()) {
                    zipFolder(file, directoryName, zipOutputStream);
                }
            }
        } else {
            //PARA AGREGAR AL ZIP UNA CARPETA VACIA
            System.out.println("carpeta vacia: " + (directoryName.substring(0, directoryName.length() - 1)) + "/");
            zipOutputStream.putNextEntry(new ZipEntry((directoryName.substring(0, directoryName.length() - 1)) + "/"));
        }
        zipOutputStream.closeEntry();
    }

    public void zipFile(File inputFile, String parentName, ZipOutputStream zipOutputStream) throws IOException {
        // A ZipEntry represents a file entry in the zip archive
        // We name the ZipEntry after the original file's name
        ZipEntry zipEntry = new ZipEntry(parentName + inputFile.getName());
        zipOutputStream.putNextEntry(zipEntry);

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        byte[] buffer = new byte[1024];
        int bytesRead;

        // Read the input file by chucks of 1024 bytes
        // and write the read bytes to the zip stream
        while ((bytesRead = fileInputStream.read(buffer)) > 0) {
            zipOutputStream.write(buffer, 0, bytesRead);
        }

        // close ZipEntry to store the stream to the file
        zipOutputStream.closeEntry();

        System.out.println("Regular file :" + parentName + inputFile.getName() + " is zipped to archive :" + zipOutputStream.toString());
    }

    public void createFolder(Path sourceDir, String name) {
        File directory = new File(sourceDir + "\\" + name);
        directory.mkdir();

        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        String dir = sourceDir.toString();
        cleanAndDisplay(new File(dir));
        notification.createSuccess("Se cargó con éxito");
    }

    public void cleanAndDisplay(File file) {
        // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
        this.view.cleanAndDisplay(file);
    }

}
