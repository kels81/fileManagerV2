package com.mx.zoom.filebox.view.schedule;

import com.mx.zoom.filebox.component.EmailWindow;
import com.mx.zoom.filebox.component.FileGridLayout;
import com.mx.zoom.filebox.component.FileListLayout;
import com.mx.zoom.filebox.component.NewFolderWindow;
import com.mx.zoom.filebox.logic.ScheduleDirectoryLogic;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.Constantes;
import com.mx.zoom.filebox.utils.Notifications;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;

@SuppressWarnings("serial")
public final class FilesView extends VerticalLayout implements View {

    private final File origenPath;
    private HorizontalLayout rootPath;
    private Button btnFolder;
    private Button btnListView;
    private Button btnGridView;
    private Label lblArrow;
    private TextField txtSearch;
    private final Components component = new Components();
    private Component directoryContent;
    private HorizontalLayout toolBar;
    private HorizontalLayout viewBar;
    private HorizontalLayout mainButtons;
    private HorizontalLayout viewButtons;
    private final ProgressBar progressBar = new ProgressBar(0.0f);
    private final Notifications notification = new Notifications();

    private final ScheduleFileLogic viewLogicFile = new ScheduleFileLogic(this);
    private final ScheduleDirectoryLogic viewLogicDirectory = new ScheduleDirectoryLogic(this);

    private Boolean selected = true;

    public FilesView() {
        this.origenPath = new File(Constantes.ROOT_PATH);
        //this.path = VaadinService.getCurrent().getBaseDirectory() + "/VAADIN/themes/UPLOADS/";

        setSizeFull();
        addStyleName("schedule");

        addComponent(buildHeader(origenPath));
        addComponent(buildToolBar(origenPath));
        //addComponent(buildViewsBar(origenPath));

        directoryContent = selectView(selected, origenPath);
        addComponent(directoryContent);
        setExpandRatio(directoryContent, 1);

        //progressBar.setCaption("Progress");
        //addComponent(progressBar);
     
        Responsive.makeResponsive(this);
//        Page.getCurrent().getStyles().add(".v-verticallayout {border: 1px solid blue;} .v-verticallayout .v-slot {border: 1px solid red;}");
    }

    private Component buildHeader(File directory) {
        Component searchBar = buildSearchBar();
        Component mainButtons = buildMainButtons(directory);
        
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        header.addStyleName("viewheader");
        //header.setSpacing(true);
        
        header.addComponents(searchBar, mainButtons);
        //header.setExpandRatio(mainButtons, 1.0f);
        header.setComponentAlignment(mainButtons, Alignment.MIDDLE_RIGHT);
        
        Responsive.makeResponsive(header);

        return header;
    }

    private Component buildSearchBar() {
        txtSearch = new TextField();
        txtSearch.setWidth(45.0f, Sizeable.Unit.PERCENTAGE);
        txtSearch.setInputPrompt("Buscar Archivos y Carpetas");
        txtSearch.setDescription("Buscar");
        txtSearch.setIcon(FontAwesome.SEARCH);
        txtSearch.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        txtSearch.addStyleName(ValoTheme.TEXTFIELD_SMALL);

        return txtSearch;
    }

    private Component buildToolBar(File directory) {
        toolBar = new HorizontalLayout();
        toolBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        toolBar.setSpacing(true);
        toolBar.addStyleName("toolbar");

        Component path = buildPath(directory);
        Component typesViews = buildViewsBar(directory);
        //Component mainButtons = buildMainButtons(directory);

        toolBar.addComponents(path, typesViews);
        toolBar.setExpandRatio(typesViews, 1.0f);
//        toolBar.setExpandRatio(mainButtons, 1.0f);
//        toolBar.setComponentAlignment(mainButtons, Alignment.MIDDLE_RIGHT);

        return toolBar;
    }

    private Component buildViewsBar(File directory) {
        viewBar = new HorizontalLayout();
        viewBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        viewBar.addStyleName("viewbar");

        Component viewsButtons = buildViewsButtons(directory);

        viewBar.addComponent(viewsButtons);
        viewBar.setComponentAlignment(viewsButtons, Alignment.MIDDLE_RIGHT);

        return viewBar;
    }

