package de.saschafeldmann.adesso.master.thesis.portlet.view.detection;

import com.vaadin.ui.Window;
import de.saschafeldmann.adesso.master.thesis.detection.algorithm.model.api.Concept;

import java.util.List;

/**
 * Project:        Masterthesis of Sascha Feldmann
 * Creation date:  28.06.2016
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
 * A view that displays a grid of detected {@link de.saschafeldmann.adesso.master.thesis.detection.algorithm.model.api.Concept}
 * and offers an action to edit those.
 */
public interface DetectionEditConceptsView {
    /**
     * Handles the logic to display the given concepts that were detected.
     * @param detectedConcepts the concepts
     */
    void displayDetectedConcepts(List<Concept> detectedConcepts);

    /**
     * Gets the popup window to be shown.
     * @return Window
     */
    Window getWindow();

    /**
     * Sets the view listener which is informed by this view.
     * @param viewListener the view listener
     */
    void setViewListener(DetectionEditConceptsViewListener viewListener);
}