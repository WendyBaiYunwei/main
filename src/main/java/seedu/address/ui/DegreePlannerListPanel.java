package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import seedu.address.model.planner.DegreePlanner;

/**
 * Panel containing the list of degreePlanners.
 */
public class DegreePlannerListPanel extends UiPart<Region> {

    private static final String FXML = "DegreePlannerListPanel.fxml";

    @FXML
    private FlowPane degreePlanners;

    public DegreePlannerListPanel(ObservableList<DegreePlanner> degreePlannerList) {
        super(FXML);
        degreePlanners.getChildren().clear();
        for (DegreePlanner degreePlanner : degreePlannerList) {
            DegreePlannerCard degreePlannerCard = new DegreePlannerCard(degreePlanner);
            degreePlanners.getChildren().add(degreePlannerCard.getRoot());
        }
    }
}
