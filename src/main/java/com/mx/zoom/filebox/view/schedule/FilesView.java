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
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import java.util.List;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;

@SuppressWarnings("serial")
//public final class FilesView extends Panel implements View {
public final class FilesView extends VerticalLayout implements View {

    private File path;
    private final File origenPath;
    private final VerticalLayout content;
    private HorizontalLayout rootPath;
    private Button btnFolder;
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

    private FileGridLayout fileGrid;
    private FileListLayout fileList;

    private final Boolean selected = false;

    private Table table;
    private ThemeResource iconResource;
    private Image icon;

    public FilesView() {
        this.origenPath = new File(Constantes.ROOT_PATH);
        //this.path = VaadinService.getCurrent().getBaseDirectory() + "/VAADIN/themes/UPLOADS/";

        setSizeFull();
        addStyleName("schedule");

        content = new VerticalLayout();
        content.addComponent(buildHeader());
        content.addComponent(buildToolBar(origenPath));
        content.addComponent(buildViewsBar());

        //directoryContent = buildGridView(origenPath);
        directoryContent = selectView(selected, origenPath);
        content.addComponent(directoryContent);
        content.setExpandRatio(directoryContent, 1);

        //progressBar.setCaption("Progress");
        //addComponent(progressBar);
        //[ GRID VIEW ]
        //setContent(content);
        //[ LIST VIEW ]
        addComponent(content);

        Responsive.makeResponsive(content);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout(buildSearchBar());
        header.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        header.addStyleName("viewheader");
        //header.setSpacing(true);
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

        Component path = buildPath();
        Component mainButtons = buildMainButtons(directory);

        toolBar.addComponents(path, mainButtons);
        toolBar.setExpandRatio(mainButtons, 1.0f);
        toolBar.setComponentAlignment(mainButtons, Alignment.MIDDLE_RIGHT);

        return toolBar;
    }

