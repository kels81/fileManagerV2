/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.logic;

import com.mx.zoom.filebox.component.window.EmailWindow;
import com.vaadin.ui.Component;
import java.io.Serializable;

/**
 *
 * @author Edrd
 */
public class EmailAttachedLogic implements Serializable {
    
    private final EmailWindow view;

    public EmailAttachedLogic(EmailWindow emailWindowView) {
        view = emailWindowView;
    }
    
    public void addAttachedFile(Component component) {
        view.addAttachedFile(component);
    }
    
    public void addAttachedFileHidden(Component component) {
        view.addAttachedFileHidden(component);
    }
    
    public void removeAttachedFile(Component component) {
        view.removeAttachedFile(component);
    }
    
    public void removeAttachedFileHidden(Component component) {
        view.removeAttachedFileHidden(component);
    }
    
    
    
}
