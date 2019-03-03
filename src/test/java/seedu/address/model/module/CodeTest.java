package seedu.address.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.Assert;

public class CodeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Code(null));
    }

    @Test
    public void constructor_invalidCode_throwsIllegalArgumentException() {
        String invalidCode = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Code(invalidCode));
    }

    @Test
    public void isValidCode() {
        // null code
        Assert.assertThrows(NullPointerException.class, () -> Code.isValidCode(null));

        // invalid codes
        assertFalse(Code.isValidCode("")); // empty string
        assertFalse(Code.isValidCode(" ")); // spaces only

        // valid codes
        assertTrue(Code.isValidCode("Blk 456, Den Road, #01-355"));
        assertTrue(Code.isValidCode("-")); // one character
        assertTrue(Code.isValidCode("Leng Inc; 1234 Market St; San Francisco CA 2349879; USA")); // long code
    }
}
