/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.utils;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eduardo
 */
public class Components {

    public TextField createTextField1(String caption) {
        TextField txt = new TextField(caption);
        txt.setNullRepresentation("");
        txt.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        return txt;
    }

    public DateField createDateField(String caption) {
        DateField date = new DateField(caption);
        date.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        date.setValidationVisible(true);
        date.setInvalidAllowed(false);
        date.setParseErrorMessage("Fecha incorrecta");
        return date;
    }

    public Button createButtonPrimary1(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        return btn;
    }

    public Button createButtonNormal1(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        return btn;
    }

    public ComboBox createComboBox1(String caption) {
        ComboBox cmb = new ComboBox(caption);
        cmb.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        cmb.setPageLength(0);
        cmb.setImmediate(true);
        cmb.setTextInputAllowed(false);
        cmb.setNullSelectionAllowed(false);
        cmb.setInputPrompt("Elegir " + caption.toLowerCase());
        cmb.addStyleName(ValoTheme.COMBOBOX_SMALL);
        return cmb;
    }

    public MenuBar createMenuBar1() {
        MenuBar menu = new MenuBar();
        menu.addStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        return menu;
    }
    
    public CheckBox createCheckBox(String caption) {
        CheckBox cb = new CheckBox(caption);
        cb.setImmediate(true);
        return cb;
    }

    public TextField createTextField(String caption) {
        TextField f = new TextField(caption);
        f.setNullRepresentation("");
        //f.addFocusListener(focusListener);
        //f.addBlurListener(blurListener);
        return f;
    }

    public Button createButtonPrimary(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        //btnFolder.setEnabled(false);
        return btn;
    }
    
    public Button createButtonIconTiny() {
        Button btn = new Button();
        btn.addStyleName(ValoTheme.BUTTON_TINY);
        btn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        //btn.addStyleName("color1");    //like primary
        //btnFolder.setEnabled(false);
        return btn;
    }
    
    public Button createButtonNormal(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        btn.addStyleName("mybutton");
        //btnFolder.setEnabled(false);
        return btn;
    }

    public Button createButtonPath(String caption) {
        Button btn = new Button(caption);
        btn.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        btn.addStyleName(ValoTheme.BUTTON_SMALL);
        //btnFolder.setEnabled(false);
        return btn;
    }

    public MenuBar createMenuBar() {
        MenuBar menu = new MenuBar();
        menu.addStyleName(ValoTheme.MENUBAR_SMALL);
        menu.addStyleName("color1");    //like primary
        return menu;
    }
    
    public List<File> directoryContents(File directory) {
        // ARRAY QUE VA A ACONTENER TODOS LOS ARCHIVOS ORDENADOS POR TIPO Y ALFABETICAMENTE
        List<File> allDocsLst = new ArrayList<>();
        File[] files = directory.listFiles();
        List<File> fileLst = new ArrayList<>();
        List<File> directoryLst = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                directoryLst.add(file);
                //directoryContents(file);   //para conocer los archivos de las subcarpetas
            } else {
                fileLst.add(file);
            }
        }
        allDocsLst.addAll(directoryLst);
        allDocsLst.addAll(fileLst);

        return allDocsLst;
    }
    
    public List<File> directoryFolderContents(File directory) {
        // ARRAY QUE VA A ACONTENER TODOS LOS ARCHIVOS ORDENADOS POR TIPO Y ALFABETICAMENTE
        List<File> allDocsLst = new ArrayList<>();
        File[] files = directory.listFiles();
        List<File> fileLst = new ArrayList<>();
        List<File> directoryLst = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                directoryLst.add(file);
                //directoryContents(file);   //para conocer los archivos de las subcarpetas
            } else {
                fileLst.add(file);
            }
        }
        allDocsLst.addAll(directoryLst);
        allDocsLst.addAll(fileLst);

        return allDocsLst;
    }

}
