package pwe.planner.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.model.module.Module;

/**
 * An UI component that displays information of a {@code Module}.
 */
public class ModuleCard extends UiPart<Region> {

    private static final String FXML = "ModuleListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Module module;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label code;
    @FXML
    private Label name;
    @FXML
    private Label credits;
    @FXML
    private Label semesters;
    @FXML
    private Label corequisites;
    @FXML
    private FlowPane tags;

    public ModuleCard(Module module, int displayedIndex) {
        super(FXML);
        requireNonNull(module);

        this.module = module;
        id.setText(displayedIndex + ". ");
        name.setText(module.getName().fullName);
        credits.setText("Modular Credits: " + module.getCredits().value);
        code.setText(module.getCode().value);

        String semestersText = StringUtil.joinStreamAsString(module.getSemesters().stream().sorted());
        semesters.setText("Offered in Semesters: " + semestersText);

        String corequisitesText = StringUtil.joinStreamAsString(module.getCorequisites().stream().sorted());
        corequisites.setText("Co-requisites: " + corequisitesText);
        module.getTags().stream().sorted().forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModuleCard)) {
            return false;
        }

        // state check
        ModuleCard card = (ModuleCard) other;
        return id.getText().equals(card.id.getText())
                && module.equals(card.module);
    }
}
