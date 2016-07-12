package de.saschafeldmann.adesso.master.thesis.portlet.presenter.generation;

import com.google.common.base.Joiner;
import de.saschafeldmann.adesso.master.thesis.csv.CsvWriter;
import de.saschafeldmann.adesso.master.thesis.csv.CsvWriterImpl;
import de.saschafeldmann.adesso.master.thesis.detection.model.CardinalRelationConcept;
import de.saschafeldmann.adesso.master.thesis.detection.model.FillInTheBlankTextConcept;
import de.saschafeldmann.adesso.master.thesis.detection.model.api.Concept;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.Course;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.LearningContent;
import de.saschafeldmann.adesso.master.thesis.generation.LinguisticRealiser;
import de.saschafeldmann.adesso.master.thesis.generation.model.TestQuestion;
import de.saschafeldmann.adesso.master.thesis.portlet.QuestionGeneratorPortletVaadinUi;
import de.saschafeldmann.adesso.master.thesis.portlet.model.QuestionGenerationSession;
import de.saschafeldmann.adesso.master.thesis.portlet.presenter.AbstractStepPresenter;
import de.saschafeldmann.adesso.master.thesis.portlet.presenter.generation.edit.QuestionGenerationEditQuestionListener;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.i18n.Messages;
import de.saschafeldmann.adesso.master.thesis.portlet.view.ViewWithMenu;
import de.saschafeldmann.adesso.master.thesis.portlet.view.detection.DetectionViewImpl;
import de.saschafeldmann.adesso.master.thesis.portlet.view.generation.QuestionGenerationView;
import de.saschafeldmann.adesso.master.thesis.portlet.view.generation.QuestionGenerationViewListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Project:        Masterthesis of Sascha Feldmann
 * Creation date:  04.07.2016
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
 * The implementation of a {@link QuestionGenerationPresenter}
 */
