package com.sust.testing.platform.ui.view.forms;

import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class VectorPsychotypeForm extends FormLayout {

    private VectorPsychotype vectorPsychotype;

    private TextField name = new TextField(getTranslation("vector.name"));
    private TextArea description = new TextArea("vector.desc");
    private ComboBox<VectorPsychotype.Color> color = new ComboBox<>("vector.color");

    Binder<VectorPsychotype> binder = new BeanValidationBinder<>(VectorPsychotype.class);

    public VectorPsychotypeForm(VectorPsychotype vectorPsychotype){
        this.vectorPsychotype = vectorPsychotype;

        binder.bindInstanceFields(this);

        color.setItems(VectorPsychotype.Color.values());

        add(name, description, color);

    }

    public void setVectorPsychotype(VectorPsychotype vectorPsychotype) {
        this.vectorPsychotype = vectorPsychotype;
    }


}