    private Component buildViewsBar() {
        viewBar = new HorizontalLayout();
        viewBar.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        viewBar.addStyleName("viewbar");

        Component viewsButtons = buildViewsButtons();

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
            NewFolderWindow newFolderWindow = new NewFolderWindow(viewLogicFile, directory);
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

    private Component buildViewsButtons() {
        viewButtons = new HorizontalLayout();
        //viewButtons.setSpacing(true);

        Button btnListView = component.createButtonIconTiny();
        btnListView.setIcon(FontAwesome.TH_LIST);
        btnListView.setDescription("Vista Lista");
        btnListView.addClickListener((ClickEvent event) -> {
            Notification.show("Vista Lista");
        });

        Button btnGridView = component.createButtonIconTiny();
        btnGridView.setIcon(FontAwesome.TH_LARGE);
        btnGridView.setDescription("Vista Grid");
        btnGridView.addClickListener((ClickEvent event) -> {
            Notification.show("Vista Grid");
        });

//        CssLayout group = new CssLayout(btnListView, btnGridView);
//        group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
//        viewButtons.addComponent(group);
        viewButtons.addComponents(btnListView, btnGridView);

        return viewButtons;
    }

    private Component buildPath() {
        rootPath = new HorizontalLayout();
        //rootPath.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        //rootPath.setSpacing(true);
        //rootPath.addStyleName("barPath");

        Button button = component.createButtonPath("Archivos");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                cleanAndBuild(origenPath);
            }
        });

        rootPath.addComponent(button);

        return rootPath;
    }

    private Component selectView(Boolean selected, File pathDirectory) {
        Component viewSelected = null;
        if (selected) {
            viewSelected = buildGridView(pathDirectory);
        } else {
            viewSelected = buildListView(pathDirectory);
        }

        return viewSelected;
    }

    private Component buildGridView(File pathDirectory) {
        path = pathDirectory;

        CssLayout gridView = new CssLayout();
        gridView.addStyleName("gridView");
        gridView.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
        Responsive.makeResponsive(gridView);

        File currentDir = new File(path.getAbsolutePath());
        List<File> files = (List<File>) component.directoryContents(currentDir);

//        files.stream().map((file) -> {
//            fileGrid = new FileGridLayout(viewLogicFile, viewLogicDirectory, file);
//            return fileGrid;
//        }).forEach((fileGrid) -> {
//            gridView.addComponent(fileGrid);
//        });

        for (final File file : files) {
            fileGrid = new FileGridLayout(viewLogicFile, viewLogicDirectory, file);
            gridView.addComponent(fileGrid);
        }


        return gridView;
    }

    private Component buildListView(File pathDirectory) {
//        VerticalLayout listView = new VerticalLayout();
//        listView.setWidth(100.0f, Sizeable.Unit.PERCENTAGE);
//        listView.addStyleName("listView");
//        Responsive.makeResponsive(listView);
//
//        fileList = new FileListLayout(viewLogicFile, viewLogicDirectory, pathDirectory);
//
//        listView.addComponent(fileList);
        
        //return listView;
        return new FileListLayout(viewLogicFile, viewLogicDirectory, pathDirectory);
    }

    public void displayDirectoryContents(File file) {
        String directory = nameDir(file, origenPath.getAbsolutePath());
        String[] arrayDirectories = directory.split("\\\\");

        for (String lblDirectory : arrayDirectories) {
            lblArrow = new Label(FontAwesome.ANGLE_RIGHT.getHtml(), ContentMode.HTML);
            lblArrow.addStyleName(ValoTheme.LABEL_COLORED);
            btnFolder = component.createButtonPath(lblDirectory);
            btnFolder.addClickListener(event -> {
                String newRoot = event.getComponent().getCaption();
                String directorys = file.getAbsolutePath();
                int inicio = directorys.indexOf(newRoot);
                String dir = directorys.substring(0, inicio + newRoot.length());

                cleanAndBuild(new File(dir));
                displayDirectoryContents(new File(dir));

            });

            rootPath.addComponent(lblArrow);
            rootPath.setComponentAlignment(lblArrow, Alignment.MIDDLE_CENTER);
            rootPath.addComponent(btnFolder);
        }
    }

    private String nameDir(File file, String nameDir) {
        String root = nameDir;
        String directory = file.getAbsolutePath();
        int inicio = directory.indexOf(root);
        // SE REALIZA ESTA VALIDACION PARA EVITAR ERRORES EN LA VISTA DE LOS ARCHIVOS DEL FOLDER "ARCHIVOS" UNICAMENTE
        int uno = root.equals(directory) ? 0 : 1;
        String substring = directory.substring(inicio + root.length() + uno);

        return substring;
    }

    public void cleanAndBuild(File directory) {
        content.removeAllComponents();
        content.addComponent(buildHeader());
        content.addComponent(buildToolBar(directory));
        content.addComponent(buildViewsBar());
        //directoryContent = buildGridView(directory);
        directoryContent = selectView(selected, directory);
        content.addComponent(directoryContent);
        content.setExpandRatio(directoryContent, 1);
    }

    private Plupload uploadContents(File directory) {

        String uploadPath = directory.getAbsolutePath();
        System.out.println("uploadPath = " + uploadPath);
        Plupload uploader = new Plupload("Cargar", FontAwesome.UPLOAD);
        //uploader.addFilter(new PluploadFilter("music", "mp3, flac"));
        uploader.setPreventDuplicates(true);
        uploader.addStyleName(ValoTheme.BUTTON_PRIMARY);
        uploader.addStyleName(ValoTheme.BUTTON_SMALL);
        uploader.setUploadPath(uploadPath);
        uploader.setMaxFileSize("5mb");

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
                cleanAndBuild(new File(uploadPath));
                displayDirectoryContents(new File(uploadPath));
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
    
    @Override
    public void enter(final ViewChangeEvent event) {
    }

}
