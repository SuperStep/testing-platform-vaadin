package com.sust.testing.platform.ui.view.list;

import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.service.QuestionService;
import com.sust.testing.platform.backend.service.TestService;
import com.sust.testing.platform.ui.MainLayout;
import com.sust.testing.platform.ui.view.forms.TestForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@PageTitle("Tests | ProtoTests")
@Route(value="tests", layout = MainLayout.class)
@Secured("admin")
public class TestList extends VerticalLayout {

    QuestionService questionService;
    TestService testService;

    Grid<Test> testGrid = new Grid<>(Test.class);

    TestForm form;

    public TestList(QuestionService questionService,
                    TestService testService) {

        this.questionService = questionService;
        this.testService = testService;

        setSizeFull();

        configureGrid();

        form = new TestForm(questionService);
        form.addListener(TestForm.SaveEvent.class, this::saveTest);
        form.addListener(TestForm.DeleteEvent.class, this::deleteTest);
        form.addListener(TestForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(testGrid, form);

        //Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();

        closeEditor();
    }

    private void configureGrid() {

        testGrid.addClassName("tests-grid");
        testGrid.setSizeFull();
        testGrid.setColumns("name");
        testGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        testGrid.asSingleSelect().addValueChangeListener(event -> editTest(event.getValue()));

    }

    private void updateList() {
        testGrid.setItems(testService.findAll());
    }

    public void editTest(Test test) {
        if (test == null) {
            closeEditor();
        } else {
            form.setTest(test);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    void addTest() {
        testGrid.asSingleSelect().clear();
        Test newTest = new Test("");
        testService.save(newTest);
        editTest(newTest);
    }

    private void saveTest(TestForm.SaveEvent event) {
        testService.save(event.getTest());
        updateList();
        closeEditor();
    }
    private void deleteTest(TestForm.DeleteEvent event) {
        testService.delete(event.getTest());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setTest(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolbar() {

        Button addContactButton = new Button("Add test");
        addContactButton.addClickListener(click -> addTest());

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
}
