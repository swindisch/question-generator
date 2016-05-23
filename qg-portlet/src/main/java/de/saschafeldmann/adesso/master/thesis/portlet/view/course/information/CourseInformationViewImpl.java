package de.saschafeldmann.adesso.master.thesis.portlet.view.course.information;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.Messages;
import de.saschafeldmann.adesso.master.thesis.portlet.view.AbstractStepView;
import de.saschafeldmann.adesso.master.thesis.portlet.view.components.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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
 * Course information dialog view.
 */
@Component
@Scope("prototype")
public class CourseInformationViewImpl extends AbstractStepView implements CourseInformationView {
    /**
     * The view mode for this view.
     */
    public enum ViewMode {
        NEW_COURSE,
        EDIT_COURSE
    }

    public static final String VIEW_NAME = "CourseInformationView";

    private ViewMode viewMode;
    private InfoBox infoBox;
    private Label introductionLabel;
    private TextField inputCourseTitle;
    private TextField inputCourseUrl;
    private ListSelect inputCourseLanguageSelect;
    private Button btnNext;
    private Button btnNewSession;
    private CourseInformationViewListener viewListener;

    @Autowired
    public CourseInformationViewImpl(final Messages messages, final InfoBox infoBox, final Label introductionLabel, final VersionLabel versionLabel, final TextField inputCourseTitle,
            final TextField inputCourseUrl,
            final ListSelect inputCourseLanguageSelect,
            final Button btnNext,
            final Button btnNewSession ) {
        super(messages, versionLabel);

        this.infoBox = infoBox;
        this.introductionLabel = introductionLabel;
        this.inputCourseTitle = inputCourseTitle;
        this.inputCourseUrl = inputCourseUrl;
        this.inputCourseLanguageSelect = inputCourseLanguageSelect;
        this.btnNext = btnNext;
        this.btnNewSession = btnNewSession;
    }

    @PostConstruct
    private void initialize() {
        this.introductionLabel.setCaption(messages.getCourseInformationViewIntroductionText());
        this.inputCourseTitle.setCaption(messages.getCourseInformationViewCourseTitleLabel());
        this.inputCourseUrl.setCaption(messages.getCourseInformationViewCourseUrlLabel());

        initializeLanguageSelect();

        this.btnNext.setCaption(messages.getCourseInformationViewBtnNextLabel());
        this.btnNewSession.setCaption(messages.getCourseInformationViewBtnNewSessionLabel());

        registerListeners();
    }

    private void initializeLanguageSelect() {
        this.inputCourseLanguageSelect.setCaption(messages.getCourseInformationViewCourseLanguageLabel());

        this.inputCourseLanguageSelect.addItem(messages.getCourseInformationViewGermanLanguageLabel());
        this.inputCourseLanguageSelect.addItem(messages.getCourseInformationViewEnglishLanguageLabel());
    }

    private void registerListeners() {
        btnNext.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                viewListener.onNextButtonClicked();
            }
        });

        btnNewSession.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent clickEvent) {
                viewListener.onNewSessionButtonClicked();
            }
        });
    }

    /**
     * @see View#enter(ViewChangeListener.ViewChangeEvent)
     */
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    /**
     * @see CourseInformationView#reset()
     */
    public void reset() {
        super.reset();

        setInfoBox();
        addComponent(infoBox);
        addComponent(introductionLabel);
        addComponent(inputCourseTitle);
        addComponent(inputCourseUrl);
        addComponent(inputCourseLanguageSelect);

        addButtonsAtBottom(btnNext, btnNewSession);
        addVersionLabel();
    }

    private void setInfoBox() {
        if (viewMode.equals(ViewMode.NEW_COURSE)) {
            this.infoBox.setCaption(messages.getCourseInformationViewNewCourseInfoText());
        } else {
            // TODO
        }
    }

    /**
     * @see CourseInformationView#getRootComponent()
     */
    public com.vaadin.ui.Component getRootComponent() {
        return this;
    }

    /**
     * Sets the view listener for this view.
     * @param viewListener the view listener instance.
     */
    public void setViewListener(CourseInformationViewListener viewListener) {
        this.viewListener = viewListener;
    }

    /**
     * Sets the view mode of this view.
     * @param viewMode see {@link ViewMode}
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }
}
