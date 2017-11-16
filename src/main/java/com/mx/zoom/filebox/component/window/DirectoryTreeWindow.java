/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component.window;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.mx.zoom.filebox.logic.EmailAttachedLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.Constantes;
import com.mx.zoom.filebox.utils.Notifications;
import com.vaadin.event.Action;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.List;

/**
 *
 * @author Edrd
 */
public class DirectoryTreeWindow extends Window {

    private final File origenPath;
    private final VerticalLayout content;
    private HorizontalLayout root;
    private Tree tree;
    private File rootDir;
    private final TabSheet detailsWrapper;
    private HierarchicalContainer container;

    private final Components component = new Components();
    private final Notifications notification = new Notifications();

    private final EmailAttachedLogic viewLogic;

    public DirectoryTreeWindow(EmailAttachedLogic emailAttachedLogic) {
        viewLogic = emailAttachedLogic;

        this.origenPath = new File(Constantes.ROOT_PATH);

        Responsive.makeResponsive(this);

        addStyleName("directorywindow");
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
        detailsWrapper.addComponent(tree());

        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);
        content.addComponent(buildFooter());

        setContent(content);
    }

    private HorizontalLayout tree() {
        root = new HorizontalLayout();
        root.setCaption(Constantes.ROOT_DIRECTORY);
        root.setMargin(true);

        Container generateContainer = getDirectoryContainer(origenPath);
        tree = new Tree();
        tree.setContainerDataSource(generateContainer);
        tree.setItemCaptionPropertyId("caption");
        tree.setItemIconPropertyId("icon");
        tree.setImmediate(true);
        tree.setSelectable(false);
        tree.addActionHandler(new Action.Handler() {
            private final Action ADJUNTAR = new Action(FontAwesome.PAPERCLIP.getHtml() + " Adjuntar");
            private final Action[] ACTIONS = new Action[]{ADJUNTAR};

            @Override
            public Action[] getActions(Object target, Object sender) {
                return ACTIONS;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                //Notification.show(action.getCaption());
                Item item = tree.getItem(target);
                //VALIDACION PARA ADJUNTAR SOLAMENTE ARCHIVOS NO CARPETAS
                if (action == ADJUNTAR && item.getItemProperty("type").getValue().equals("file")) {
                    buildAttachedFile(item);
                } else {
                    notification.createFailure("No se pueden adjuntar carpetas.");  //PARA QUE SE VEA ESTE MENSAJE, SETSELECTABLE = TRUE
                }
            }
        });
        tree.addExpandListener(new Tree.ExpandListener() {
            @Override
            public void nodeExpand(Tree.ExpandEvent event) {
                String directory = event.getItemId().toString();
                //ESTA VALIDACION ES PARA CUANDO SE QUIERE MOSTRAR CARPETA ROOT Y CARPETAS 
                //DENTRO DE ELLA 1ER NIVEL
                if (!directory.equals(Constantes.ROOT_PATH)) {
                    createTreeContent(new File(directory), event);
                }
            }
        });
        tree.addCollapseListener(new Tree.CollapseListener() {
            @Override
            public void nodeCollapse(Tree.CollapseEvent event) {
                // Remove all children of the collapsing node
                Object children[] = tree.getChildren(event.getItemId()).toArray();
                for (Object childrenItem : children) {
                    tree.collapseItemsRecursively(childrenItem);
                    //tree.collapseItem(childrenItem);
                }
            }
        });
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                Object itemId = event.getItemId();
                //if (event.getItem().getItemProperty("type").getValue().equals("file")) {
                tree.select(event.getItemId());
                //}
                //VALIDACION PARA EXPANDIR NODE DESDE EL LABEL
                if (event.isDoubleClick()) {
                    //Notification.show("789797_"+event.getItem().getItemProperty("caption").getValue());
                    if (tree.isExpanded(itemId)) {
                        tree.collapseItem(itemId);
                    } else {
                        tree.expandItem(itemId);
                    }
                }
            }
        });

        // Expand all items that can be
        for (Object itemId : container.getItemIds()) {
            //ESTA VALIDACION ES PARA CUANDO SE QUIERE MOSTRAR CARPETA ROOT Y CARPETAS 
            //DENTRO DE ELLA 1ER NIVEL
            if (itemId.toString().equals(Constantes.ROOT_PATH)) {
                tree.expandItem(itemId);
            }
        }

        root.addComponent(tree);

        return root;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setHeight(30.0f, Unit.PIXELS);

        Button btnCerrar = component.createButtonNormal("Cerrar");
        btnCerrar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        //footer.addComponent(btnCerrar);
        //footer.setComponentAlignment(btnCerrar, Alignment.TOP_RIGHT);
        return footer;
    }

    public HierarchicalContainer getDirectoryContainer(File directory) {

        // Create new container
        container = new HierarchicalContainer();
        // Create containerproperty for name
        container.addContainerProperty("icon", ThemeResource.class, null);
        container.addContainerProperty("caption", String.class, null);
        container.addContainerProperty("path", String.class, null);
        container.addContainerProperty("type", String.class, null);

        createTreeContent(directory, null);

        return container;
    }

    private void createTreeContent(File directory, Tree.ExpandEvent event) {

        if (event == null) {
            rootDir = directory;
            // Create an item
            Item item = container.addItem(rootDir);
            item.getItemProperty("caption").setValue(rootDir.getName());
            item.getItemProperty("icon").setValue(new ThemeResource("img/file_manager/folder_24.png"));
            item.getItemProperty("path").setValue(rootDir.getAbsolutePath());
            item.getItemProperty("type").setValue("folder");
        }

        List<File> files = component.directoryContents(directory);

        if (!files.isEmpty()) {
            for (File file : files) {
                String source = file.isDirectory() ? "folder" : "file";

                container.addItem(file);
                container.getItem(file).getItemProperty("caption").setValue(file.getName());
                container.getItem(file).getItemProperty("icon").setValue(new ThemeResource("img/file_manager/" + source + "_24.png"));
                container.getItem(file).getItemProperty("path").setValue(file.getAbsolutePath());
                container.getItem(file).getItemProperty("type").setValue(source);

                container.setParent(file, event != null ? event.getItemId() : rootDir);

                // SI SE ENCUENTRA VACIA LA CARPETA, NO MOSTRARA LA FLECHA DE EXPANDIR
                if (file.isFile() || (file.isDirectory() && file.list().length == 0)) {
                    container.setChildrenAllowed(file, false);
                }
            }
        }
    }

    private void buildAttachedFile(Item item) {
        String path = item.getItemProperty("path").getValue().toString();
        String name = path.substring(path.lastIndexOf('\\') + 1);

        HorizontalLayout adjLayout = new HorizontalLayout();
        adjLayout.addStyleName("attachedlayout");

        Label pathFile = new Label(path);
        pathFile.addStyleName(ValoTheme.LABEL_TINY);

        Label nameFile = new Label("&nbsp;" + FontAwesome.FILE_TEXT_O.getHtml() + "&nbsp;" + name + "&nbsp;");
        nameFile.setContentMode(ContentMode.HTML);
        nameFile.addStyleName(ValoTheme.LABEL_LIGHT);
        nameFile.addStyleName(ValoTheme.LABEL_SMALL);

        Button delete = new Button("×");
        delete.setDescription("Eliminar");
        delete.setPrimaryStyleName("deleteBtn");
        delete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                removeAttachedFIle(adjLayout, pathFile);
            }
        });

        adjLayout.addComponents(nameFile, delete);
        addAttachedFile(adjLayout, pathFile, name);
    }

    private void addAttachedFile(Component attachedFile, Label pathFile, String name) {
        // NOMBRE DE ARCHIVOS QUE VE EL USAURIO
        viewLogic.addAttachedFile(attachedFile);
        // LA RUTA DE LOS ARCHIVOS QUE VE EL USUARIO, PERO QUE SE ENCUENTRA OCULTO
        viewLogic.addAttachedFileHidden(pathFile);
        notification.createSuccess("Se adjunto el archivo: " + name);
    }

    private void removeAttachedFIle(Component attachedFile, Label pathFile) {
        // ELIMINAR LABEL DEL ARCHIVO QUE VE EL USUARIO
        viewLogic.removeAttachedFile(attachedFile);
        // ELIMINAR TAMBIEN EL PATH DEL ARCHIVO QUE SE ENCUENTRA OCULTO
        // EL QUE SE MANDA POR CORREO
        viewLogic.removeAttachedFileHidden(pathFile);
    }
}
