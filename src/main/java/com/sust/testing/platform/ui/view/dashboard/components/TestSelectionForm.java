package com.sust.testing.platform.ui.view.dashboard.components;

import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.service.TestService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.shared.Registration;

public class TestSelectionForm extends FormLayout {

    ComboBox<Test> testField = new ComboBox<>(getTranslation("testing.select.test"));
    Test selectedTest;
    TextArea testDescription = new TextArea();

    public TestSelectionForm(TestService testService){

        setSizeFull();
        testField.setItems(testService.findAll());
        testField.setItemLabelGenerator(Test::getName);
        testField.addValueChangeListener(e -> {
            selectedTest = testField.getValue();
            testDescription = new TextArea(selectedTest.getDescription());
        });
        Button selectButton = new Button(getTranslation("testing.select.button"));
        selectButton.addClickListener(event -> fireEvent(new ChangeEvent(this, false, selectedTest)));

        if (testField.getValue() != null){
            testDescription.setValue(testField.getValue().getDescription());
        }

        add(testField, testDescription, selectButton);

    }



    public static class ChangeEvent extends ComponentEvent<TestSelectionForm> {
        private final Test test;
        public ChangeEvent(TestSelectionForm source, boolean fromClient, Test test) {
            super(source, fromClient);
            this.test = test;
        }
        public Test getTest() {
            return test;
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
