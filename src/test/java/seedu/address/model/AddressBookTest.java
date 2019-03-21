package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.TypicalModules.ALICE;
import static seedu.address.testutil.TypicalModules.getTypicalModuleList;
import static seedu.address.testutil.TypicalRequirementCategories.getTypicalRequirementCategoriesList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.module.Module;
import seedu.address.model.module.exceptions.DuplicateModuleException;
import seedu.address.model.requirement.RequirementCategory;
import seedu.address.storage.JsonSerializableAddressBook;
import seedu.address.testutil.ModuleBuilder;

public class AddressBookTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), addressBook.getModuleList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        addressBook.resetData(null);
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() throws IllegalValueException {
        AddressBook newData =
                new JsonSerializableAddressBook(getTypicalModuleList(), getTypicalRequirementCategoriesList())
                        .toModelType();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicateModules_throwsDuplicateModuleException() {
        // Two modules with the same identity fields
        Module editedAlice = new ModuleBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Module> newModules = Arrays.asList(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newModules);

        thrown.expect(DuplicateModuleException.class);
        addressBook.resetData(newData);
    }

    @Test
    public void hasModule_nullModule_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        addressBook.hasModule(null);
    }

    @Test
    public void hasModule_moduleNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasModule(ALICE));
    }

    @Test
    public void hasModule_moduleInAddressBook_returnsTrue() {
        addressBook.addModule(ALICE);
        assertTrue(addressBook.hasModule(ALICE));
    }

    @Test
    public void hasModule_moduleWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addModule(ALICE);
        Module editedAlice = new ModuleBuilder(ALICE).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasModule(editedAlice));
    }

    @Test
    public void getModuleList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        addressBook.getModuleList().remove(0);
    }

    @Test
    public void addListener_withInvalidationListener_listenerAdded() {
        SimpleIntegerProperty counter = new SimpleIntegerProperty();
        InvalidationListener listener = observable -> counter.set(counter.get() + 1);
        addressBook.addListener(listener);
        addressBook.addModule(ALICE);
        assertEquals(1, counter.get());
    }

    @Test
    public void removeListener_withInvalidationListener_listenerRemoved() {
        SimpleIntegerProperty counter = new SimpleIntegerProperty();
        InvalidationListener listener = observable -> counter.set(counter.get() + 1);
        addressBook.addListener(listener);
        addressBook.removeListener(listener);
        addressBook.addModule(ALICE);
        assertEquals(0, counter.get());
    }

    /**
     * A stub ReadOnlyAddressBook whose modules list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Module> modules = FXCollections.observableArrayList();
        private final ObservableList<RequirementCategory> requirementCategories = FXCollections.observableArrayList();

        AddressBookStub(Collection<Module> modules) {
            this.modules.setAll(modules);
        }

        @Override
        public ObservableList<Module> getModuleList() {
            return modules;
        }

        @Override
        public ObservableList<RequirementCategory> getRequirementCategoryList() {
            return requirementCategories;
        }

        @Override
        public void addListener(InvalidationListener listener) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            throw new AssertionError("This method should not be called.");
        }
    }

}
