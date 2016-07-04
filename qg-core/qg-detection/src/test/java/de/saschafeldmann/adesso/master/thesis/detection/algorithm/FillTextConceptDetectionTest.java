package de.saschafeldmann.adesso.master.thesis.detection.algorithm;

import static org.junit.Assert.*;

import de.saschafeldmann.adesso.master.thesis.detection.algorithm.filltext.FillTextConceptDetection;
import de.saschafeldmann.adesso.master.thesis.detection.model.FillTextConcept;
import de.saschafeldmann.adesso.master.thesis.detection.model.api.Concept;
import de.saschafeldmann.adesso.master.thesis.detection.util.DetectionProperties;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.Language;
import de.saschafeldmann.adesso.master.thesis.elearningimport.model.LearningContent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Test of the {@link de.saschafeldmann.adesso.master.thesis.detection.algorithm.filltext.FillTextConceptDetection} algorithm.
 */
public class FillTextConceptDetectionTest {

    private static final String GERMAN_GEOGRAPHY_TEXT = "Die Bundesrepublik Deutschland liegt in Europa. Die Hauptstadt von Deutschland ist Berlin.";
    private static final String[] GERMAN_GEOGRAPHY_POS_TEXT = {"<ART>Die</ART><NN>Bundesrepublik</NN><NE>Deutschland</NE><VVFIN>liegt</VVFIN><APPR>in</APPR><NE>Europa</NE><$.>.</$.>",
                                                                "<ART>Die</ART><NN>Hauptstadt</NN><APPR>von</APPR><NE>Deutschland</NE><VAFIN>ist</VAFIN><NE>Berlin</NE><$.>.</$.>"};
    private static final String[] GERMAN_GEOGRAPHY_NER_TEXT = {"<O>Die</O><I-LOC>Bundesrepublik</I-LOC><I-LOC>Deutschland</I-LOC><O>liegt</O><O>in</O><I-LOC>Europa</I-LOC><O>.</O>",
                                                                "<O>Die</O><O>Hauptstadt</O><O>von</O><I-LOC>Deutschland</I-LOC><O>ist</O><I-LOC>Berlin</I-LOC><O>.</O>"};

    @Test
    public void testFillTextConceptDetectionDetectsGermanGeographyConcepts() throws Exception {
        // given a German geography learning content and the algorithm
        LearningContent learningContent = newLearningContent(GERMAN_GEOGRAPHY_TEXT, new ArrayList<String>(Arrays.asList(GERMAN_GEOGRAPHY_POS_TEXT)),
                new ArrayList<String>(Arrays.asList(GERMAN_GEOGRAPHY_NER_TEXT)), Language.GERMAN);
        DetectionAlgorithm<FillTextConcept> algorithm = newFillTextConceptAlgorithm();

        // when the algorithm is called
        List<FillTextConcept> detectedFillTextConcepts = algorithm.execute(learningContent, new DetectionOptions());

        // then make sure that the expected concepts were detected
        assertTrue("At least one fill text concept should have been detected", detectedFillTextConcepts.size() > 0);
    }

    private LearningContent newLearningContent(String germanGeographyText, List<String> germanGeographyPosText, List<String> germanGeographyNerText, Language givenLanguage) {
        final LearningContent learningContent = new LearningContent.LearningContentBuilder()
                .withRawText(germanGeographyText)
                .withTitle("FillTextConceptDetectionTest test content")
                .withType(LearningContent.Type.DIRECT_RAWTEXT)
                .build();

        learningContent.setPartOfSpeechAnnotatedText(germanGeographyPosText);
        learningContent.setNamedEntityAnnotatedText(germanGeographyNerText);
        learningContent.setDeterminedLanguage(givenLanguage);

        return learningContent;
    }


    private DetectionAlgorithm<FillTextConcept> newFillTextConceptAlgorithm() throws Exception {
        DetectionProperties properties = new DetectionProperties();
        return new FillTextConceptDetection(properties);
    }
}
