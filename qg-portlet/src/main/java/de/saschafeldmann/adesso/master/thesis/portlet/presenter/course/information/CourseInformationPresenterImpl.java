package de.saschafeldmann.adesso.master.thesis.portlet.presenter.course.information;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Notification;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.Course;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.Language;
import de.saschafeldmann.adesso.master.thesis.portlet.model.QuestionGenerationSession;
import de.saschafeldmann.adesso.master.thesis.portlet.presenter.AbstractStepPresenter;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.Messages;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.contents.CourseContentsViewImpl;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.information.CourseInformationView;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.information.CourseInformationViewImpl;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.information.CourseInformationViewListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Project:        Masterthesis of Sascha Feldmann
 * Creation date:  23.05.2016
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
 * CourseInformationPresenterImpl: the presenter implementation which manages the {@link CourseInformationViewImpl} and
 * implements the {@link CourseInformationViewListener} so that the view can notify the presenter on several actions.
 */
@Component
@Scope("prototype")
public class CourseInformationPresenterImpl extends AbstractStepPresenter implements CourseInformationPresenter, CourseInformationViewListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseInformationPresenterImpl.class);

    private final CourseInformationView courseInformationView;
    private Course courseModel;
    private Messages messages;

    /**
     * Creates a new CourseInformationPresenterImpl.
     * @param courseInformationViewImpl the managed view.
     */
    @Autowired
    public CourseInformationPresenterImpl(final CourseInformationViewImpl courseInformationViewImpl, final Messages messages) {
        if (null == courseInformationViewImpl) {
            throw new NullPointerException("The argument courseInformationView must not be null!");
        }

        this.courseInformationView = courseInformationViewImpl;
        this.messages = messages;
    }

    /**
     * @see CourseInformationPresenter#initializeView()
     */
    public CourseInformationView initializeView() {
        this.courseInformationView.setMenuListener(this);
        this.courseInformationView.setCurrentSessionStatus(questionGenerationSession.getStatus());
        this.courseInformationView.setViewListener(this);
        this.courseInformationView.setViewMode(CourseInformationViewImpl.ViewMode.NEW_COURSE);
        this.courseInformationView.reset();
        return this.courseInformationView;
    }

    /**
     * @see CourseInformationViewListener#onNextButtonClicked()
     */
    public void onNextButtonClicked() {
        LOGGER.info("onNextButtonClicked()");

        try {
            updateCourseModel();

            getNavigator().navigateTo(CourseContentsViewImpl.VIEW_NAME);
        } catch (Exception e) {
            LOGGER.error("onNextButtonClicked(): could not create or update the course - exception {} occured:\n{}",
                    e.getMessage(), ExceptionUtils.getStackTrace(e));

            Notification.show(
                    messages.getCourseInformationViewErrorNotificationTitle(),
                    messages.getCourseInformationViewErrorNotificationText(),
                    Notification.Type.ERROR_MESSAGE
            );
        }
    }

    /**
     * Creates or updates the course model.
     */
    private void updateCourseModel() {
        this.courseModel = new Course.CourseBuilder()
                .withTitle(courseInformationView.getInputTitle())
                .withViewUrl(courseInformationView.getInputViewUrl())
                .withLanguage(getLanguage(courseInformationView.getInputLanguage()))
                .build();

        this.questionGenerationSession.setCourse(courseModel);
    }

    private Language getLanguage(String inputLanguage) {
        if (inputLanguage.equals(messages.getCourseInformationViewEnglishLanguageLabel())) {
            return Language.ENGLISH;
        } else {
            return Language.GERMAN;
        }
    }

    /**
     * @see CourseInformationViewListener#onNewSessionButtonClicked()
     */
    public void onNewSessionButtonClicked() {
        LOGGER.info("onNewSessionButtonClicked()");
    }

    /**
     * @see CourseInformationViewListener#onViewFocus()
     */
    public void onViewFocus() {
        LOGGER.info("onViewFocus()");

        if (questionGenerationSession.getStatus().equals(QuestionGenerationSession.Status.STARTED)) {
            this.courseInformationView.setViewMode(CourseInformationViewImpl.ViewMode.NEW_COURSE);
        } else {
            this.courseInformationView.setCourseTitle(questionGenerationSession.getCourse().getTitle());
            this.courseInformationView.setViewMode(CourseInformationViewImpl.ViewMode.EDIT_COURSE);
        }

        this.courseInformationView.setCurrentSessionStatus(questionGenerationSession.getStatus());
        this.courseInformationView.reset();
    }

}
