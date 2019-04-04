package pwe.planner.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import pwe.planner.commons.core.GuiSettings;
import pwe.planner.commons.core.LogsCenter;
import pwe.planner.logic.commands.Command;
import pwe.planner.logic.commands.CommandResult;
import pwe.planner.logic.commands.exceptions.CommandException;
import pwe.planner.logic.parser.ApplicationParser;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.Model;
import pwe.planner.model.ReadOnlyApplication;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;
import pwe.planner.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final CommandHistory history;
    private final ApplicationParser applicationParser;
    private boolean applicationModified;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        history = new CommandHistory();
        applicationParser = new ApplicationParser();

        // Set applicationModified to true whenever the models' application is modified.
        model.getApplication().addListener(observable -> applicationModified = true);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        applicationModified = false;

        CommandResult commandResult;
        try {
            Command command = applicationParser.parseCommand(commandText);
            commandResult = command.execute(model, history);
        } finally {
            history.add(commandText);
        }

        if (applicationModified) {
            logger.info("Application modified, saving to file.");
            try {
                storage.saveApplication(model.getApplication());
            } catch (IOException ioe) {
                throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
            }
        }

        return commandResult;
    }

    @Override
    public ReadOnlyApplication getApplication() {
        return model.getApplication();
    }

    @Override
    public ObservableList<Module> getFilteredModuleList() {
        return model.getFilteredModuleList();
    }

    @Override
    public ObservableList<DegreePlanner> getFilteredDegreePlannerList() {
        return model.getFilteredDegreePlannerList();
    }

    @Override
    public ObservableList<RequirementCategory> getFilteredRequirementCategoryList() {
        return model.getFilteredRequirementCategoryList();
    }

    @Override
    public ObservableList<String> getHistory() {
        return history.getHistory();
    }

    @Override
    public Path getModuleListFilePath() {
        return model.getModuleListFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public ReadOnlyProperty<Module> selectedModuleProperty() {
        return model.selectedModuleProperty();
    }

    @Override
    public void setSelectedModule(Module module) {
        model.setSelectedModule(module);
    }

    @Override
    public ReadOnlyProperty<RequirementCategory> selectedRequirementCategoryProperty() {
        return model.selectedRequirementCategoryProperty();
    }

    @Override
    public void setSelectedRequirementCategory(RequirementCategory requirementCategory) {
        model.setSelectedRequirementCategory(requirementCategory);
    }

}
