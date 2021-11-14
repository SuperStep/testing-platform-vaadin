package com.sust.testing.platform.ui.view.list;

import com.sust.testing.platform.backend.entity.CompleteTestAnswers;
import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.sust.testing.platform.backend.service.CompleteTestAnswersService;
import com.sust.testing.platform.backend.service.VectorService;
import com.sust.testing.platform.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;

@Component
@Scope("prototype")
@PageTitle("Completed tests | ProtoTests")
@Route(value="completed", layout = MainLayout.class)
public class CompletedTestsList extends VerticalLayout {

    GridCrud<CompleteTestAnswers> crud = new GridCrud<>(CompleteTestAnswers.class);

    public CompletedTestsList(CompleteTestAnswersService completeTestAnswersService){
        // grid configuration
        crud.getGrid().setColumns("test", "question", "answer", "vector", "value");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties(
                "test", "question", "answer", "vector", "value");

        // layout configuration
        setSizeFull();
        add(crud);
        crud.setFindAllOperationVisible(false);

        // logic configuration
        crud.setFindAllOperation(() -> completeTestAnswersService.findAll());
        crud.setDeleteOperation(entity -> completeTestAnswersService.delete(entity));
        crud.setUpdateOperationVisible(false);
        crud.setAddOperationVisible(false);
    }

}
