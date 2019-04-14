package pwe.planner.storage;

import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Set;

import javafx.collections.ObservableList;
import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.commons.util.StringUtil;
import pwe.planner.model.Application;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Module;
import pwe.planner.model.planner.DegreePlanner;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.requirement.RequirementCategory;

/**
 * A class to access the JsonSerializable* files data stored as a json file on the hard disk.
 */
public class JsonSerializableApplication {
    public static final String MESSAGE_NON_EXISTENT_REQUIREMENT_CATEGORY_CODE =
            "The module code (%1$s) in requirement category (%2$s) does not exists in the module list!";
    public static final String MESSAGE_NON_EXISTENT_DEGREE_PLANNER_CODE =
            "The module code (%1$s) in degree planner (Year %2$s Semester %3$s) does not exists in the module list!";
    public static final String MESSAGE_INVALID_DEGREE_PLANNER_MODULE_SEMESTER =
            "The module code (%1$s) is added to Year %2$s Semester %3$s of the degree plan, but the module is offered "
            + "only in Semesters %4$s!\n";
    public static final String MESSAGE_INVALID_DEGREE_PLANNER_EMPTY_MODULE_SEMESTERS =
            "The module code (%1$s) is added to Year %2$s Semester %3$s of the degree plan, but the module is "
            + "not offered in any semesters!";
    public static final String MESSAGE_MISSING_COREQUISITES_IN_DEGREE_PLANNER =
            "The module codes (%1$s) and (%2$s) are co-requisites, but (%2$s) is not added to the degree plan!";
    public static final String MESSAGE_INVALID_COREQUISITES_IN_DEGREE_PLANNER =
            "The module codes (%1$s) and (%2$s) are co-requisites, but are not added to the same year/semester in the "
            + "degree plan!\n"
            + "Module code %1$s is in Year %3$s Semester %4$s, and module code (%2$s) is in Year %5$s Semester %6$s.";

    private ObservableList<Module> moduleObservableList;
    private ObservableList<DegreePlanner> degreeObservableList;
    private ObservableList<RequirementCategory> requirementCategoryObservableList;

    public JsonSerializableApplication(ObservableList<Module> moduleObservableList,
            ObservableList<DegreePlanner> degreeObservableList,
            ObservableList<RequirementCategory> requirementCategoryObservableList) {
        requireAllNonNull(moduleObservableList, degreeObservableList, requirementCategoryObservableList);

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
     * Converts the application into the model's {@link Application} object.<br>
     * Checks for additional data constraints on top of {@link JsonSerializableModuleList#toModelType()},
     * {@link JsonSerializableRequirementCategoryList#toModelType()} and
     * {@link JsonSerializableDegreePlannerList#toModelType()}.
     * <br><br>
     * Data constraints:<br>
     * 1. All codes in requirement categories must exist in module list.<br>
     * 2. All codes in degree planners must exist in module list.<br>
     * 3. All modules in degree planners must be taken only in semesters the module is offered in.<br>
     * 4. All modules in degree planners must have all their co-requisites in the same semester.<br>
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public Application toModelType() throws IllegalValueException {
        ObservableList<Module> modules = getModuleObservableList();
        ObservableList<RequirementCategory> requirementCategories = getRequirementCategoryObservableList();
        ObservableList<DegreePlanner> degreePlanners = getDegreePlannerObservableList();

        Application application = new Application();
        application.setModules(modules);
        application.setRequirementCategories(requirementCategories);
        application.setDegreePlanners(degreePlanners);

        // 1. Ensure that all codes in requirement categories exist in module list
        for (RequirementCategory requirementCategory : requirementCategories) {
            for (Code code : requirementCategory.getCodeSet()) {
                if (!application.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_REQUIREMENT_CATEGORY_CODE, code,
                            requirementCategory.getName()));
                }
            }
        }

        // 2. Ensure that all codes in degree planner exist in module list
        for (DegreePlanner degreePlanner : degreePlanners) {
            for (Code code : degreePlanner.getCodes()) {
                if (!application.hasModuleCode(code)) {
                    throw new IllegalValueException(String.format(MESSAGE_NON_EXISTENT_DEGREE_PLANNER_CODE, code,
                            degreePlanner.getYear(), degreePlanner.getSemester()
                    ));
                }

                // 3. Ensure that all modules in degree planners are taken only in semesters the module is offered in.
                Module module = application.getModuleByCode(code);
                Set<Semester> semesters = module.getSemesters();

                if (!semesters.contains(degreePlanner.getSemester())) {
                    if (semesters.isEmpty()) {
                        throw new IllegalValueException(String.format(
                                MESSAGE_INVALID_DEGREE_PLANNER_EMPTY_MODULE_SEMESTERS, code, degreePlanner.getYear(),
                                degreePlanner.getSemester()));
                    } else {
                        String semestersOfferingModule = StringUtil.joinStreamAsString(semesters.stream().sorted());
                        throw new IllegalValueException(String.format(MESSAGE_INVALID_DEGREE_PLANNER_MODULE_SEMESTER,
                                code, degreePlanner.getYear(), degreePlanner.getSemester(), semestersOfferingModule));
                    }
                }

                // 4. Ensure that all modules in degree planners have all their co-requisites in the same semester.
                Set<Code> corequisites = module.getCorequisites();
                for (Code corequisite : corequisites) {
                    DegreePlanner corequisiteDegreePlanner = application.getDegreePlannerByCode(corequisite);
                    if (corequisiteDegreePlanner == null) {
                        throw new IllegalValueException(String.format(MESSAGE_MISSING_COREQUISITES_IN_DEGREE_PLANNER,
                                code, corequisite));
                    } else if (!degreePlanner.isSameDegreePlanner(corequisiteDegreePlanner)) {
                        throw new IllegalValueException(String.format(MESSAGE_INVALID_COREQUISITES_IN_DEGREE_PLANNER,
                                code, corequisite, degreePlanner.getYear(), degreePlanner.getSemester(),
                                corequisiteDegreePlanner.getYear(), corequisiteDegreePlanner.getSemester()));
                    }
                }
            }
        }

        return application;
    }
}
