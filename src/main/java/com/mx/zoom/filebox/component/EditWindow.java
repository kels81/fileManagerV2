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
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Edrd
 */
public class EditWindow extends Window {

    private final VerticalLayout content;
    private VerticalLayout body;
    private HorizontalLayout footer;
    private TextField txtEditName;

    private Button btnCancelar;
    private Button btnGuardar;

    private final Components component = new Components();
    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    private final TabSheet detailsWrapper;

    public EditWindow(ScheduleFileLogic editFileLogic, ScheduleDirectoryLogic editDirectoryLogic,  File file) {
        this.viewLogicFile = editFileLogic;
        this.viewLogicDirectory = editDirectoryLogic;

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
        body.setCaption("Renombrar");
        body.setSizeFull();
        body.setSpacing(true);
        body.setMargin(true);

        txtEditName = new TextField();
        txtEditName.focus();
        txtEditName.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        txtEditName.setValue(FilenameUtils.getBaseName(file.getName()));    //Para mostrar solamente el nombre del archivo sin la extensión
        txtEditName.setInputPrompt("Nuevo nombre del archivo");
        txtEditName.setTextChangeEventMode(AbstractTextField.TextChangeEventMode.EAGER);          //EAGER, Para que evento no sea lento
        txtEditName.setTextChangeTimeout(200);
        txtEditName.setImmediate(true);
        txtEditName.addTextChangeListener((FieldEvents.TextChangeEvent event) -> {
            btnGuardar.setEnabled(StringUtils.isNotBlank(event.getText()));
        });

        body.addComponent(txtEditName);
        body.setComponentAlignment(txtEditName, Alignment.MIDDLE_CENTER);

        return body;
    }

    private Component buildFooter(File file) {
        footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        btnCancelar = component.createButtonNormal("Cancelar");
        btnCancelar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        btnCancelar.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        btnGuardar = component.createButtonPrimary("Guardar");
        btnGuardar.addClickListener((Button.ClickEvent event) -> {
            Path source = Paths.get(file.getAbsolutePath());
            String newName = txtEditName.getValue().trim();

            File newFile = null;
            if (file.isDirectory()) {
                newFile = new File(source.getParent().toString() + "\\" + newName);
                viewLogicDirectory.renameDirectory(source, file, newFile);
            } else {
                //DOCUMENTO
                String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
                newFile = new File(source.getParent().toString() + "\\" + newName + "." + extension);
                viewLogicFile.renameFile(source, file, newFile);
            }
            
            close();
        });
        btnGuardar.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(btnCancelar, btnGuardar);
        footer.setExpandRatio(btnCancelar, 1);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);

        return footer;
    }

}
