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
        assertFalse(Credits.isValidCredits("+10")); // no plus sign
        assertFalse(Credits.isValidCredits("-1")); // no negative sign
        assertFalse(Credits.isValidCredits("00")); // no leading zero (for 1 zero)
        assertFalse(Credits.isValidCredits("000")); // no leading zeroes (for 1 zero)
        assertFalse(Credits.isValidCredits("01")); // no leading zero (for 1 digit)
        assertFalse(Credits.isValidCredits("001")); // no leading zeroes (for 1 digit)
        assertFalse(Credits.isValidCredits("099")); // no leading zero (for 2 digits)
        assertFalse(Credits.isValidCredits("1MC")); // alphabets within digits
        assertFalse(Credits.isValidCredits("1 6")); // spaces within digits

        // valid credits
        assertTrue(Credits.isValidCredits("0")); // exactly zero
        assertTrue(Credits.isValidCredits("50")); // exactly 2 numbers
        assertTrue(Credits.isValidCredits("999")); // exactly 3 numbers
    }
}
