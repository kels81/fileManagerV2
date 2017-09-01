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
public enum FileFormats {

    WORD(new String[]{"doc", "docx", "dot", "dotx", "docm", "dotm", "rtf"}),
    EXCEL(new String[]{"xls", "xlsx", "xlsm", "xlsb"}),
    POWERPOINT(new String[]{"ppt", "pptx", "pps", "ppsx"}),
    VISIO(new String[]{"vsd", "vdx", "vss", "vsx", "vst", "vtx", "vsdx", "vdw"}),
    PROJECT(new String[]{"mpp", "mpt"}),
    IMAGEN(new String[]{"bmp", "gif", "jpg", "png", "tiff", "multi-page tiff", "webp"}),
    CODIGO(new String[]{"html", "mht", "mhtml", "xml"}),
    AUDIO(new String[]{"mp3", "aac", "ogg", "wma", "flac", "alac", "wma"}),
    VIDEO(new String[]{"avi", "asf", "mov", "qt", "avchd", "flv", "swf", "mpg", "mpeg", "mp4", "mpeg4", "wmv", "mkv"}),
    TEXTO(new String[]{"txt", "csv"}),
    COMPRIMIR(new String[]{"rar", "zip", "7z"}),
    PDF(new String[]{"pdf"});

    private final String[] arrayFileFormats;

    private FileFormats(String[] arrayFileFormats) {
        this.arrayFileFormats = arrayFileFormats;
    }

    public String[] getArrayFileFormats() {
        return arrayFileFormats;
    }

}
