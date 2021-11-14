package com.sust.testing.platform.ui.view.forms;

import com.sust.testing.platform.backend.entity.Influence;
import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.sust.testing.platform.backend.service.VectorService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class QuestionForm extends FormLayout {

    private Question question;

    VectorService vectorService;
    List<Influence> influenceList;
    List<VectorPsychotype> vectorsList;

    TextArea text = new TextArea("Text");
    IntegerField positionInTest = new IntegerField("Position");

    Grid<Influence> grid = new Grid<>();

    ComboBox<Test> test = new ComboBox<>("Test");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    Binder<Question> binder = new BeanValidationBinder<>(Question.class);

    public void setQuestion(Question question) {
        this.question = question;
        if (question != null){
            this.influenceList = question.getInfluenceList();
            updateGrid();
        }
        binder.readBean(question);
    }

    public void updateGrid(){
        grid.setItems(this.influenceList);
    }

    public QuestionForm(List<Test> tests, List<VectorPsychotype> vectorsList) {

        this.vectorsList = vectorsList;

        positionInTest.setHasControls(true);
        positionInTest.setValue(getLastPosition() + 1);

        addClassName("questions-form");
        binder.bindInstanceFields(this);

        test.setItems(tests);
        test.setItemLabelGenerator(Test::getName);
        test.addValueChangeListener(e -> {
            positionInTest.setValue(getLastPosition() + 1);
        });

        text.setSizeFull();
        text.setWidthFull();
        grid.setHeightByRows(true);
        grid.setPageSize(5);

        VerticalLayout top = new VerticalLayout(
                new HorizontalLayout(test, positionInTest));

        VerticalLayout bottom = new VerticalLayout(configureInfluenceGrid());

        top.setSizeFull();
        bottom.setSizeFull();
        HorizontalLayout buttons = createButtonsLayout();

        add(top, text, bottom, buttons);

        setColspan(text, 2);

        this.setResponsiveSteps(
                new ResponsiveStep("40em", 1),
                new ResponsiveStep("80em", 2));

    }

    private Integer getLastPosition() {
        if (!test.isEmpty()) {
            return test.getValue().getQuestions().size();
        } else {
            return 1;
        }
    }

    private VerticalLayout configureInfluenceGrid() {

        Button addNewInfluence = new Button("Add", event -> {
            influenceList.add(new Influence(Influence.Answer.Yes, 1));
            updateGrid();
        });

        Grid.Column<Influence> answerColumn = grid.addColumn(Influence::getAnswer)
                .setHeader("Answer");
        Grid.Column<Influence> valueColumn = grid.addColumn(Influence::getValue)
                .setHeader("Value");
        Grid.Column<Influence> vectorColumn =
                grid.addColumn(Influence::getVectorName)
                .setHeader("Vector");

        Binder<Influence> binder = new Binder<>(Influence.class);
        grid.getEditor().setBinder(binder);

        ComboBox<Influence.Answer> answerField = new ComboBox<>();
        answerField.setItems(Influence.Answer.values());

        IntegerField valueField = new IntegerField();
        valueField.setHasControls(true);

        ComboBox<VectorPsychotype> vectorFiled = new ComboBox<>();
        vectorFiled.setItems(vectorsList);
        vectorFiled.setItemLabelGenerator(VectorPsychotype::getName);

        answerField.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab' && event.shiftKey");
        binder.forField(answerField).bind("answer");
        answerColumn.setEditorComponent(answerField);

        valueField.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab'");
        binder.forField(valueField).bind("value");
        valueColumn.setEditorComponent(valueField);

        vectorFiled.getElement()
                .addEventListener("keydown",
                        event -> grid.getEditor().cancel())
                .setFilter("event.key === 'Tab'");
        binder.forField(vectorFiled).bind("vector");
        vectorColumn.setEditorComponent(vectorFiled);

        grid.addItemDoubleClickListener(event -> {
            grid.getEditor().editItem(event.getItem());
            //answerField.focus();
        });

        return new VerticalLayout(addNewInfluence, grid);

    }

    private HorizontalLayout createButtonsLayout() {

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new QuestionForm.DeleteEvent(this, question)));
        close.addClickListener(event -> fireEvent(new QuestionForm.CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(question);
            fireEvent(new QuestionForm.SaveEvent(this, question, influenceList));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    // Events
    public static abstract class QuestionFormEvent extends ComponentEvent<QuestionForm> {

        private Question question;
        private List<Influence> influenceList;

        protected QuestionFormEvent(QuestionForm source, Question question, List<Influence> influenceList) {
            super(source, false);
            this.question = question;
            this.influenceList = influenceList;
        }
        public Question getQuestion() {
            return question;
        }
        public List<Influence> getInfluenceList() { return influenceList; }
    }
    public static class SaveEvent extends QuestionFormEvent {
        SaveEvent(QuestionForm source, Question question, List<Influence> influenceList) {
            super(source, question, influenceList);
        }
    }
    public static class DeleteEvent extends QuestionFormEvent {
        DeleteEvent(QuestionForm source, Question question) {
            super(source, question, null);
        }
    }
    public static class CloseEvent extends QuestionFormEvent {
        CloseEvent(QuestionForm source) {
            super(source, null, null);
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
