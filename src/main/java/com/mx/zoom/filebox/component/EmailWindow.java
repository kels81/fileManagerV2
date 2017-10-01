/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component;

//import com.vaadin.addon.contextmenu.ContextMenu;
//import com.vaadin.addon.contextmenu.MenuItem;
//import com.vaadin.addon.contextmenu.Menu;
import com.mx.zoom.filebox.logic.EmailAttachedLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.Constantes;
import com.mx.zoom.filebox.utils.Mail;
import com.mx.zoom.filebox.utils.Notifications;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Edrd
 */
public class EmailWindow extends Window implements View {

    private final File origenPath;
    private final VerticalLayout content;
    private VerticalLayout filesAttached;
    private VerticalLayout root;
    public HorizontalLayout attachedLayout;
    public CssLayout adjuntar;
    public FormLayout form;
    private final TabSheet detailsWrapper;
    private final Components component = new Components();

    private Button btnAdjuntar;
    private Button btnCancelar;
    private Button btnEnviar;
    private AddressEditor txtPara;
    private TextField txtAsunto;
    private RichTextArea cuerpoCorreo;
    private final Notifications notification = new Notifications();
    //private final ContextMenu contextMenu = new ContextMenu(this, false);
    
    private final EmailAttachedLogic viewLogic = new EmailAttachedLogic(this);

    public EmailWindow() {
        this.origenPath = new File(Constantes.ROOT_PATH);

        addStyleName("emailwindow");
        Responsive.makeResponsive(this);
        setModal(true);
        setResizable(false);
        setClosable(true);
        center();

        setHeight(90.0f, Unit.PERCENTAGE);

        content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));

        detailsWrapper = new TabSheet();
        detailsWrapper.setSizeFull();
        detailsWrapper.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        detailsWrapper.addComponent(fields());

        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);
        content.addComponent(buildFooter());

        setContent(content);
    }

    private VerticalLayout fields() {
        root = new VerticalLayout();
        root.setCaption("Email");
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("email-form");

        attachedLayout = new HorizontalLayout();
        attachedLayout.setSpacing(true);
        attachedLayout.setWidth(100.0f, Unit.PERCENTAGE);

        adjuntar = new CssLayout();
        //adjuntar.setStyleName("attachedfiles");

        filesAttached = new VerticalLayout();
        filesAttached.setVisible(false);

        btnAdjuntar = component.createButtonNormal("Adjuntar");
        btnAdjuntar.setIcon(FontAwesome.PAPERCLIP);
        btnAdjuntar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                DirectoryTreeWindow directoryTreeWindow = new DirectoryTreeWindow(viewLogic);
                Window w = directoryTreeWindow;
                UI.getCurrent().addWindow(w);
                w.focus();
            }
        });

        attachedLayout.addComponents(btnAdjuntar, adjuntar);
        attachedLayout.setExpandRatio(adjuntar, 1.0f);

        txtPara = new AddressEditor();
        txtPara.setCaption("Para");
        txtPara.setWidth(100.0f, Unit.PERCENTAGE);

//        paraTxt.addTextChangeListener(new FieldEvents.TextChangeListener() {
//            @Override
//            public void textChange(FieldEvents.TextChangeEvent event) {
//                String texto = event.getText();
//                if (texto.contains("@")) {
//                    paraTxt.setValue(texto+"; ");
//                }
//            }
//        });
        txtAsunto = component.createTextField("Asunto");
        txtAsunto.setWidth(100.0f, Unit.PERCENTAGE);

        cuerpoCorreo = new RichTextArea();
        cuerpoCorreo.setHeight(100.0f, Unit.PERCENTAGE);
        cuerpoCorreo.setWidth(100.0f, Unit.PERCENTAGE);

        form = new FormLayout();
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        form.addComponents(txtPara, txtAsunto);

        root.addComponent(form);
        root.addComponent(attachedLayout);
        root.addComponent(filesAttached);
        root.addComponent(cuerpoCorreo);

        //Page.getCurrent().getStyles().add(".v-verticallayout {border: 1px solid blue;} .v-verticallayout .v-slot {border: 1px solid red;}");
        return root;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);

        btnCancelar = component.createButtonNormal("Cancelar");
        btnCancelar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

        btnEnviar = component.createButtonPrimary("Enviar");
        btnEnviar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //System.out.println("para: " + paraTxt.getValue());
                //boolean enviar = (StringUtils.isNotBlank(paraTxt.getValue()) && StringUtils.isNotBlank(asuntoTxt.getValue()) && StringUtils.isNotBlank(cuerpoCorreo.getValue()));
                boolean enviar = (StringUtils.isNotBlank(txtPara.getValue().toString()) && StringUtils.isNotBlank(txtAsunto.getValue()) && StringUtils.isNotBlank(cuerpoCorreo.getValue()));
                if (enviar) {
                    Mail sendMail = new Mail();

                    String asunto = txtAsunto.getValue().trim();
                    String mensaje = cuerpoCorreo.getValue();
                    //List<String> receptores = new ArrayList<String>();
                    //receptores.add(paraTxt.getValue());

                    String receptores = txtPara.getValue().toString().trim();
                    System.out.println("receptores = " + receptores);

                    List<String> adjuntos = new ArrayList<>();
                    for (int i = 0; i < filesAttached.getComponentCount(); i++) {
                        Component c = filesAttached.getComponent(i);
                        adjuntos.add(filesAttached.getComponent(i).toString());
                    }

                    boolean envio = sendMail.enviarSpring(asunto, receptores, mensaje, adjuntos);
                    System.out.println("envio = " + envio);

                    String message = envio ? "Se envío con Éxito" : "Problemas con el envío";
                    notification.createSuccess(message);

                    close();
                } else {
                    notification.createFailure("Favor de revisar los campos");
                }
            }
        });

        footer.addComponents(btnCancelar, btnEnviar);
        footer.setExpandRatio(btnCancelar, 1.0f);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);
        return footer;
    }


    public void addAttachedFile(Component attachedFile) {
        adjuntar.addComponent(attachedFile);
    }
    
    public void addAttachedFileHidden(Component attachedFile) {
        filesAttached.addComponent(attachedFile);
    }
    
    public void removeAttachedFile(Component attachedFile) {
        adjuntar.removeComponent(attachedFile);
    }
    
    public void removeAttachedFileHidden(Component attachedFile) {
        filesAttached.removeComponent(attachedFile);
    }
    
    @Override
    public void enter(final ViewChangeEvent event) {
    }
    
}
