package pwe.planner.storage;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.Application;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * A class to access the JsonSerializable files data stored as a json file on the hard disk.
 */
public class JsonSerializableApplication {

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

    public JsonSerializableApplication(ObservableList<Module> moduleObservableList,
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
     * Converts this application into the model's {@code Application} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Application toModelType() throws IllegalValueException {
        Application application = new Application();
        application.setModules(getModuleObservableList());
        application.setDegreePlanners(getDegreePlannerObservableList());
        application.setRequirementCategories(getRequirementCategoryObservableList());

        for (RequirementCategory requirementCategory : application.getRequirementCategoryList()) {
            for (Code code : requirementCategory.getCodeSet()) {
                if (!application.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_REQUIREMENT_CATEGORY_CODE,
                            code,
                            requirementCategory.getName()
                    ));
                }
            }
        }

        for (DegreePlanner degreePlanner : application.getDegreePlannerList()) {
            for (Code code : degreePlanner.getCodes()) {
                if (!application.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_DEGREE_PLANNER_CODE,
                            code,
                            degreePlanner.getYear(),
                            degreePlanner.getSemester()
                    ));
                }
            }
        }

        return application;
    }

}
