/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.beans;

import com.vaadin.ui.Image;

/**
 *
 * @author Edrd
 */
public class FileBean {
    
    private String name;
    private String modified;
    private String size;
    private Image icon;

    public FileBean(String name, String modified, String size) {
        this.name = name;
        this.modified = modified;
        this.size = size;
        //this.icon = icon;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }
    
    
    
}
