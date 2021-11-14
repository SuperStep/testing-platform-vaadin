package com.sust.testing.platform.ui.view.list;

import com.sust.testing.platform.backend.entity.VectorPsychotype;
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
@PageTitle("Tests | ProtoTests")
@Route(value="vectors", layout = MainLayout.class)
@Secured("admin")
public class VectorList extends VerticalLayout {

    GridCrud<VectorPsychotype> crud = new GridCrud<>(VectorPsychotype.class);

    public VectorList(VectorService vectorService){
        // grid configuration
        crud.getGrid().setColumns("name", "description", "color");
        crud.getGrid().setColumnReorderingAllowed(true);

        // form configuration
        crud.getCrudFormFactory().setUseBeanValidation(true);
        crud.getCrudFormFactory().setVisibleProperties(
                "name", "description", "color");
        crud.getCrudFormFactory().setVisibleProperties(
                CrudOperation.ADD,
                "name", "description", "color");


        // layout configuration
        setSizeFull();
        add(crud);
        crud.setFindAllOperationVisible(false);

        // logic configuration
        crud.setOperations(
                () -> vectorService.findAll(),
                vector -> vectorService.save(vector),
                vector -> vectorService.save(vector),
                vector -> vectorService.delete(vector)
        );
    }

}
