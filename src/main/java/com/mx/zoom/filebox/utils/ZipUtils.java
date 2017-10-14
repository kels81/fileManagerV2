/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Edrd
 */
public class ZipUtils {

    public static void unZipFolder(String zipFilePath, String outputPath) throws IOException {

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry;

        String zipEntryName;

        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            zipEntryName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                zipEntryName = zipEntryName.substring(0, zipEntryName.length() - 1);
                File folder = new File(outputPath + File.separator + zipEntryName);
                folder.mkdirs();
            } else {
                File file = new File(outputPath + File.separator + zipEntryName);
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = zipInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            }
        }

        zipInputStream.close();
    }

    public static Boolean zipDirectory(Path tempDirectory, File directoryToZip)  {
            Boolean result;
        try {
            String zipDirectoryName = directoryToZip.getName() + ".zip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tempDirectory + "\\" + zipDirectoryName));

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
            
            result = Boolean.TRUE;
        } catch (IOException e) {
            result = Boolean.FALSE;
        }
        
        return result;
    }

    public static void zipFolder(File inputFolder, String parentName, ZipOutputStream zipOutputStream) throws IOException {
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

    public static void zipFile(File inputFile, String parentName, ZipOutputStream zipOutputStream) throws IOException {
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
}
