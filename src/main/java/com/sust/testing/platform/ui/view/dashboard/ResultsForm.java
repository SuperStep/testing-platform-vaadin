package com.sust.testing.platform.ui.view.dashboard;

import com.storedobject.chart.*;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.entity.VectorPsychotype;
import com.sust.testing.platform.backend.service.CompleteTestAnswersService;
import com.sust.testing.platform.backend.service.UserService;
import com.sust.testing.platform.backend.service.VectorService;
import com.sust.testing.platform.ui.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.Location;
import de.mekaso.vaadin.addon.compani.AnimatedView;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewInTransition;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewOutTransition;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Route(value = "results", layout = MainLayout.class)
public class ResultsForm extends VerticalLayout implements HasUrlParameter<String> {

    private CompleteTestAnswersService completeTestAnswersService;
    private VectorService vectorService;
    private final UserService userService;
    private Long test_id;
    User currentUser;

    public ResultsForm(CompleteTestAnswersService completeTestAnswersService,
                       VectorService vectorService, UserService userService) {

        this.userService = userService;
        this.completeTestAnswersService = completeTestAnswersService;
        this.vectorService = vectorService;
        this.currentUser = userService.getCurrent();
        addClassName("pt-page-delay400");
    }

    private void generateView() {

        addClassName("dashboard-view");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        Map<VectorPsychotype, Integer> results = completeTestAnswersService.getStats(currentUser, test_id);

        VerticalLayout verticalLayout = new VerticalLayout(
                new H1(currentUser.getEmail()),
                getBarChart(results)
        );
        verticalLayout.setAlignItems(Alignment.CENTER);
        HorizontalLayout horizontalLayout = new HorizontalLayout(
                verticalLayout,
                getDetails(results)
        );

        add(horizontalLayout, repeatBtn());
    }

    private Button repeatBtn() {
        Button repeat = new Button(getTranslation("testing.repeat"), e -> {
            UI.getCurrent().navigate(TestingForm.class);
        });
        return repeat;
    }

    private SOChart getBarChart (Map<VectorPsychotype, Integer> results) {

        SOChart soChart = new SOChart();
        soChart.setSize("600px", "400px");

        CategoryData labels = new CategoryData(results.keySet()
                .stream().map(VectorPsychotype::getName).toArray(String[]::new));

        Data data = new Data((results.values().toArray(
                new Number[results.keySet().size()])));

        PieChart bc = new PieChart(labels, data);
        RectangularCoordinate rc;
        rc  = new RectangularCoordinate(new XAxis(DataType.CATEGORY), new YAxis(DataType.NUMBER));
        Position p = new Position();
        p.setBottom(Size.percentage(55));
        rc.setPosition(p);
        bc.plotOn(rc);

        soChart.add(bc);

        return soChart;
    }

    private Accordion getDetails(Map<VectorPsychotype, Integer> results) {

        Accordion accordion = new Accordion();

        Map<VectorPsychotype, Integer> resultsSorted =
            results.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue, LinkedHashMap::new)
            );

        for (Map.Entry<VectorPsychotype, Integer> result: resultsSorted.entrySet()) {

            TextArea desc = new TextArea(getTranslation("testing.description"));
            desc.setReadOnly(true);

            VectorPsychotype vector = result.getKey();
            if (vector != null) {
                desc.setValue(vector.getDescription());
            }

            accordion.add(String.format("%s  %s: %s",
                    result.getKey().getName(),
                    getTranslation("testing.score"),
                    result.getValue()),
                    desc);
        }

        return accordion;
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {

        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        Map<String, List<String>> parametersMap =
                queryParameters.getParameters();

        test_id = Long.parseLong(parametersMap.get("test_id").get(0));

        generateView();

    }
}
