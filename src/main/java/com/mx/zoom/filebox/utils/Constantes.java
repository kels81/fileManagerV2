/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.utils;

/**
 *
 * @author Edrd
 */
public class Constantes {

    //CARPETA DE ARCHIVOS PC
    public static final String ROOT_PATH = "C:\\Users\\ecortesh\\Documents\\GitHub\\fileManagerV2\\Archivos";
    public static final String SEPARADOR = "\\\\";
    public static final String ROOT_DIRECTORY = getRootDirectory();
    public static final String PATH_BASE = getPathBase();
    public static final String OUTPUT_PDF = "C:\\Test\\";
    
    public static final int OPENOFFICE_PORT = 8100;
    public static final String OPENOFFICE_HOST = "localhost";

    private static String getRootDirectory() {
        String[] directories = ROOT_PATH.split(SEPARADOR);
        return directories[directories.length - 1];
    }
    
    private static String getPathBase() {
        return ROOT_PATH.substring(0, ROOT_PATH.indexOf(ROOT_DIRECTORY));
    }

}
