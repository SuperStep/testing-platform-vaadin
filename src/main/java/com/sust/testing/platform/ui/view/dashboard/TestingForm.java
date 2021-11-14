package com.sust.testing.platform.ui.view.dashboard;

import com.github.appreciated.card.Card;
import com.sust.testing.platform.backend.VectorResolver;
import com.sust.testing.platform.backend.entity.Influence;
import com.sust.testing.platform.backend.entity.Question;
import com.sust.testing.platform.backend.entity.Test;
import com.sust.testing.platform.backend.entity.User;
import com.sust.testing.platform.backend.repository.UserRepository;
import com.sust.testing.platform.backend.service.CompleteTestAnswersService;
import com.sust.testing.platform.backend.service.QuestionService;
import com.sust.testing.platform.backend.service.TestService;
import com.sust.testing.platform.security.SecurityUtils;
import com.sust.testing.platform.ui.MainLayout;
import com.sust.testing.platform.ui.components.CustomCarousel;
import com.sust.testing.platform.ui.view.dashboard.components.TestSelectionForm;
import com.sust.testing.platform.ui.view.forms.QuestionForm;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.mekaso.vaadin.addon.compani.AnimatedView;
import de.mekaso.vaadin.addon.compani.Animator;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewInTransition;
import de.mekaso.vaadin.addon.compani.viewtransitions.ViewOutTransition;
import java.util.*;
import java.util.stream.Collectors;

@Route(value="", layout = MainLayout.class)
public class TestingForm extends VerticalLayout implements AnimatedView {

    final Animator animator = Animator.init(UI.getCurrent());

    TestService testService;
    QuestionService questionService;
    CompleteTestAnswersService completeTestAnswersService;
    VectorResolver vectorResolver;
    Test currentTest;
    User currentUser;
    List<Question> questions;
    Integer questionIndex;
    Card questionForm;
    ProgressBar progressBar = new ProgressBar();
    Button selectQuestionButton =
        new Button(getTranslation("testing.select.choose"), event -> showDialog());
    Dialog dialog;

    UUID uuid;

    CustomCarousel carousel = CustomCarousel.create();
    private static final int WIDTH = 600;
    private static final int HEIGHT = 375;

    private final UserRepository userRepository;

    ComboBox<Test> testField = new ComboBox<>(getTranslation("testing.select.test"));

    public TestingForm(TestService testService,
                       QuestionService questionService,
                       CompleteTestAnswersService completeTestAnswersService,
                       VectorResolver vectorResolver, UserRepository userRepository) {

        this.questionService = questionService;
        this.testService = testService;
        this.completeTestAnswersService = completeTestAnswersService;
        this.vectorResolver = vectorResolver;
        this.userRepository = userRepository;

        currentUser = userRepository.findByEmailIgnoreCase(SecurityUtils.getUsername());

        //addClassName("dashboard-view");
        addClassName("pt-page-delay400");
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setSizeFull();

        carousel.setVisible(false);

        add(selectQuestionButton, carousel);

        this.addListener(ChangeEvent.class, this::releaseQuestionForm);

        showDialog();
    }

    private void showDialog(){
        TestSelectionForm selectionForm = new TestSelectionForm(testService);
        selectionForm.addListener(TestSelectionForm.ChangeEvent.class, this::setTest);
        dialog = new Dialog();
        dialog.add(selectionForm);
        dialog.open();
    }

    public void setTest(TestSelectionForm.ChangeEvent event) {

        dialog.close();
        currentTest = event.getTest();

        selectQuestionButton.setVisible(false);

        questionIndex = 0;
        carousel.removeAll();
        carousel.setVisible(true);
        carousel.setWidth(WIDTH + "px");
        carousel.setHeight(HEIGHT + "px");

        questions = currentTest.getQuestions(true);
        if (questions.isEmpty()) {
            Notification notification = new Notification(
                    getTranslation("testing.noQuestions"), 3000);
            notification.open();
        } else {
            for (Question question : questions) {
                carousel.add(new QuestionForm(question));
            }
        }

    }

    private class QuestionForm extends FlexLayout{

        QuestionForm (Question question) {

            setSizeFull();
            setFlexDirection(FlexLayout.FlexDirection.COLUMN);

            TextArea questionText = new TextArea();
            questionText.setReadOnly(true);
            questionText.setSizeFull();
            questionText.setValue(question.getText());

            add(questionText);

            HorizontalLayout answers = getAnswerButtons(Influence.Answer.values(), question, this);
            answers.setAlignItems(Alignment.CENTER);
            add(questionText, answers);
            getFlexGrow(questionText);

            getStyle().set("background-color", "#FFFFFF");
            getStyle().set("opacity", "1.0");
            getStyle().set("align-items", "stretch");

        }
    }

    private void releaseQuestionForm(ChangeEvent event) {
        FlexLayout questionFrom = event.getQuestionForm();
        questionFrom.setEnabled(false);
    }

    private HorizontalLayout getAnswerButtons(Influence.Answer[] answers, Question question,
                                              QuestionForm questionForm) {

        HorizontalLayout layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.CENTER);

        for (Influence.Answer answer:
             answers) {
            Button answerBtn = new Button();
            answerBtn.setText(getTranslation("testing.answer." + answer.name()));
            answerBtn.addClickListener(e -> {
                processAnswer(currentTest, question, answer);
                fireEvent(new ChangeEvent(this, false, questionForm));
            });
            layout.setFlexGrow(1, answerBtn);
            layout.add(answerBtn);
        }
        return layout;
    }

    private void processAnswer(Test test, Question question, Influence.Answer answer) {

        vectorResolver.saveAnswers(test, question, answer, currentUser);

        if (questions.size() <= questionIndex + 1) {
            Map<String, List<String>> parameters = new HashMap<>();
            List<String> tests = new ArrayList<>();
            tests.add(test.getId().toString());
            parameters.put("test_id", tests);
            UI.getCurrent().navigate("results", new QueryParameters(parameters));
            return;
        }

        questionIndex += 1;
        carousel.next();

    }

    public static class ChangeEvent extends ComponentEvent<TestingForm> {
        private final TestingForm.QuestionForm questionForm;
        public ChangeEvent(TestingForm source, boolean fromClient,
                           TestingForm.QuestionForm questionForm) {
            super(source, fromClient);
            this.questionForm = questionForm;
        }
        public TestingForm.QuestionForm getQuestionForm() {
            return questionForm;
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    @Override
    public ViewInTransition getInTransition() {
        return ViewInTransition.RotateCubeTopIn;
    }

    @Override
    public ViewOutTransition getOutTransition() {
        return ViewOutTransition.RotateCubeTopOut;
    }

}
