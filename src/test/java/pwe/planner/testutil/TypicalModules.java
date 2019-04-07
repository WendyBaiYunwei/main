package pwe.planner.testutil;

import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static pwe.planner.logic.commands.CommandTestUtil.VALID_NAME_BOB;
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

    public static final Module ALICE = new ModuleBuilder().withName("Alice Pauline")
            .withCode("CS1010")
            .withCredits("0")
            .withTags("friends").build();
    public static final Module BENSON = new ModuleBuilder().withName("Benson Meier")
            .withCode("CS1231")
            .withCredits("1")
            .withTags("owesMoney", "friends")
            .withCorequisites("CS2102")
            .build();
    public static final Module CARL = new ModuleBuilder().withName("Carl Kurz").withCredits("2")
            .withCode("CS2040C").build();
    public static final Module DANIEL = new ModuleBuilder().withName("Daniel Meier").withCredits("3")
            .withCode("CS2100").withTags("friends").build();
    public static final Module ELLE = new ModuleBuilder().withName("Elle Meyer").withCredits("4")
            .withCode("CS2101").build();
    public static final Module FIONA = new ModuleBuilder().withName("Fiona Kunz").withCredits("5")
            .withCode("CS2102").withCorequisites("CS1231").build();
    public static final Module GEORGE = new ModuleBuilder().withName("George Best").withCredits("6")
            .withCode("CS2105").build();
    public static final Module HOON = new ModuleBuilder().withName("Hoon Meier").withCredits("7")
            .withCode("CS2106").build();

    // Manually added
    public static final Module IDA = new ModuleBuilder().withName("Ida Mueller").withCredits("8")
            .withCode("CS2107").build();

    // Manually added - Module's details found in {@code CommandTestUtil}
    public static final Module AMY = new ModuleBuilder().withName(VALID_NAME_AMY).withCredits(VALID_CREDITS_AMY)
            .withCode(VALID_CODE_AMY).withTags(VALID_TAG_FRIEND).build();
    public static final Module BOB = new ModuleBuilder().withName(VALID_NAME_BOB).withCredits(VALID_CREDITS_BOB)
            .withCode(VALID_CODE_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
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
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE, HOON));
    }
}
