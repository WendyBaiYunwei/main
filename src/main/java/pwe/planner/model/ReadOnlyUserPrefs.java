package pwe.planner.model;

import java.nio.file.Path;

import pwe.planner.commons.core.GuiSettings;

/**
 * Unmodifiable view of user prefs.
 */
public interface ReadOnlyUserPrefs {

    GuiSettings getGuiSettings();

    Path getModuleListFilePath();

    Path getDegreePlannerListFilePath();

    Path getRequirementCategoryListFilePath();
}
