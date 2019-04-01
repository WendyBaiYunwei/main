package seedu.address.ui;

import java.util.Comparator;
import java.util.stream.Stream;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Module;
import seedu.address.model.requirement.RequirementCategory;

/**
 * An UI component that displays information of a {@code RequirementCategory}.
 */
public class RequirementCategoryCard extends UiPart<Region> {

    private static final String FXML = "RequirementCategoryListCard.fxml";

    public final RequirementCategory requirementCategory;

    @FXML
    private HBox requirementCategoryCardPane;

    @FXML
    private Label requirementCategoryName;

    @FXML
    private Label requirementCategoryCredit;

    @FXML
    private FlowPane codes;

    public RequirementCategoryCard(RequirementCategory requirementCategory, ObservableList<Module> moduleList) {
        super(FXML);
        this.requirementCategory = requirementCategory;

        requirementCategoryName.setText(requirementCategory.getName().fullName);

        Stream<Module> modulesInRequirementCategory = requirementCategory.getCodeSet().stream()
                .map(code -> moduleList.stream().filter(module -> module.getCode().equals(code)).findFirst().get());

        int currentCredits = modulesInRequirementCategory.map(Module::getCredits).map(Credits::toString)
                .map(Integer::parseInt).reduce(0, (totalCredits, credit) -> totalCredits + credit);

        String creditsRequired = requirementCategory.getCredits().toString();

        requirementCategoryCredit.setText("Modular Credits Fulfilled: " + currentCredits + "/" + creditsRequired);

        if (currentCredits == Integer.parseInt(creditsRequired)) {
            requirementCategoryCredit.getStyleClass().clear();
            requirementCategoryCredit.getStyleClass().add("green");
        }

        if (currentCredits > Integer.parseInt(creditsRequired)) {
            requirementCategoryCredit.getStyleClass().clear();
            requirementCategoryCredit.getStyleClass().add("red");
        }

        if (requirementCategory.getCodeSet().isEmpty()) {
            Text noCodes = new Text("No modules in this category!");
            noCodes.getStyleClass().clear();
            noCodes.getStyleClass().add("noModules");
            codes.getChildren().add(noCodes);
        } else {
            requirementCategory.getCodeSet().stream().sorted(Comparator.comparing(code -> code.value))
                    .forEach(code -> codes.getChildren().add(new Label(code.value)));
        }

        requirementCategoryCardPane.setOnMouseClicked(null);

    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RequirementCategoryCard)) {
            return false;
        }

        // state check
        RequirementCategoryCard card = (RequirementCategoryCard) other;
        return requirementCategoryName.getText().equals(card.requirementCategoryName.getText());
    }
}

