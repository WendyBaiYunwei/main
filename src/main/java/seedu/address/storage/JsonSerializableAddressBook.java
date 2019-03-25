package seedu.address.storage;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;

/**
 * A class to access the JsonSerializable files data stored as a json file on the hard disk.
 */
public class JsonSerializableAddressBook {

    private ObservableList<Module> moduleObservableList;
    private ObservableList<DegreePlanner> degreeObservableList;
    private ObservableList<RequirementCategory> requirementCategoryObservableList;

    public JsonSerializableAddressBook(ObservableList<Module> moduleObservableList,
            ObservableList<DegreePlanner> degreeObservableList,
            ObservableList<RequirementCategory> requirementCategoryObservableList) {
        this.moduleObservableList = moduleObservableList;
        this.degreeObservableList = degreeObservableList;
        this.requirementCategoryObservableList = requirementCategoryObservableList;
    }

    public ObservableList<Module> getModuleObservableList() {
        return moduleObservableList;
    }

    public ObservableList<DegreePlanner> getDegreePlannerObservableList() {
        return degreeObservableList;
    }

    public ObservableList<RequirementCategory> getRequirementCategoryObservableList() {
        return requirementCategoryObservableList;
    }

    public static Class<JsonSerializableModuleList> getJsonSerializableModuleListClass() {
        return JsonSerializableModuleList.class;
    }

    public static Class<JsonSerializableRequirementCategoryList> getJsonSerializableRequirementCategoryListClass() {
        return JsonSerializableRequirementCategoryList.class;
    }

    public static Class<JsonSerializableDegreePlannerList> getJsonSerializableDegreePlannerListClass() {
        return JsonSerializableDegreePlannerList.class;
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        addressBook.setModules(getModuleObservableList());
        addressBook.setDegreePlanners(getDegreePlannerObservableList());
        addressBook.setRequirementCategories(getRequirementCategoryObservableList());
        return addressBook;
    }

}
