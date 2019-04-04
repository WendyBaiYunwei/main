package pwe.planner.ui;

import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;

/**
 * An UI component that displays information of a {@code DegreePlanner}.
 */
public class DegreePlannerCard extends UiPart<Region> {

    private static final String FXML = "DegreePlannerListCard.fxml";
    private static final Integer MINIMUM_LOAD = 18;
    private static final Integer OVER_LOAD = 24;

    public final DegreePlanner degreePlanner;

    @FXML
    private StackPane degreePlannerCardPane;

    @FXML
    private Label year;

    @FXML
    private Label semester;

    @FXML
    private Label credits;

    @FXML
    private VBox degreePlannerListView;

    public DegreePlannerCard(DegreePlanner degreePlanner, ObservableList<Module> moduleList) {
        super(FXML);
        requireAllNonNull(degreePlanner, moduleList);

        this.degreePlanner = degreePlanner;

        year.setText("Year: " + degreePlanner.getYear().year);
        year.setPadding(new Insets(0, 0, 0, 5));
        semester.setText(" Semester: " + degreePlanner.getSemester().plannerSemester);

        List<Module> modulesInDegreePlanner = degreePlanner.getCodes().stream()
                .map(code -> moduleList.stream().filter(module -> module.getCode().equals(code))
                        .findFirst().get()).collect(Collectors.toList());


        int currentCredits = modulesInDegreePlanner.stream().map(Module::getCredits).map(Credits::toString)
                .map(Integer::parseInt).reduce(0, (totalCredits, credit) -> totalCredits + credit);

        credits.setText("Total Credits: " + currentCredits + " MCs");
        credits.setPadding(new Insets(0, 0, 0, 5));
        credits.getStyleClass().clear();
        if (currentCredits < MINIMUM_LOAD) {
            credits.getStyleClass().add("orange");
        } else if (currentCredits < OVER_LOAD) {
            credits.getStyleClass().add("green");
        } else {
            credits.getStyleClass().add("red");
        }

        modulesInDegreePlanner.stream().sorted(Comparator.comparing(module -> module.getCode().toString()))
                .forEach(module -> {
                    VBox vbox = new VBox();
                    VBox.setMargin(vbox, new Insets(1, 1, 1, 1));
                    vbox.getChildren().add(new Label(
                            module.getCode().value + " " + module.getName().toString()));
                    vbox.getStyleClass().add("myModule");
                    degreePlannerListView.getChildren().add(vbox);
                });

        degreePlannerCardPane.setOnMouseClicked(null);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DegreePlannerCard)) {
            return false;
        }

        // state check
        DegreePlannerCard card = (DegreePlannerCard) other;
        return year.getText().equals(card.year.getText());
    }
}

