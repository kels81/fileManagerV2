/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Edrd
 */
public class ConfirmWindow extends Window {

    private final VerticalLayout content;
    private VerticalLayout body;
    private HorizontalLayout footer;

    private Button btnCancelar;
    private Button btnAceptar;

    private final Components component = new Components();
    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    private final TabSheet detailsWrapper;

    public ConfirmWindow(ScheduleFileLogic deleteFileLogic, ScheduleDirectoryLogic deleteDirectoryLogic, File file) {
        this.viewLogicFile = deleteFileLogic;
        this.viewLogicDirectory = deleteDirectoryLogic;

        addStyleName("confirm-window");
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
        detailsWrapper.addComponent(body());

        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);
        content.addComponent(buildFooter(file));

        setContent(content);
        //Page.getCurrent().getStyles().add(".v-verticallayout {border: 1px solid blue;} .v-verticallayout .v-slot {border: 1px solid red;}");
    }

    private Component body() {
        body = new VerticalLayout();
        body.setCaption("Confirmar");
        //body.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        body.setSizeFull();
        body.setMargin(true);

        Label messageLbl = new Label("¿Está seguro de que desea eliminar este archivo?");
        //messageLbl.addStyleName(ValoTheme.LABEL_LIGHT);
        //messageLbl.addStyleName(ValoTheme.LABEL_H4);

        body.addComponent(messageLbl);
        body.setComponentAlignment(messageLbl, Alignment.MIDDLE_LEFT);

        return body;
    }

    private Component buildFooter(File file) {
        footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);

        btnCancelar = component.createButtonNormal("Cancelar");
        btnCancelar.addClickListener((ClickEvent event) -> {
            close();
        });
        btnCancelar.setClickShortcut(ShortcutAction.KeyCode.ESCAPE, null);

        btnAceptar = component.createButtonPrimary("Aceptar");
        btnAceptar.focus();
        btnAceptar.addClickListener((ClickEvent event) -> {
            Path source = Paths.get(file.getAbsolutePath());

            if (file.isDirectory()) {
                viewLogicDirectory.deleteDirectory(source, file);
            } else {
                viewLogicFile.deleteFile(source, file);
            }
            close();
        });
        btnAceptar.setClickShortcut(ShortcutAction.KeyCode.ENTER, null);

        footer.addComponents(btnCancelar, btnAceptar);
        footer.setExpandRatio(btnCancelar, 1);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);

        return footer;
    }
}
