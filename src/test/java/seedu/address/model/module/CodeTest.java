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
        assertTrue(Code.isValidCode("CS1010"));
        assertTrue(Code.isValidCode("CS2040C")); // ends with an optional alphabet
        assertTrue(Code.isValidCode("IFS4231")); // starts with 3 alphabets
        assertTrue(Code.isValidCode("ABC1234D")); // starts with 3 alphabets and ends with optional alphabet
    }
}
