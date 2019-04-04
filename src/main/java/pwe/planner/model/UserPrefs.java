package pwe.planner.model;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import pwe.planner.commons.core.GuiSettings;

/**
 * Represents User's preferences.
 */
public class UserPrefs implements ReadOnlyUserPrefs {

    private GuiSettings guiSettings = new GuiSettings();

    private Path moduleListFilePath = Paths.get("data", "moduleList.json");
    private Path degreePlannerListFilePath = Paths.get("data", "degreePlannerList.json");
    private Path requirementCategoryListFilePath = Paths.get("data", "requirementCategoryList.json");

    /**
     * Creates a {@code UserPrefs} with default values.
     */
    public UserPrefs() {}

    /**
     * Creates a {@code UserPrefs} with the prefs in {@code userPrefs}.
     */
    public UserPrefs(ReadOnlyUserPrefs userPrefs) {
        this();
        requireNonNull(userPrefs);

        resetData(userPrefs);
    }

    /**
     * Resets the existing data of this {@code UserPrefs} with {@code newUserPrefs}.
     */
    public void resetData(ReadOnlyUserPrefs newUserPrefs) {
        requireNonNull(newUserPrefs);

        setGuiSettings(newUserPrefs.getGuiSettings());
        setModuleListFilePath(newUserPrefs.getModuleListFilePath());
        setDegreePlannerListFilePath(newUserPrefs.getDegreePlannerListFilePath());
        setRequirementCategoryListFilePath(newUserPrefs.getRequirementCategoryListFilePath());

    }

    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);

        this.guiSettings = guiSettings;
    }

    public Path getModuleListFilePath() {
        return moduleListFilePath;
    }

    public void setModuleListFilePath(Path moduleListFilePath) {
        requireNonNull(moduleListFilePath);

        this.moduleListFilePath = moduleListFilePath;
    }

    public Path getDegreePlannerListFilePath() {
        return degreePlannerListFilePath;
    }

    public void setDegreePlannerListFilePath(Path degreePlannerListFilePath) {
        requireNonNull(degreePlannerListFilePath);

        this.degreePlannerListFilePath = degreePlannerListFilePath;
    }

    public Path getRequirementCategoryListFilePath() {
        return requirementCategoryListFilePath;
    }

    public void setRequirementCategoryListFilePath(Path requirementCategoryListFilePath) {
        requireNonNull(requirementCategoryListFilePath);

        this.requirementCategoryListFilePath = requirementCategoryListFilePath;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserPrefs)) { //this handles null as well.
            return false;
        }

        UserPrefs o = (UserPrefs) other;

        return guiSettings.equals(o.guiSettings)
                && moduleListFilePath.toAbsolutePath().equals(o.moduleListFilePath.toAbsolutePath())
                && degreePlannerListFilePath.toAbsolutePath().equals(o.degreePlannerListFilePath.toAbsolutePath());

    }

    @Override
    public int hashCode() {
        return Objects.hash(guiSettings, moduleListFilePath, degreePlannerListFilePath);
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Gui Settings : ")
                .append(guiSettings)
                .append('\n')
                .append("Local data file location for module list: ")
                .append(Paths.get("").toAbsolutePath().relativize(moduleListFilePath.toAbsolutePath()))
                .append('\n')
                .append("Local data file location for requirement categories list: ")
                .append(Paths.get("").toAbsolutePath().relativize(requirementCategoryListFilePath.toAbsolutePath()))
                .append('\n')
                .append("Local data file location for degree planner list: ")
                .append(Paths.get("").toAbsolutePath().relativize(degreePlannerListFilePath.toAbsolutePath()))
                .toString();
    }

}
