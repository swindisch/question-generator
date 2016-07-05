package de.saschafeldmann.adesso.master.thesis.portlet.view.generation.edit;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import de.saschafeldmann.adesso.master.thesis.generation.model.TestQuestion;
import de.saschafeldmann.adesso.master.thesis.portlet.model.generation.QuestionType;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.QuestionGeneratorProperties;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.i18n.Messages;
import de.saschafeldmann.adesso.master.thesis.portlet.util.CommaseparatedValueUtil;
import de.saschafeldmann.adesso.master.thesis.portlet.util.VaadinUtil;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.Button;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.ComboBox;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.FormLayout;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.HorizontalLayout;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.ListSelect;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Project:        Masterthesis of Sascha Feldmann
 * Creation date:  05.07.2016
 * Author:         Sascha Feldmann (sascha.feldmann@gmx.de)
 * <br /><br />
 * University:
 * Hochschule für Technik und Wirtschaft, Berlin
 * Fachbereich 4
 * Studiengang Internationale Medieninformatik (Master)
 * <br /><br />
 * Company:
 * adesso AG
 * <br /><br />
 * The implementation of {@link QuestionGenerationEditQuestionView}
 */
@org.springframework.stereotype.Component
@Scope("prototype")
public class QuestionGenerationEditQuestionViewImpl extends Window implements QuestionGenerationEditQuestionView, Window.CloseListener {

    private static final String CSS_STYLE_NAME_WINDOW = "question-generation-edit-question-window";
    private QuestionGenerationEditQuestionViewListener viewListener;

    private final Messages messages;
    private final FormLayout formLayout;
    private final TextField inputTestQuestion;
    private final TextField inputCorrectAnswer;
    private final ListSelect selectQuestionType;
    private final TextField textFieldWrongAnswers;
    private final TextField textFieldCorrectAnswers;
    private final HorizontalLayout buttonGroupLayout;
    private final Button editButton;
    private final Button deleteButton;
    private final QuestionGeneratorProperties questionGeneratorProperties;
    private TestQuestion testQuestion;

    /**
     * Creates a new view.
     * @param messages messages
     * @param formLayout formLayout
     * @param inputTestQuestion the test question
     * @param inputCorrectAnswer the correct answer
     * @param selectQuestionType the question type
     * @param comboBoxWrongAnswers the combobox of wrong answers
     * @param textFieldCorrectAnswers the combobox of correct answer
     * @param editButton the edit button
     * @param deleteButton the delete button
     * @param questionGeneratorProperties the properties
     */
    @Autowired
    public QuestionGenerationEditQuestionViewImpl(
            final Messages messages,
            final FormLayout formLayout,
            final TextField inputTestQuestion,
            final TextField inputCorrectAnswer,
            final ListSelect selectQuestionType,
            final TextField comboBoxWrongAnswers,
            final TextField textFieldCorrectAnswers,
            final HorizontalLayout buttonGroupLayout,
            final Button editButton,
            final Button deleteButton,
            final QuestionGeneratorProperties questionGeneratorProperties
    ) {
        this.messages = messages;
        this.formLayout = formLayout;
        this.inputTestQuestion = inputTestQuestion;
        this.inputCorrectAnswer = inputCorrectAnswer;
        this.selectQuestionType = selectQuestionType;
        this.textFieldWrongAnswers = comboBoxWrongAnswers;
        this.textFieldCorrectAnswers = textFieldCorrectAnswers;
        this.buttonGroupLayout = buttonGroupLayout;
        this.editButton = editButton;
        this.deleteButton = deleteButton;
        this.questionGeneratorProperties = questionGeneratorProperties;
    }

    @PostConstruct
    private void initialize() {
        setLabels();
        initializeQuestionTypeList();
        arrangeComponents();
        hideMultipleChoiceComponents();
        setActionListeners();
        setStyles();
        setPosition(questionGeneratorProperties.getQuestionEditWindowPositionX(), questionGeneratorProperties.getQuestionEditWindowPositionY());

        setContent(formLayout);
    }

    private void setStyles() {
        addStyleName(CSS_STYLE_NAME_WINDOW);
    }

    private void setLabels() {
        setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowTitle());

