package com.sust.testing.platform.ui.view.dashboard;

import com.sust.testing.platform.backend.data.QuestionSet;
import com.sust.testing.platform.backend.data.Uploader;
import com.sust.testing.platform.backend.entity.Settings;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.repository.SettingsRepository;
import com.sust.testing.platform.backend.service.TestService;
import com.sust.testing.platform.backend.service.UserService;
import com.sust.testing.platform.ui.MainLayout;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.IOUtils;
import org.springframework.security.access.annotation.Secured;
import org.vaadin.crudui.crud.impl.GridCrud;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@PageTitle("admin dashboard | ProtoTests")
@Route(value="admin", layout = MainLayout.class)
@Secured("admin")
public class AdminDashboard extends VerticalLayout {

    GridCrud<User> userCrud = new GridCrud<>(User.class);
    GridCrud<Settings> settingsCrud = new GridCrud<>(Settings.class);

    MemoryBuffer buffer = new MemoryBuffer();
    List<QuestionSet> data;

    private final UserService userService;

    public AdminDashboard(UserService userService,
                          TestService testService,
                          Uploader uploader,
                          SettingsRepository settingsRepository) {
        this.userService = userService;

        add(
            new HorizontalLayout(
                    getUploadButton(),
                    new Button(getTranslation("admin.uploadbutton"), e -> {
                        data = uploader.parseFile(buffer);
                        testService.persistData(data);
                    })
            )
        );

        //add(new H2("Manage settings"), generateSettingsCrud());
        add(new H2("Manage users"), generateUserCrud());

    }

    private GridCrud<User> generateUserCrud() {

        GridCrud<User> crud = new GridCrud<>(User.class);

        // grid configuration
        crud.getGrid().setColumns("email", "firstName", "lastName", "role", "activated");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties(
                "email", "firstName", "lastName", "passwordHash",
                "role", "locked", "activated");

        // layout configuration
        setSizeFull();

        crud.setFindAllOperationVisible(false);

        // logic configuration
        crud.setOperations(
                () -> userService.findAll(),
                user -> userService.save(user),
                user -> userService.save(user),
                user -> userService.delete(user)
        );

        crud.setSizeFull();

        return crud;
    }


    private Component getUploadButton() {
        Div output = new Div();

        //@formatter:off
        // begin-source-example
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label(getTranslation("admin.uploadfile.tip")));
        //upload.setAcceptedFileTypes("text/csv");
        //upload.setMaxFileSize(300);

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            showOutput(event.getErrorMessage(), component, output);
        });
        // end-source-example
        //@formatter:on
        upload.setId("test-upload");
        output.setId("test-output");

        return card(getTranslation("admin.uploadfile"), upload, output);
    }

    private Component card(String title, Component... components) {
        Div container = new Div(components);
        container.addComponentAsFirst(new H2(title));
        return container;
    }

    private Component createTextComponent(InputStream stream) {
        String text;
        try {
            text = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            text = "exception reading stream";
        }
        return new Text(text);
    }

    private void showOutput(String text, Component content,
                            HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }

}
