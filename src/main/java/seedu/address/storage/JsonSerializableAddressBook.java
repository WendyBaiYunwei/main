package seedu.address.storage;

import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.module.Code;
import seedu.address.model.module.Module;
import seedu.address.model.planner.DegreePlanner;
import seedu.address.model.requirement.RequirementCategory;

/**
 * A class to access the JsonSerializable files data stored as a json file on the hard disk.
 */
public class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_MODULE = "Modules list contains duplicate module(s).";
    public static final String MESSAGE_INVALID_COREQUISITE =
            "The module code (%1$s) cannot be a co-requisite of itself!";
    public static final String MESSAGE_NON_EXISTENT_REQUIREMENT_CATEGORY_CODE =
            "The module code (%1$s) in requirement category (%2$s) does not exists in the module list!";
    public static final String MESSAGE_NON_EXISTENT_DEGREE_PLANNER_CODE =
            "The module code (%1$s) in degree planner (Year %2$s Semester %3$s) does not exists in the module list!";

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

        for (RequirementCategory requirementCategory : addressBook.getRequirementCategoryList()) {
            for (Code code : requirementCategory.getCodeSet()) {
                if (!addressBook.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_REQUIREMENT_CATEGORY_CODE,
                            code,
                            requirementCategory.getName()
                    ));
                }
            }
        }

        for (DegreePlanner degreePlanner : addressBook.getDegreePlannerList()) {
            for (Code code : degreePlanner.getCodes()) {
                if (!addressBook.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_DEGREE_PLANNER_CODE,
                            code,
                            degreePlanner.getYear(),
                            degreePlanner.getSemester()
                    ));
                }
            }
        }

        return addressBook;
    }

}