    private Component buildMainButtons(File directory) {
        mainButtons = new HorizontalLayout();
        mainButtons.setSpacing(true);

        // CARGAR ARCHIVO
        Plupload btnUploader = uploadContents(directory);

        MenuBar menubar = component.createMenuBar();
        MenuBar.MenuItem menu = menubar.addItem("Nuevo", null);
        menu.setIcon(FontAwesome.PLUS);
        menu.addItem("Carpeta", FontAwesome.FOLDER_O, (MenuBar.MenuItem selectedItem) -> {
            NewFolderWindow newFolderWindow = new NewFolderWindow(viewLogicDirectory, directory);
            Window w = newFolderWindow;
            UI.getCurrent().addWindow(w);
            w.focus();
        });

        Button btnEmail = component.createButtonPrimary("Email");
        btnEmail.setIcon(FontAwesome.ENVELOPE_O);
        btnEmail.addClickListener((ClickEvent event) -> {
            EmailWindow emailWdw = new EmailWindow();
            Window w = emailWdw;
            UI.getCurrent().addWindow(w);
            w.focus();
        });

        mainButtons.addComponents(btnUploader, menubar, btnEmail);

        return mainButtons;
    }

    private Component buildViewsButtons(File directory) {
        viewButtons = new HorizontalLayout();
        //viewButtons.setSpacing(true);

        btnListView = component.createButtonIconTiny();
        //btnListView.setIcon(FontAwesome.TH_LIST);
        btnListView.setIcon(FontAwesome.BARS);
        btnListView.addStyleName(setStyle(selected));
        btnListView.setEnabled(selected);
        btnListView.setDescription("Vista Lista");
        btnListView.addClickListener((ClickEvent event) -> {
            selected = false;
            cleanAndDisplay(directory);
        });

        btnGridView = component.createButtonIconTiny();
        btnGridView.setIcon(FontAwesome.TH_LARGE);
        btnGridView.addStyleName(setStyle(!selected));
        btnGridView.setEnabled(!selected);
        btnGridView.setDescription("Vista Grid");
        btnGridView.addClickListener((ClickEvent event) -> {
            selected = true;
            cleanAndDisplay(directory);
        });

//        CssLayout group = new CssLayout(btnListView, btnGridView);
//        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
//        viewButtons.addComponent(group);
        viewButtons.addComponents(btnListView, btnGridView);

        return viewButtons;
    }

    private Component buildPath(File fileDirectory) {
        rootPath = new HorizontalLayout();

        List<File> listDirectories = getListDirectories(fileDirectory);
        int i = 1;
        for (File directory : listDirectories) {
            btnFolder = component.createButtonPath(directory.getName());
            btnFolder.setEnabled((i != listDirectories.size()));
            btnFolder.addStyleName((i == listDirectories.size() ? "labelColored" : ""));
            btnFolder.addClickListener(event -> {
                //System.out.println("evnt: "+event.getComponent().getCaption());
                cleanAndDisplay(directory);
            });
            rootPath.addComponent(btnFolder);
            if (i != listDirectories.size()) {
                lblArrow = new Label(FontAwesome.ANGLE_RIGHT.getHtml(), ContentMode.HTML);
                lblArrow.addStyleName(ValoTheme.LABEL_COLORED);
                rootPath.addComponent(lblArrow);
                rootPath.setComponentAlignment(lblArrow, Alignment.MIDDLE_CENTER);
            }
            i++;
        }
        return rootPath;
    }

    private Component selectView(Boolean selected, File pathDirectory) {
        Component viewSelected = null;
        if (selected) {
            viewSelected = new FileGridLayout(viewLogicFile, viewLogicDirectory, pathDirectory);
        } else {
            viewSelected = new FileListLayout(viewLogicFile, viewLogicDirectory, pathDirectory);
        }

        return viewSelected;
    }

    private List<File> getListDirectories(File directory) {
        List<File> listDirectories = new ArrayList<>();
        String[] arrayDirectories = directory.getPath().split(Constantes.SEPARADOR);
        int idxArchivos = Arrays.asList(arrayDirectories).indexOf(Constantes.ROOT_DIRECTORY);
        String[] newArrayDirectories = Arrays.copyOfRange(arrayDirectories, idxArchivos, arrayDirectories.length);
        StringBuilder newPath = new StringBuilder();
        int i = 1;
        for (String dirName : newArrayDirectories) {
//            int fin = directory.getPath().indexOf(dirName);
//            listDirectories.add(new File(directory.getPath().substring(0, fin + dirName.length())));
            newPath.append(dirName);
            if (i != newArrayDirectories.length) {
                newPath.append("\\");
            }
            listDirectories.add(new File(Constantes.PATH_BASE + newPath.toString()));
            i++;
        }
        System.out.println("newPath = " + newPath.toString());
        return listDirectories;
    }

