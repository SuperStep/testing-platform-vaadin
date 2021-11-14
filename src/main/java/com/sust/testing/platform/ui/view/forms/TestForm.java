package com.sust.testing.platform.ui.view.forms;

import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.service.QuestionService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class TestForm extends FormLayout {

    private Test test;
    private QuestionService questionService;

    TextField name = new TextField("Name");
    Grid<Question> grid = new Grid<>(Question.class);
    
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Test> binder = new BeanValidationBinder<>(Test.class);

    public void setTest(Test test) {
        this.test = test;
        updateGrid();
        binder.readBean(test);
    }

    public TestForm(QuestionService questionService) {

        this.questionService = questionService;

        addClassName("tests-form");
        binder.bindInstanceFields(this);

        configureGrid();

        add(name, grid, createButtonsLayout());
    }

    private void configureGrid() {
        grid.addClassName("questions-grid");
        grid.setSizeFull();
        //grid.removeColumnByKey("test");
        grid.setColumns("text", "positionInTest");
        grid.setHeightByRows(true);
        //grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    private void updateGrid() {
        if (this.test != null) {
            grid.setItems(questionService.getQuestionsByTestID(this.test));
        } else {
        }
    }

    private HorizontalLayout createButtonsLayout() {

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new TestForm.DeleteEvent(this, test)));
        close.addClickListener(event -> fireEvent(new TestForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(test);
            fireEvent(new TestForm.SaveEvent(this, test));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class TestFormEvent extends ComponentEvent<TestForm> {

        private Test test;

        protected TestFormEvent(TestForm source, Test test) {
            super(source, false);
            this.test = test;
        }
        public Test getTest() {
            return test;
        }
    }
    public static class SaveEvent extends TestFormEvent {
        SaveEvent(TestForm source, Test test) {
            super(source, test);
        }
    }
    public static class DeleteEvent extends TestFormEvent {
        DeleteEvent(TestForm source, Test test) {
            super(source, test);
        }
    }
    public static class CloseEvent extends TestFormEvent {
        CloseEvent(TestForm source) {
            super(source, null);
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