@Component
@Scope("prototype")
public class QuestionGenerationPresenterImpl extends AbstractStepPresenter implements QuestionGenerationPresenter, QuestionGenerationViewListener, QuestionGenerationEditQuestionListener {
    private static final String CSV_EXPORT_FILENAME_TEMPLATES = "generated_test_questions_%s.csv";
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionGenerationPresenterImpl.class);
    private static final Joiner CSV_EXPORT_MULTIPLE_VALUES_IN_ONE_COLUMN_JOINER = Joiner.on(",").skipNulls();
    private final QuestionGenerationView questionGenerationView;

    @Autowired
    private LinguisticRealiser linguisticRealiser;
    @Autowired
    private Messages messages;

    /**
     * Creates a new view.
     *
     * @param view
     */
    @Autowired
    public QuestionGenerationPresenterImpl(final QuestionGenerationView view) {
        this.questionGenerationView = view;
    }

    @Override
    public QuestionGenerationView initializeView() {
        this.questionGenerationView.setViewListener(this);
        this.questionGenerationView.setMenuListener(this);
        this.questionGenerationView.setCurrentSessionStatus(questionGenerationSession.getStatus());

        this.questionGenerationView.reset();
        return this.questionGenerationView;
    }

    @Override
    public ViewWithMenu getView() {
        return this.questionGenerationView;
    }

    @Override
    public void onStartQuestionGenerationButtonClicked() {
        LOGGER.info("onStartQuestionGenerationButtonClicked()");

        generateTestQuestions();

        refreshGeneratedQuestionsLearningContents();
    }

    private void generateTestQuestions() {
        questionGenerationSession.resetGeneratedQuestionsContentsMap();

        for (LearningContent learningContent : questionGenerationSession.getDetectedConceptsContentsMap().keySet()) {
            List<TestQuestion> testQuestionsList = new ArrayList<>();
            questionGenerationSession.getGeneratedQuestionsContentsMap()
                    .put(learningContent, testQuestionsList);

            for (Concept concept : questionGenerationSession.getDetectedConceptsContentsMap().get(learningContent)) {
                TestQuestion testQuestion = linguisticRealiser.generateQuestion(concept);
                testQuestion.setLabel(buildTestQuestionLabel(testQuestion));
                testQuestionsList.add(
                        testQuestion
                );
            }
        }
    }

    private String buildTestQuestionLabel(TestQuestion testQuestion) {
        String label = "";

        // differentiate the label
        if (testQuestion.getSourceConcept() instanceof FillInTheBlankTextConcept) {
            label += messages.getQuestionGenerationViewFinishedQuestionsFillsentencesPrefix();
        } else if (testQuestion.getSourceConcept() instanceof CardinalRelationConcept) {
            label += messages.getQuestionGenerationViewFinishedQuestionsCardinalSentencesPrefix();
        }

        label += " - " + testQuestion.getQuestion();

        return label;
    }

    @Override
    public void onCompletedLearningContentSelected(final LearningContent learningContent) {
        LOGGER.info("onCompletedLearningContentSelected(): learning content {}", learningContent);

        questionGenerationView.displayGeneratedQuestions(
                questionGenerationSession.getGeneratedQuestionsContentsMap().get(learningContent)
        );
    }

    @Override
    public void onGeneratedQuestionSelected(TestQuestion testQuestion) {
        LOGGER.info("onGeneratedQuestionSelected(): test question {}", testQuestion);

        QuestionGeneratorPortletVaadinUi.getCurrentPortletVaadinUi().getQuestionGenerationEditQuestionPresenter().displayViewForTestQuestion(testQuestion);
    }

    @Override
    public void onBackButtonClicked() {
        LOGGER.info("onBackButtonClicked()");

        getNavigator().navigateTo(DetectionViewImpl.VIEW_NAME);
    }

    @Override
    public void onExportButtonClicked() {
        LOGGER.info("onExportButtonClicked()");

        CsvWriter csvWriter = new CsvWriterImpl();
        addHeaderColumnsToCsvWriter(csvWriter);
        addGeneratedQuestionsToCsvWriter(csvWriter);

        try {
            File exportFile = csvWriter.writeToFile(String.format(CSV_EXPORT_FILENAME_TEMPLATES, questionGenerationSession.getCourse().getTitle()));
            questionGenerationView.offerCsvFileForDownload(exportFile);
        } catch (Exception e) {
            LOGGER.error("onExportButtonClicked(): could not generate CSV due to {}", e);
        }
    }

    private void addHeaderColumnsToCsvWriter(CsvWriter csvWriter) {
        csvWriter.addRow(
                messages.getQuestionGenerationViewExportCsvHeaderColumnLearningContent(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnTestquestion(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnMultipleChoice(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnCorrectAnswer(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnAlternativeCorrectAnswers(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnAlternativeWrongAnswerst(),
                messages.getQuestionGenerationViewExportCsvHeaderColumnOriginalSentence()
        );
    }

    private void addGeneratedQuestionsToCsvWriter(CsvWriter csvWriter) {
        for (final LearningContent learningContent: questionGenerationSession.getGeneratedQuestionsContentsMap().keySet()) {
            final String columnLearningContentTitle = learningContent.getTitle();

            for (final TestQuestion testQuestion: questionGenerationSession.getGeneratedQuestionsContentsMap().get(learningContent)) {
                final String columnTestQuestionQuestion = testQuestion.getQuestion();
                final String columnTestQuestionOriginalSentence = testQuestion.getSourceConcept().getOriginalSentence();
                final String columnTestQuestionCorrectAnswer = testQuestion.getCorrectAnswer();
                final String columnTestQuestionAlternativeCorrectAnswers = CSV_EXPORT_MULTIPLE_VALUES_IN_ONE_COLUMN_JOINER.join(testQuestion.getAlternativeCorrectAnswers());
                final String columnTestQuestionAlternativeWrongAnswers = CSV_EXPORT_MULTIPLE_VALUES_IN_ONE_COLUMN_JOINER.join(testQuestion.getAlternativeWrongAnswers());
                final String columnIsMultipleChoice = (testQuestion.isMultipleChoice() ? messages.getQuestionGenerationViewExportCsvHeaderRowMultipleChoiceYes() : messages.getQuestionGenerationViewExportCsvHeaderRowMultipleChoiceNo());

                csvWriter.addRow(
                        columnLearningContentTitle,
                        columnTestQuestionQuestion,
                        columnIsMultipleChoice,
                        columnTestQuestionCorrectAnswer,
                        columnTestQuestionAlternativeCorrectAnswers,
                        columnTestQuestionAlternativeWrongAnswers,
                        columnTestQuestionOriginalSentence);
            }
        }
    }

    @Override
    public void onViewFocus() {
        questionGenerationView.setCurrentSessionStatus(questionGenerationSession.getStatus());
        questionGenerationView.reset();

        refreshGeneratedQuestionsLearningContents();
    }

    @Override
    public void onEditQuestionDialogClosed(TestQuestion testQuestion) {
        LOGGER.info("onEditQuestionDialogClosed(): refreshing generated questions for question {}", testQuestion);

        // rebuild test question label
        testQuestion.setLabel(buildTestQuestionLabel(testQuestion));
        refreshGeneratedQuestion(testQuestion);
    }

    private void refreshGeneratedQuestion(TestQuestion testQuestion) {
        onCompletedLearningContentSelected(testQuestion.getSourceConcept().getLearningContent());
    }

    private void refreshGeneratedQuestionsLearningContents() {
        List<LearningContent> learningContents = toList(questionGenerationSession.getGeneratedQuestionsContentsMap().keySet());
        questionGenerationView.displayCompletedLearningContents(
                learningContents
        );

        if (learningContents.size() > 0) {
            questionGenerationSession.setStatus(QuestionGenerationSession.Status.QUESTIONS_GENERATED);
        } else {
            questionGenerationSession.setStatus(QuestionGenerationSession.Status.DETECTION_DONE);
        }
    }

    private List<LearningContent> toList(Set<LearningContent> learningContents) {
        List<LearningContent> learningContentList = new ArrayList<>();

        learningContentList.addAll(learningContents);

        return learningContentList;
    }
}
