package de.saschafeldmann.adesso.master.thesis.portlet;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedPortletSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import de.saschafeldmann.adesso.master.thesis.portlet.presenter.course.information.CourseInformationPresenter;
import de.saschafeldmann.adesso.master.thesis.portlet.presenter.course.information.CourseInformationPresenterImpl;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.VaadinProperties;
import de.saschafeldmann.adesso.master.thesis.portlet.properties.VersionProperties;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.information.CourseInformationView;
import de.saschafeldmann.adesso.master.thesis.portlet.view.course.information.CourseInformationViewImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;

/**
 * Project:        Masterthesis of Sascha Feldmann
 * Creation date:  17.05.2016
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
 * This class defines the (vaadin) portlet. It is the single entry point for the portlet handling logic.
 */
@Theme(VaadinProperties.THEME)
public class QuestionGeneratorPortlet extends UI {
    private Navigator viewNavigator;
    private CourseInformationView courseInformationView;
    private ApplicationContext applicationContext;

    private ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        initializeApplicationContext(vaadinRequest);
        initializeViews();

        setContent(courseInformationView.getRootComponent());
    }

    private void initializeApplicationContext(VaadinRequest vaadinRequest) {
        WrappedPortletSession wrappedPortletSession = (WrappedPortletSession) vaadinRequest.getWrappedSession();

        this.applicationContext = PortletApplicationContextUtils.getRequiredWebApplicationContext(wrappedPortletSession.getPortletSession().getPortletContext());
    }

    private void initializeViews() {
        CourseInformationPresenter courseInformationPresenter = getApplicationContext().getBean(CourseInformationPresenterImpl.class);

        this.courseInformationView = courseInformationPresenter.initializeView();
        this.viewNavigator.addView(CourseInformationViewImpl.VIEW_NAME, courseInformationView);
    }
}
