/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Edrd
 */
public class NewFolderWindow extends Window {

    private final VerticalLayout content;
    private VerticalLayout body;
    private HorizontalLayout footer;
    private TextField txtNameFolder;

    private Button btnCrear;

    private final Components component = new Components();
    private final ScheduleDirectoryLogic viewLogicDirectory;

    private final TabSheet detailsWrapper;

    public NewFolderWindow(ScheduleDirectoryLogic moveCopyFileLogic, File file) {
        viewLogicDirectory = moveCopyFileLogic;

        addStyleName("createfolder-window");
        Responsive.makeResponsive(this);

        setModal(true);
        setResizable(false);
        setClosable(true);
        setHeight(90.0f, Sizeable.Unit.PERCENTAGE);

        content = new VerticalLayout();
        content.setSizeFull();

        detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addComponent(body(file));
        
        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);
        content.addComponent(buildFooter(file));

        setContent(content);
    }

    private Component body(File file) {
        body = new VerticalLayout();
        body.setCaption("Carpeta");
        body.setSizeFull();
        body.setSpacing(true);
        body.setMargin(true);

        txtNameFolder = new TextField();
        txtNameFolder.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        txtNameFolder.setInputPrompt("Escriba el nombre de carpeta");
        txtNameFolder.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);          //EAGER, Para que evento no sea lento
        txtNameFolder.setTextChangeTimeout(200);                                  //Duración para iniciar el evento
        txtNameFolder.setImmediate(true);
        txtNameFolder.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            btnCrear.setEnabled(StringUtils.isNotBlank(event.getText()));
        });
        txtNameFolder.focus();

        body.addComponent(txtNameFolder);
        body.setComponentAlignment(txtNameFolder, Alignment.MIDDLE_CENTER);

        return body;
    }

    private Component buildFooter(File file) {
        footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        btnCrear = component.createButtonPrimary("Crear");
        btnCrear.focus();
        btnCrear.addStyleName(ValoTheme.BUTTON_PRIMARY);
        btnCrear.setEnabled(false);
        btnCrear.addClickListener((ClickEvent event) -> {

            viewLogicDirectory.createFolder(file.toPath(), txtNameFolder.getValue().trim());
            close();
        });
        btnCrear.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponent(btnCrear);
        footer.setComponentAlignment(btnCrear, Alignment.TOP_RIGHT);

        return footer;
    }

}
