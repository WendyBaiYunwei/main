package pwe.planner.model.module;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pwe.planner.model.module.exceptions.DuplicateModuleException;
import pwe.planner.model.module.exceptions.ModuleNotFoundException;

/**
 * A list of modules that enforces uniqueness between its elements and does not allow nulls.
 * A module is considered unique by comparing using {@code Module#isSameModule(Module)}. As such, adding and updating of
 * modules uses Module#isSameModule(Module) for equality so as to ensure that the module being added or updated is
 * unique in terms of identity in the UniqueModuleList. However, the removal of a module uses Module#equals(Object) so
 * as to ensure that the module with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Module#isSameModule(Module)
 */
public class UniqueModuleList implements Iterable<Module> {

    private final ObservableList<Module> internalList = FXCollections.observableArrayList();
    private final ObservableList<Module> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent module as the given argument.
     */
    public boolean contains(Module toCheck) {
        requireNonNull(toCheck);

        return internalList.stream().anyMatch(toCheck::isSameModule);
    }

    /**
     * Returns a module object if the list contains an equivalent module code as the given argument.
     */
    public Module getModuleByCode(Code toCheck) {
        requireNonNull(toCheck);

        return internalList.stream()
                .filter(module -> module.getCode().equals(toCheck))
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a module to the list.
     * The module must not already exist in the list.
     */
    public void add(Module toAdd) {
        requireNonNull(toAdd);

        if (contains(toAdd)) {
            throw new DuplicateModuleException();
        }
        internalList.add(toAdd);

        cascadeAddModuleCorequisites(toAdd);
    }

    /**
     * Combines all co-requisites linked to current module into one {@code Set<Code>}, and update all linked modules to
     * have all-corequisites
     *
     * @param moduleToAdd
     */
    private void cascadeAddModuleCorequisites(Module moduleToAdd) {
        assert moduleToAdd != null;

        // create a union Set<Code> of co-requisites
        Code currentCode = moduleToAdd.getCode();
        Set<Code> currentCorequisites = moduleToAdd.getCorequisites();
        Set<Code> allCorequisites = new HashSet<>(currentCorequisites);

        for (Code codeToAdd : currentCorequisites) {
            Optional<Module> otherModuleOptional = internalUnmodifiableList.stream()
                    .filter(module -> module.getCode().equals(codeToAdd))
                    .findFirst();

            if (otherModuleOptional.isPresent()) {
                allCorequisites.addAll(otherModuleOptional.get().getCorequisites());
            }
        }

        allCorequisites.add(currentCode);

        // update all co-requisite modules with the union set excluding itself
        for (Code codeToEditCorequisites : allCorequisites) {
            Optional<Module> otherModuleOptional = internalUnmodifiableList.stream()
                    .filter(module -> module.getCode().equals(codeToEditCorequisites))
                    .findFirst();

            if (otherModuleOptional.isPresent()) {
                Module otherModule = otherModuleOptional.get();
                Set<Code> editedOtherCorequisites = new HashSet<>(allCorequisites);
                editedOtherCorequisites.remove(otherModule.getCode());

                Module editedOtherModule = new Module(
                        otherModule.getCode(),
                        otherModule.getName(),
                        otherModule.getCredits(),
                        otherModule.getSemesters(),
                        editedOtherCorequisites,
                        otherModule.getTags()
                );

                setModule(otherModule, editedOtherModule, false);
            }
        }
    }

    /**
     * Replaces the module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the list.
     * The module identity of {@code editedModule} must not be the same as another existing module in the list.
     */
    public void setModule(Module target, Module editedModule) {
        requireAllNonNull(target, editedModule);

        setModule(target, editedModule, true);
    }


    /**
     * Replaces the module {@code target} in the list with {@code editedModule}.
     * {@code target} must exist in the list.
     * The module identity of {@code editedModule} must not be the same as another existing module in the list.
     */
    private void setModule(Module target, Module editedModule, boolean cascade) {
        requireAllNonNull(target, editedModule);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ModuleNotFoundException();
        }

        if (!target.isSameModule(editedModule) && contains(editedModule)) {
            throw new DuplicateModuleException();
        }

        internalList.set(index, editedModule);

        if (cascade) {
            if (!target.getCode().equals(editedModule.getCode())) {
                cascadeEditModuleCorequisites(target, editedModule);
            }
            cascadeDeleteModuleCorequisites(target);
            cascadeAddModuleCorequisites(editedModule);
        }
    }

    /**
     * Cascades the edited module code by updating {@code UniqueModuleList} accordingly
     * @param target module code to edit/find
     * @param editedModule module code to replace with
     */
    private void cascadeEditModuleCorequisites(Module target, Module editedModule) {
        assert target != null;
        assert editedModule != null;

        ObservableList<Module> modules = internalUnmodifiableList;
        Code codeToEdit = target.getCode();
        Code editedCode = editedModule.getCode();

        for (Module module : modules) {
            if (module.getCorequisites().contains(codeToEdit)) {
                Set<Code> editedCorequisiteCodes = new HashSet<>(module.getCorequisites());
                editedCorequisiteCodes.remove(codeToEdit);
                editedCorequisiteCodes.add(editedCode);

                Module editedCorequisiteModule = new Module(
                        module.getCode(),
                        module.getName(),
                        module.getCredits(),
                        module.getSemesters(),
                        editedCorequisiteCodes,
                        module.getTags()
                );

                setModule(module, editedCorequisiteModule, false);
            }
        }
    }

    /**
     * Removes the equivalent module from the list.
     * The module must exist in the list.
     */
    public void remove(Module toRemove) {
        requireNonNull(toRemove);

        if (!internalList.remove(toRemove)) {
            throw new ModuleNotFoundException();
        }

        cascadeDeleteModuleCorequisites(toRemove);
    }

    /**
     * Cascades the deleted module code by removing it from {@code UniqueModuleList} accordingly
     * @param moduleToDelete module code to delete
     */
    private void cascadeDeleteModuleCorequisites(Module moduleToDelete) {
        assert moduleToDelete != null;

        ObservableList<Module> modules = internalUnmodifiableList;
        Code codeToDelete = moduleToDelete.getCode();

        for (Module module : modules) {
            if (module.getCorequisites().contains(codeToDelete)) {
                Set<Code> editedCorequisiteCodes = new HashSet<>(module.getCorequisites());
                editedCorequisiteCodes.remove(codeToDelete);

                Module editedModule = new Module(
                        module.getCode(),
                        module.getName(),
                        module.getCredits(),
                        module.getSemesters(),
                        editedCorequisiteCodes,
                        module.getTags()
                );

                setModule(module, editedModule, false);
            }
        }
    }

    public void setModules(UniqueModuleList replacement) {
        requireNonNull(replacement);

        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code modules}.
     * {@code modules} must not contain duplicate modules.
     */
    public void setModules(List<Module> modules) {
        requireAllNonNull(modules);

        if (!modulesAreUnique(modules)) {
            throw new DuplicateModuleException();
        }

        internalList.setAll(modules);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Module> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Module> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueModuleList // instanceof handles nulls
                    && internalList.equals(((UniqueModuleList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code modules} contains only unique modules.
     */
    private boolean modulesAreUnique(List<Module> modules) {
        for (int i = 0; i < modules.size() - 1; i++) {
            for (int j = i + 1; j < modules.size(); j++) {
                if (modules.get(i).isSameModule(modules.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
