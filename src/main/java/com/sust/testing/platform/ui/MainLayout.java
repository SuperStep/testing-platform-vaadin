package com.sust.testing.platform.ui;

import com.sust.testing.platform.security.SecurityUtils;
import com.sust.testing.platform.ui.view.dashboard.AdminDashboard;
import com.sust.testing.platform.ui.view.dashboard.TestingForm;
import com.sust.testing.platform.ui.view.list.CompletedTestsList;
import com.sust.testing.platform.ui.view.list.QuestionList;
import com.sust.testing.platform.ui.view.list.TestList;
import com.sust.testing.platform.ui.view.list.VectorList;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewInTransition;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewOutTransition;

@JsModule("./js/os-theme-switcher.js")
@CssImport("./styles/shared-styles.css")
@PWA(name = "TestingPlatform", shortName = "TestingPlatform",
        offlineResources = {"./styles/offline.css", "./images/offline.png"})
public class MainLayout extends AppLayout {

    Tabs tabs = new Tabs();

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1(getTranslation("app.name"));
        logo.addClassName("logo");

        Anchor logout = new Anchor("logout", getTranslation("app.logout"));

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, logout);
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassName("header");

        addToNavbar(header);

    }
    private void createDrawer() {

        tabs.removeAll();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        createOptionIfGranted("menu.testing", TestingForm.class, true);
        createOptionIfGranted("menu.testlist", TestList.class, false);
        createOptionIfGranted("menu.questions", QuestionList.class, false);
        createOptionIfGranted("menu.vectors", VectorList.class, false);
        createOptionIfGranted("menu.completed", CompletedTestsList.class, false);
        createOptionIfGranted("menu.admindash", AdminDashboard.class, false);
        addToDrawer(tabs);

    }

    private void createOptionIfGranted(String key, Class classname, boolean highlight) {

        if (SecurityUtils.isAccessGranted(classname)) {
            RouterLink link = new RouterLink(getTranslation(key), classname);
            if (highlight) {
                link.setHighlightCondition(HighlightConditions.sameLocation());
            }
            tabs.add(new Tab(link));
        }
    }

//    @Override
//    public ViewInTransition getInTransition() {
//        return ViewInTransition.FlipTop;
//    }
//
//    @Override
//    public ViewOutTransition getOutTransition() {
//        return ViewOutTransition.FlipBottom;
//    }
}