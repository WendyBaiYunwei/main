package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;

import seedu.address.model.planner.DegreePlanner;

/**
 * Panel containing the list of degreePlanners.
 */
public class DegreePlannerListPanel extends UiPart<Region> {

    private static final String FXML = "DegreePlannerListPanel.fxml";

    @FXML
    private ListView<DegreePlanner> degreePlanners;

    public DegreePlannerListPanel(ObservableList<DegreePlanner> degreePlannerList) {
        super(FXML);
        degreePlanners.setItems(degreePlannerList);
        degreePlanners.setCellFactory(listView -> new DegreePlannerViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code DegreePlanner}
     * using a {@code DegreePlannerListCard}.
     */
    class DegreePlannerViewCell extends ListCell<DegreePlanner> {
        @Override
        protected void updateItem(DegreePlanner degreePlanner, boolean empty) {
            super.updateItem(degreePlanner, empty);

            if (empty || degreePlanner == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new DegreePlannerCard(degreePlanner).getRoot());
            }
        }
    }
}