    public void cleanAndDisplay(File directory) {
        removeAllComponents();
        addComponent(buildHeader(directory));
        addComponent(buildToolBar(directory));
        directoryContent = selectView(selected, directory);
        addComponent(directoryContent);
        setExpandRatio(directoryContent, 1);
    }

    private Plupload uploadContents(File directory) {

        String uploadPath = directory.getAbsolutePath();
        Plupload uploader = new Plupload("Cargar", FontAwesome.UPLOAD);
        //uploader.addFilter(new PluploadFilter("music", "mp3, flac"));
        uploader.setPreventDuplicates(true);
        uploader.addStyleName(ValoTheme.BUTTON_PRIMARY);
        uploader.addStyleName(ValoTheme.BUTTON_SMALL);
        uploader.setUploadPath(uploadPath);
        uploader.setMaxFileSize("15mb");

//show notification after file is uploaded
        uploader.addFileUploadedListener(new Plupload.FileUploadedListener() {
            @Override
            public void onFileUploaded(PluploadFile file) {

                /**
                 * CAMBIAR EL NOMBRE DEL ARCHIVO QUE SE SUBE, YA QUE NO RESPETA
                 * EL NOMBRE DEL ARCHIVO ORIGINAL
                 */
                File uploadedFile = (File) file.getUploadedFile();
                // NOMBRE CORRECTO
                String realName = file.getName();
                System.out.println("realName = " + realName);
                // NOMBRE INCORRECTO
                String falseName = uploadedFile.getName();
                // PATH DEL ARCHIVO
                String pathFile = uploadedFile.getAbsolutePath();
                pathFile = pathFile.substring(0, pathFile.lastIndexOf("\\"));
                System.out.println("pathFile = " + pathFile);
                // SE CREAN LOS OBJETIPOS DE TIPO FILE DE CADA UNO
                File fileFalse = new File(pathFile + "\\" + falseName);
                File fileReal = new File(pathFile + "\\" + realName);
                // SE REALIZA EL CAMBIO DE NOMBRE DEL ARCHIVO
                boolean cambio = fileFalse.renameTo(fileReal);

                // SE RECARGA LA PAGINA, PARA MOSTRAR EL ARCHIVO CARGADO
                cleanAndDisplay(new File(uploadPath));
                notification.createSuccess("Se cargó el archivo: " + file.getName());
            }
        });

//update upload progress
        uploader.addUploadProgressListener(new Plupload.UploadProgressListener() {
            @Override
            public void onUploadProgress(PluploadFile file) {

                progressBar.setWidth("128px");
                //progressBar.setStyleName(ValoTheme.PROGRESSBAR_POINT);
                progressBar.setVisible(true);

                progressBar.setValue(new Long(file.getPercent()).floatValue() / 100);
                progressBar.setDescription(file.getPercent() + "%");

                System.out.println("I'm uploading " + file.getName()
                        + "and I'm at " + file.getPercent() + "%");
            }
        });

//autostart the uploader after addind files
        uploader.addFilesAddedListener(new Plupload.FilesAddedListener() {
            @Override
            public void onFilesAdded(PluploadFile[] files) {
                progressBar.setValue(0f);
                progressBar.setVisible(true);
                uploader.start();
            }
        });

//notify, when the upload process is completed
        uploader.addUploadCompleteListener(new Plupload.UploadCompleteListener() {
            @Override
            public void onUploadComplete() {
                System.out.println("upload is completed!");
            }
        });

//handle errors
        uploader.addErrorListener(new Plupload.ErrorListener() {
            @Override
            public void onError(PluploadError error) {
                Notification.show("There was an error: "
                        + error.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
        });

        return uploader;
    }

    private String setStyle(Boolean selected) {
        return selected ? "borderButton" : "noBorderButton";
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

}
