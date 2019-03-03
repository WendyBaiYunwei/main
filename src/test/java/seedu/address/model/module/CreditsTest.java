package seedu.address.model.module;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.Assert;

public class CreditsTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Credits(null));
    }

    @Test
    public void constructor_invalidCredits_throwsIllegalArgumentException() {
        String invalidCredits = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Credits(invalidCredits));
    }

    @Test
    public void isValidCredits() {
        // null credits
        Assert.assertThrows(NullPointerException.class, () -> Credits.isValidCredits(null));

        // invalid credits
        assertFalse(Credits.isValidCredits("")); // empty string
        assertFalse(Credits.isValidCredits(" ")); // spaces only
        assertFalse(Credits.isValidCredits("91")); // less than 3 numbers
        assertFalse(Credits.isValidCredits("credits")); // non-numeric
        assertFalse(Credits.isValidCredits("9011p041")); // alphabets within digits
        assertFalse(Credits.isValidCredits("9312 1534")); // spaces within digits

        // valid credits
        assertTrue(Credits.isValidCredits("911")); // exactly 3 numbers
        assertTrue(Credits.isValidCredits("93121534"));
        assertTrue(Credits.isValidCredits("124293842033123")); // long numbers
    }
}
