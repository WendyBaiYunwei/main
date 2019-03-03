package seedu.address.testutil;

import static seedu.address.logic.commands.CommandTestUtil.VALID_CODE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CODE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CREDITS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CREDITS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.module.Module;

/**
 * A utility class containing a list of {@code Module} objects to be used in tests.
 */
public class TypicalModules {

    public static final Module ALICE = new ModuleBuilder().withName("Alice Pauline")
            .withCode("123, Jurong West Ave 6, #08-111")
            .withCredits("94351253")
            .withTags("friends").build();
    public static final Module BENSON = new ModuleBuilder().withName("Benson Meier")
            .withCode("311, Clementi Ave 2, #02-25")
            .withCredits("98765432")
            .withTags("owesMoney", "friends").build();
    public static final Module CARL = new ModuleBuilder().withName("Carl Kurz").withCredits("95352563")
            .withCode("wall street").build();
    public static final Module DANIEL = new ModuleBuilder().withName("Daniel Meier").withCredits("87652533")
            .withCode("10th street").withTags("friends").build();
    public static final Module ELLE = new ModuleBuilder().withName("Elle Meyer").withCredits("9482224")
            .withCode("michegan ave").build();
    public static final Module FIONA = new ModuleBuilder().withName("Fiona Kunz").withCredits("9482427")
            .withCode("little tokyo").build();
    public static final Module GEORGE = new ModuleBuilder().withName("George Best").withCredits("9482442")
            .withCode("4th street").build();

    // Manually added
    public static final Module HOON = new ModuleBuilder().withName("Hoon Meier").withCredits("8482424")
            .withCode("little india").build();
    public static final Module IDA = new ModuleBuilder().withName("Ida Mueller").withCredits("8482131")
            .withCode("chicago ave").build();

    // Manually added - Module's details found in {@code CommandTestUtil}
    public static final Module AMY = new ModuleBuilder().withName(VALID_NAME_AMY).withCredits(VALID_CREDITS_AMY)
            .withCode(VALID_CODE_AMY).withTags(VALID_TAG_FRIEND).build();
    public static final Module BOB = new ModuleBuilder().withName(VALID_NAME_BOB).withCredits(VALID_CREDITS_BOB)
            .withCode(VALID_CODE_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND)
            .build();

    public static final String KEYWORD_MATCHING_MEIER = PREFIX_NAME + "Meier"; // A keyword that matches MEIER

    private TypicalModules() {} // prevents instantiation

    /**
     * Returns an {@code AddressBook} with all the typical modules.
     */
    public static AddressBook getTypicalAddressBook() {
        AddressBook ab = new AddressBook();
        for (Module module : getTypicalModules()) {
            ab.addModule(module);
        }
        return ab;
    }

    public static List<Module> getTypicalModules() {
        return new ArrayList<>(Arrays.asList(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE));
    }
}
