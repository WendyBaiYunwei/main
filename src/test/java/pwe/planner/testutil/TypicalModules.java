package pwe.planner.testutil;

import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY_ONE;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_AMY_TWO;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_FOUR;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_SEMESTER_BOB_THREE;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.ObservableList;
import pwe.planner.model.Application;
import pwe.planner.model.module.Module;

/**
 * A utility class containing a list of {@code Module} objects to be used in tests.
 */
public class TypicalModules {

    public static final Module ALICE = new ModuleBuilder()
            .withCode("CS1010")
            .withName("Alice Pauline")
            .withCredits("0")
            .withSemesters("1", "2", "4")
            .withTags("friends")
            .build();

    public static final Module BENSON = new ModuleBuilder()
            .withCode("CS1231")
            .withName("Benson Meier")
            .withCredits("1")
            .withSemesters("1", "2")
            .withCorequisites("CS2102")
            .withTags("owesMoney", "friends")
            .build();

    public static final Module CARL = new ModuleBuilder()
            .withCode("CS2040C")
            .withName("Carl Kurz")
            .withCredits("2")
            .withSemesters("1", "2")
            .build();

    public static final Module DANIEL = new ModuleBuilder()
            .withCode("CS2100")
            .withName("Daniel Meier")
            .withCredits("3")
            .withSemesters("1", "2")
            .withTags("friends")
            .build();

    public static final Module ELLE = new ModuleBuilder()
            .withCode("CS2101")
            .withName("Elle Meyer")
            .withCredits("4")
            .withSemesters("1", "2")
            .build();

    public static final Module FIONA = new ModuleBuilder()
            .withCode("CS2102")
            .withName("Fiona Kunz")
            .withCredits("5")
            .withSemesters("1")
            .withCorequisites("CS1231")
            .build();

    public static final Module GEORGE = new ModuleBuilder()
            .withCode("CS2105")
            .withName("George Best")
            .withCredits("6")
            .withSemesters("1", "2")
            .build();

    // Manually added
    public static final Module HOON = new ModuleBuilder()
            .withName("Hoon Meier")
            .withCredits("7")
            .withCode("CS2106")
            .withSemesters("3")
            .build();

    public static final Module IDA = new ModuleBuilder()
            .withName("Ida Mueller")
            .withCredits("8")
            .withCode("CS2107")
            .withSemesters("4")
            .build();

    // Manually added - Module's details found in {@code CommandTestUtil}
    public static final Module AMY = new ModuleBuilder()
            .withCode(VALID_CODE_AMY)
            .withName(VALID_NAME_AMY)
            .withCredits(VALID_CREDITS_AMY)
            .withSemesters(VALID_SEMESTER_AMY_ONE, VALID_SEMESTER_AMY_TWO)
            .withTags(VALID_TAG_FRIEND)
            .build();

    public static final Module BOB = new ModuleBuilder()
            .withCode(VALID_CODE_BOB)
            .withName(VALID_NAME_BOB)
            .withCredits(VALID_CREDITS_BOB)
            .withSemesters(VALID_SEMESTER_BOB_THREE, VALID_SEMESTER_BOB_FOUR)
            .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = PREFIX_NAME + "Meier"; // A keyword that matches MEIER

    private TypicalModules() {} // prevents instantiation

    /**
     * Returns an {@code Application} with all the typical modules.
     */
    public static ObservableList<Module> getTypicalModuleList() {
        Application moduleList = new Application();
        for (Module module : getTypicalModules()) {
            moduleList.addModule(module);
        }
        return moduleList.getModuleList();
    }


    public static List<Module> getTypicalModules() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