        inputTestQuestion.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputTestquestion());
        inputCorrectAnswer.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputCorrectAnswer());
        selectQuestionType.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputQuestionTypeLabel());

        textFieldWrongAnswers.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputQuestionWrongAnswersLabel());
        textFieldWrongAnswers.setDescription(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputQuestionWrongAnswersTooltip());
        textFieldCorrectAnswers.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputQuestionCorrectAnswersLabel());
        textFieldCorrectAnswers.setDescription(messages.getQuestionGenerationViewFinishedQuestionsEditWindowInputQuestionCorrectAnswersTooltip());

        editButton.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowButtonEdit());
        deleteButton.setCaption(messages.getQuestionGenerationViewFinishedQuestionsEditWindowButtonDelete());
    }

    private void initializeQuestionTypeList() {
        selectQuestionType.setRows(1);
        selectQuestionType.addItem(QuestionType.SHORT_ANSWER_QUESTION);
        selectQuestionType.addItem(QuestionType.MULTIPLE_CHOICE_QUESTION);
    }

    private void arrangeComponents() {
        formLayout.addComponent(inputTestQuestion);
        formLayout.addComponent(inputCorrectAnswer);
        formLayout.addComponent(selectQuestionType);
        formLayout.addComponent(textFieldWrongAnswers);
        formLayout.addComponent(textFieldCorrectAnswers);

        buttonGroupLayout.addComponent(editButton);
        buttonGroupLayout.addComponent(deleteButton);

        formLayout.addComponent(buttonGroupLayout);
    }

    private void setActionListeners() {
        selectQuestionType.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (null != valueChangeEvent && valueChangeEvent.getProperty().getValue() instanceof QuestionType) {
                    QuestionType questionType = (QuestionType) valueChangeEvent.getProperty().getValue();

                    if (questionType.equals(QuestionType.SHORT_ANSWER_QUESTION)) {
                        hideMultipleChoiceComponents();
                    } else {
                        showMultipleChoiceComponents();
                    }
                }
            }
        });

        editButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent clickEvent) {
                viewListener.onEditButtonClicked(testQuestion);
            }
        });

        deleteButton.addClickListener(new com.vaadin.ui.Button.ClickListener() {
            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent clickEvent) {
                viewListener.onDeleteButtonClicked(testQuestion);
            }
        });

        addCloseListener(this);
    }



    private void hideMultipleChoiceComponents() {
        textFieldWrongAnswers.setVisible(false);
        textFieldCorrectAnswers.setVisible(false);
    }

    private void showMultipleChoiceComponents() {
        textFieldWrongAnswers.setVisible(true);
        textFieldCorrectAnswers.setVisible(true);
    }

    @Override
    public void displayForTestQuestion(TestQuestion testQuestion) {
        this.testQuestion = testQuestion;

        reset();

        setInputValues(testQuestion);

        // displays the window
        VaadinUtil.addWindow(this);
    }

    private void setInputValues(TestQuestion testQuestion) {
        inputTestQuestion.setValue(testQuestion.getQuestion());
        inputCorrectAnswer.setValue(testQuestion.getCorrectAnswer());

        if (testQuestion.isMultipleChoice()) {
            selectQuestionType.select(QuestionType.MULTIPLE_CHOICE_QUESTION);
            showMultipleChoiceComponents();
            textFieldWrongAnswers.setValue(CommaseparatedValueUtil.toCommaSeparatedString(testQuestion.getAlternativeWrongAnswers()));
            textFieldCorrectAnswers.setValue(CommaseparatedValueUtil.toCommaSeparatedString(testQuestion.getAlternativeCorrectAnswers()));
        } else {
            selectQuestionType.select(QuestionType.SHORT_ANSWER_QUESTION);
            hideMultipleChoiceComponents();
        }
    }

    private void reset() {
        textFieldCorrectAnswers.setValue("");
        textFieldWrongAnswers.setValue("");
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public void setViewListener(QuestionGenerationEditQuestionViewListener viewListener) {
        this.viewListener = viewListener;
    }

    @Override
    public String getQuestionInput() {
        return inputTestQuestion.getValue();
    }

    @Override
    public String getCorrectAnswerInput() {
        return inputCorrectAnswer.getValue();
    }

    @Override
    public QuestionType getQuestionTypeInput() {
        return (QuestionType) selectQuestionType.getValue();
    }

    @Override
    public List<String> getMultipleChoiceWrongAnswersInput() {
        return getAllItems(textFieldWrongAnswers);
    }

    @Override
    public List<String> getMultipleChoiceCorrectAnswersInput() {
        return getAllItems(textFieldCorrectAnswers);
    }

    private List<String> getAllItems(TextField textField) {
        return CommaseparatedValueUtil.toMultipleValuesList(textField.getValue());
    }

    @Override
    public void windowClose(CloseEvent closeEvent) {
        viewListener.onClosed(testQuestion);
    }
}