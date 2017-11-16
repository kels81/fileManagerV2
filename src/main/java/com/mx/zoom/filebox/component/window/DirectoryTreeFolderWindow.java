/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mx.zoom.filebox.component.window;

import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.Constantes;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
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
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.filefilter.DirectoryFileFilter;

/**
 *
 * @author Edrd
 */
public class DirectoryTreeFolderWindow extends Window {

    private final File origenPath;
    private final VerticalLayout content;
    private VerticalLayout body;
    private VerticalLayout root;
    private HorizontalLayout footer;
    private Tree tree;
    private File rootDir;
    private final TabSheet detailsWrapper;
    private HierarchicalContainer container;
    private Label lblFileName;
    private final File fileTo;
    private Button btnCancelar;
    private Button btnMover;
    private Button btnCopiar;

    private final Components component = new Components();
    private final ScheduleFileLogic viewLogicFile;
    private final ScheduleDirectoryLogic viewLogicDirectory;

    public DirectoryTreeFolderWindow(ScheduleFileLogic moveCopyFileLogic, ScheduleDirectoryLogic moveCopyDirectoryLogic, File file) {
        this.viewLogicFile = moveCopyFileLogic;
        this.viewLogicDirectory = moveCopyDirectoryLogic;

        this.origenPath = new File(Constantes.ROOT_PATH);
        this.fileTo = file;

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
        detailsWrapper.addComponent(body());

        content.addComponent(detailsWrapper);
        content.setExpandRatio(detailsWrapper, 1.0f);
        content.addComponent(buildFooter());

        setContent(content);
    }

    private VerticalLayout body() {
        body = new VerticalLayout();
        //body.setCaption("Mover o Copiar" + "  \""+fileTo.getName()+"\"");
        body.setCaption("Mover o Copiar");
        body.setMargin(true);
        body.setSpacing(true);
        body.addComponent(buildFileName());
        body.addComponent(new Label("Selecciona folder destino:"));
        Component tree = buildTree();
        body.addComponent(tree);
        body.setExpandRatio(tree, 1.0f);

        //Page.getCurrent().getStyles().add(".v-verticallayout {border: 1px solid blue;} .v-verticallayout .v-slot {border: 1px solid red;}");
        return body;
    }

    private Label buildFileName() {
        lblFileName = new Label("\"" + fileTo.getName() + "\"");
        lblFileName.addStyleName(ValoTheme.LABEL_COLORED);
        lblFileName.addStyleName(ValoTheme.LABEL_BOLD);
        return lblFileName;
    }

    private Component buildTree() {
        root = new VerticalLayout();

        Container generateContainer = getDirectoryContainer(origenPath);
        tree = new Tree();
        tree.setContainerDataSource(generateContainer);
        tree.setItemCaptionPropertyId("caption");
        tree.setItemIconPropertyId("icon");
        tree.setImmediate(true);
        tree.setSelectable(false);
        tree.addExpandListener(new Tree.ExpandListener() {
            @Override
            public void nodeExpand(Tree.ExpandEvent event) {
                String directory = event.getItemId().toString();
                //ESTA VALIDACION ES PARA CUANDO SE QUIERE MOSTRAR CARPETA ROOT Y CARPETAS 
                //DENTRO DE ELLA 1ER NIVEL
                if (!directory.equals(Constantes.ROOT_PATH)) {
                    //createTreeContent(new File(event.getItemId().toString()), event);
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
                }
            }
        });
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                //Notification.show("6666_" + event.getItem().getItemProperty("caption").getValue());
                Object itemId = event.getItemId();
                tree.select(itemId);
                btnCopiar.setEnabled(true);
                btnMover.setEnabled(true);
                //VALIDACION PARA EXPANDIR NODE DESDE EL LABEL
                if (event.isDoubleClick()) {
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
        footer = new HorizontalLayout();
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

        btnMover = component.createButtonPrimary("Mover");
        btnMover.setEnabled(false);
        btnMover.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Path source = Paths.get(fileTo.getAbsolutePath());
                Path target = Paths.get(tree.getValue().toString() + "\\" + fileTo.getName());

                if (fileTo.isDirectory()) {
                    viewLogicDirectory.moveDirectory(source, target, fileTo);
                } else {
                    //DOCUMENTO
                    viewLogicFile.moveFile(source, target, fileTo);
                }

                close();
            }
        });

        btnCopiar = component.createButtonPrimary("Copiar");
        btnCopiar.setEnabled(false);
        btnCopiar.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Path source = Paths.get(fileTo.getAbsolutePath());
                Path target = Paths.get(tree.getValue().toString() + "\\" + fileTo.getName());

                if (fileTo.isDirectory()) {
                    viewLogicDirectory.copyDirectory(source, target, fileTo);
                } else {
                    viewLogicFile.copyFile(source, target, fileTo);
                }
                close();
            }
        });

        footer.addComponents(btnCancelar, btnMover, btnCopiar);
        footer.setExpandRatio(btnCancelar, 1.0f);
        footer.setComponentAlignment(btnCancelar, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(btnMover, Alignment.TOP_RIGHT);
        footer.setComponentAlignment(btnCopiar, Alignment.TOP_RIGHT);
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

        File[] arrayFiles = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);     //PARA OBTENER UNICAMENTE DIRECTORIOS DE UN DIRECTORIO
        Arrays.sort(arrayFiles);
        //CONVERTIR ARRAY A LIST
        List<File> files = Arrays.asList(arrayFiles);

        if (!files.isEmpty()) {
            for (File file : files) {
                //PARA SABER SI EL DIRECTORIO TIENE ADENTRO OTROS DIRECTORIOS Y PODER MOSTRAR LA FLECHA DE EXPANDIR
                Boolean allow = Boolean.FALSE;
                if (file.isDirectory() && file.list().length != 0) {
                    File[] subDirectory = file.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
                    allow = subDirectory.length != 0;
                }

                container.addItem(file);
                container.getItem(file).getItemProperty("caption").setValue(file.getName());
                container.getItem(file).getItemProperty("icon").setValue(new ThemeResource("img/file_manager/folder_24.png"));
                container.getItem(file).getItemProperty("path").setValue(file.getAbsolutePath());
                container.getItem(file).getItemProperty("type").setValue("folder");
                container.setChildrenAllowed(file, allow);  // SI SE ENCUENTRA VACIA LA CARPETA, NO MOSTRARA LA FLECHA DE EXPANDIR

                container.setParent(file, event != null ? event.getItemId() : rootDir);
            }
        }
    }

}
