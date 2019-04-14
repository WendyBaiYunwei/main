package pwe.planner.model.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.Assert;

public class YearTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Year(null));
    }

    @Test
    public void constructor_invalidYear_throwsIllegalArgumentException() {
        String invalidYear = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Year(invalidYear));
    }

    @Test
    public void isValidYear() {
        // null year
        Assert.assertThrows(NullPointerException.class, () -> Year.isValidYear(null));

        // invalid year
        assertFalse(Year.isValidYear("")); // empty string
        assertFalse(Year.isValidYear(" ")); // spaces only
        assertFalse(Year.isValidYear("+1")); // no plus sign
        assertFalse(Year.isValidYear("-1")); // no negative sign
        assertFalse(Year.isValidYear("5")); // no integer outside range of 1 to 4
        assertFalse(Year.isValidYear("01")); // no leading zero
        assertFalse(Year.isValidYear("one")); // alphabets
        assertFalse(Year.isValidYear("1 6")); // spaces within digits

        // valid year
        assertTrue(Year.isValidYear("1")); // exactly zero
    }
}
