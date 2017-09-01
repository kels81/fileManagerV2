package com.mx.zoom.filebox.view.transactions;

import com.google.common.eventbus.Subscribe;
import com.mx.zoom.filebox.component.FileListLayout;
import com.mx.zoom.filebox.event.DashboardEvent.BrowserResizeEvent;
import com.mx.zoom.filebox.event.DashboardEventBus;
import com.mx.zoom.filebox.logic.ScheduleFileLogic;
import com.mx.zoom.filebox.utils.Components;
import com.mx.zoom.filebox.utils.Constantes;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;

@SuppressWarnings({"serial", "unchecked"})
public final class FilesView_2 extends VerticalLayout implements View {

    private Table table;
    private FileListLayout fileList;

    private File rootPath;

    private static final String[] DEFAULT_COLLAPSIBLE = {"fecha", "tamaño"};

    private final Components component = new Components();
    //private final ScheduleFileLogic viewLogic = new ScheduleFileLogic(this);

    public FilesView_2() {
        setSizeFull();
        addStyleName("transactions");
        DashboardEventBus.register(this);

        addComponent(buildToolbar());

        table = buildTable();
        addComponent(table);
        setExpandRatio(table, 1);

    }

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        DashboardEventBus.unregister(this);
    }

    private Component buildToolbar() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        //header.setSpacing(true);
        Responsive.makeResponsive(header);

        HorizontalLayout tools = new HorizontalLayout(buildFilter());
        //tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Component buildFilter() {
        final TextField filter = new TextField();
        filter.addTextChangeListener(new TextChangeListener() {
            @Override
            public void textChange(final TextChangeEvent event) {
                Filterable data = (Filterable) table.getContainerDataSource();
                data.removeAllContainerFilters();
                data.addContainerFilter(new Filter() {
                    @Override
                    public boolean passesFilter(final Object itemId,
                            final Item item) {

                        if (event.getText() == null
                                || event.getText().equals("")) {
                            return true;
                        }

                        return filterByProperty("nombre", item,
                                event.getText());
                    }

                    private boolean filterByProperty(final String prop, final Item item,
                            final String text) {
                        if (item == null || item.getItemProperty(prop) == null
                                || item.getItemProperty(prop).getValue() == null) {
                            return false;
                        }
                        String val = item.getItemProperty(prop).getValue().toString().trim()
                                .toLowerCase();
                        return val.contains(text.toLowerCase().trim());
                    }

                    @Override
                    public boolean appliesToProperty(Object propertyId) {
                        return propertyId.equals("nombre");
                    }
                });
            }
        });

        filter.setInputPrompt("Filtrar");
        filter.setDescription("Filtrar por nombre del archivo");
        filter.setIcon(FontAwesome.SEARCH);
        filter.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        //filter.setWidth(100.0f, Unit.PERCENTAGE);
        filter.addShortcutListener(new ShortcutListener("Clear",
                KeyCode.ESCAPE, null) {
            @Override
            public void handleAction(final Object sender, final Object target) {
                filter.setValue("");
                ((Filterable) table.getContainerDataSource())
                        .removeAllContainerFilters();
            }
        });
        return filter;
    }

    private Table buildTable() {
        rootPath = new File(Constantes.ROOT_PATH);

        //List<File> files = (List<File>) component.directoryContents(rootPath);
        //fileList = new FileListLayout(viewLogic, rootPath);
        
        //return fileList = new FileListLayout(viewLogic, rootPath);
        return new Table();
    }

    private boolean defaultColumnsVisible() {
        boolean result = true;
        for (String propertyId : FileListLayout.DEFAULT_COLLAPSIBLE) {
            if (fileList.isColumnCollapsed(propertyId) == Page.getCurrent()
                    .getBrowserWindowWidth() < 800) {
                result = false;
            }
        }
        return result;
    }

    @Subscribe
    public void browserResized(final BrowserResizeEvent event) {
        // Some columns are collapsed when browser window width gets small
        // enough to make the table fit better.
        if (defaultColumnsVisible()) {
            for (String propertyId : FileListLayout.DEFAULT_COLLAPSIBLE) {
                fileList.setColumnCollapsed(propertyId, Page.getCurrent()
                        .getBrowserWindowWidth() < 800);
            }
        }
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    }

}
