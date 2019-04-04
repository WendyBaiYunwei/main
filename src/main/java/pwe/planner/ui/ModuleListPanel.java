package pwe.planner.ui;

import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import pwe.planner.commons.core.LogsCenter;
import pwe.planner.model.module.Module;

/**
 * Panel containing the list of modules.
 */
public class ModuleListPanel extends UiPart<Region> {
    private static final String FXML = "ModuleListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(ModuleListPanel.class);

    @FXML
    private ListView<Module> moduleListView;

    public ModuleListPanel(ObservableList<Module> moduleList, ObservableValue<Module> selectedModule,
            Consumer<Module> onSelectedModuleChange) {
        super(FXML);
        requireAllNonNull(moduleList, selectedModule, onSelectedModuleChange);

        moduleListView.setItems(moduleList);
        moduleListView.setCellFactory(listView -> new ModuleListViewCell());
        moduleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            logger.fine("Selection in module list panel changed to : '" + newValue + "'");
            onSelectedModuleChange.accept(newValue);
        });
        selectedModule.addListener((observable, oldValue, newValue) -> {
            logger.fine("Selected module changed to: " + newValue);

            // Don't modify selection if we are already selecting the selected module,
            // otherwise we would have an infinite loop.
            if (Objects.equals(moduleListView.getSelectionModel().getSelectedItem(), newValue)) {
                return;
            }

            if (newValue == null) {
                moduleListView.getSelectionModel().clearSelection();
            } else {
                int index = moduleListView.getItems().indexOf(newValue);
                moduleListView.scrollTo(index);
                moduleListView.getSelectionModel().clearAndSelect(index);
            }
        });
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Module} using a {@code ModuleCard}.
     */
    class ModuleListViewCell extends ListCell<Module> {
        @Override
        protected void updateItem(Module module, boolean empty) {
            super.updateItem(module, empty);

            if (empty || module == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new ModuleCard(module, getIndex() + 1).getRoot());
            }
        }
    }

}
