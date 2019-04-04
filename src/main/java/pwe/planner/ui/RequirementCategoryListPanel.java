package pwe.planner.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import pwe.planner.model.module.Module;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * Panel containing the list of requirement categories to be displayed.
 * This class takes in an ObservableModule list for the sole purpose of having the ObservableModule list as a
 * reference object.
 * As the ObservableRequirementCategory list only contains the module code, the ObservableModule list is necessary
 * to be able to compute the total amount of credits.
 */
public class RequirementCategoryListPanel extends UiPart<Region> {
    private static final String FXML = "RequirementCategoryListPanel.fxml";
    private ObservableList<Module> modules;


    @FXML
    private ListView<RequirementCategory> requirementCategories;

    public RequirementCategoryListPanel(ObservableList<RequirementCategory> requirementCategoryList,
            ObservableList<Module> moduleList) {
        super(FXML);
        modules = moduleList;
        requirementCategories.setItems(requirementCategoryList);
        requirementCategories.setCellFactory(listView -> new RequirementCategoryViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code RequirementCategory}
     * using a {@code RequirementCategoryListCard}.
     */
    class RequirementCategoryViewCell extends ListCell<RequirementCategory> {
        @Override
        protected void updateItem(RequirementCategory requirementCategory, boolean empty) {
            super.updateItem(requirementCategory, empty);

            if (empty || requirementCategory == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new RequirementCategoryCard(requirementCategory, modules).getRoot());
            }
        }
    }

}
