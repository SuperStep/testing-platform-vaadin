package com.sust.testing.platform.ui.view.list;

import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.service.QuestionService;
import com.sust.testing.platform.backend.service.TestService;
import com.sust.testing.platform.backend.service.VectorService;
import com.sust.testing.platform.ui.MainLayout;
import com.sust.testing.platform.ui.view.forms.QuestionForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Scope("prototype")
@PageTitle("Questions | ProtoTests")
@Route(value="questions", layout = MainLayout.class)
@Secured("admin")
public class QuestionList extends VerticalLayout {

    QuestionService questionService;
    TestService testService;
    VectorService vectorService;

    Grid<Question> grid = new Grid<>(Question.class);
    Map<String, String> filter = new HashMap<>();

    Dialog dialog = new Dialog();

    QuestionForm form;

    public QuestionList (QuestionService questionService,
                         TestService testService,
                         VectorService vectorService) {

        this.questionService = questionService;
        this.testService = testService;
        this.vectorService = vectorService;

        setSizeFull();

        configureGrid();

        form = new QuestionForm(testService.findAll(), vectorService.findAll());
        form.addListener(QuestionForm.SaveEvent.class, this::saveQuestion);
        form.addListener(QuestionForm.DeleteEvent.class, this::deleteQuestion);
        form.addListener(QuestionForm.CloseEvent.class, e -> closeEditor());

        HorizontalLayout content = new HorizontalLayout(grid);

        //Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();

        add(getToolbar(), content);
        updateList();

    }

    private void configureGrid() {
        grid.addClassName("questions-grid");
        grid.setSizeFull();
        grid.removeColumnByKey("test");
        grid.setColumns("text", "positionInTest");
        grid.addColumn(question -> {
            Test test = question.getTest();
            return test == null ? "-" : test.getName();
        }).setHeader("Test");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editQuestion(event.getValue()));

        // Filters
        TextField textFilter = new TextField();
        textFilter.setPlaceholder("Filter");
        textFilter.addValueChangeListener(event -> {
            if (event.getValue().equals("")){
                filter.keySet().remove("text");
            } else {
                filter.put("text", event.getValue());
            }
            updateList();
        });

        TextField postitionFilter = new TextField();
        postitionFilter.setPlaceholder("Filter");
        postitionFilter.addValueChangeListener(event -> {
            if (event.getValue().equals("")) {
                filter.keySet().remove("position");
            } else {
                filter.put("position", event.getValue());
            }
            updateList();
        });

        HeaderRow filterRow = grid.appendHeaderRow();
        filterRow.getCell(grid.getColumnByKey("text")).setComponent(textFilter);
        filterRow.getCell(grid.getColumnByKey("positionInTest")).setComponent(postitionFilter);
    }

    private void updateList(Optional<String> text) {
        grid.setItems(questionService.getByCriteria(text, null));
    }

    private void updateList() {

        if (filter.keySet().isEmpty()) {
            grid.setItems(questionService.findAll());
        } else {
            grid.setItems(questionService.getByCriteria(
                    Optional.ofNullable(filter.getOrDefault("text", null)),
                    Optional.ofNullable(filter.getOrDefault("position", null))));
        }
    }

    // Contacts
    public void editQuestion(Question question) {
        if (question == null) {
            closeEditor();
        } else {
            form.setQuestion(question);
            //form.setVisible(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            dialog.add(form);
            dialog.open();
            addClassName("editing");
        }
    }

    void addQuestion() {
        grid.asSingleSelect().clear();
        editQuestion(new Question());
    }

    private void saveQuestion(QuestionForm.SaveEvent event) {
        questionService.save(event.getQuestion());
        questionService.saveInfluenceList(event.getInfluenceList(), event.getQuestion());
        updateList();
        closeEditor();
    }
    private void deleteQuestion(QuestionForm.DeleteEvent event) {
        questionService.delete(event.getQuestion());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        form.setQuestion(null);
        //form.setVisible(false);
        dialog.close();
        removeClassName("editing");
    }

    private HorizontalLayout getToolbar() {

        Button addContactButton = new Button("Add question");
        addContactButton.addClickListener(click -> addQuestion());

        HorizontalLayout toolbar = new HorizontalLayout(addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
}
