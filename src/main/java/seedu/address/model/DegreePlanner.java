package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.model.module.Module;
import seedu.address.model.module.UniqueModuleList;
import seedu.address.model.module.UniquePlannerList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSameModule comparison)
 */
public class DegreePlanner implements ReadOnlyAddressBook {

    private final UniquePlannerList plannerModules;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        plannerModules = new UniquePlannerList();
    }

    public DegreePlanner() {}

    /**
     * Creates an AddressBook using the Modules in the {@code toBeCopied}
     */
    public DegreePlanner(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the module list with {@code modules}.
     * {@code modules} must not contain duplicate modules.
     */
    public void plannerSetModules(List<Module> plannerModules) {
        this.plannerModules.plannerSetModules(plannerModules);
        indicateModified();
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        plannerSetModules(newData.getModuleList());
    }

    //// module-level operations

    /**
     * Returns true if a module with the same identity as {@code module} exists in the address book.
     */
    public boolean plannerHasModule(Module module) {
        requireNonNull(module);
        return plannerModules.plannerContains(module);
    }

    /**
     * Adds a module to the address book.
     * The module must not already exist in the address book.
     */
    public void plannerAddModule(Module p) {
        plannerModules.plannerAdd(p);
        indicateModified();
    }

    /**
     * Replaces the given module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the address book.
     * The module identity of {@code editedModule} must not be the same as another existing module in the address book.
     */
    public void plannerSetModule(Module target, Module editedModule) {
        requireNonNull(editedModule);

        plannerModules.plannerSetModule(target, editedModule);
        indicateModified();
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void plannerRemoveModule(Module key) {
        plannerModules.plannerRemove(key);
        indicateModified();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the address book has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }

    //// util methods

    @Override
    public String toString() {
        return plannerModules.asPlannerUnmodifiableObservableList().size() + " modules";
        // TODO: refine later
    }

    @Override
    public ObservableList<Module> getModuleList() {
        return plannerModules.asPlannerUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof DegreePlanner // instanceof handles nulls
                && plannerModules.equals(((DegreePlanner) other).plannerModules));
    }

    @Override
    public int hashCode() {
        return plannerModules.hashCode();
    }
}
