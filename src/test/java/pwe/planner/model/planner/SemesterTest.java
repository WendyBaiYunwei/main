package pwe.planner.model.planner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pwe.planner.testutil.Assert;

public class SemesterTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        Assert.assertThrows(NullPointerException.class, () -> new Semester(null));
    }

    @Test
    public void constructor_invalidSemester_throwsIllegalArgumentException() {
        String invalidSemester = "";
        Assert.assertThrows(IllegalArgumentException.class, () -> new Semester(invalidSemester));
    }

    @Test
    public void isValidSemester() {
        // null year
        Assert.assertThrows(NullPointerException.class, () -> Semester.isValidSemester(null));

        // invalid year
        assertFalse(Semester.isValidSemester("")); // empty string
        assertFalse(Semester.isValidSemester(" ")); // spaces only
        assertFalse(Semester.isValidSemester("+1")); // no plus sign
        assertFalse(Semester.isValidSemester("-1")); // no negative sign
        assertFalse(Semester.isValidSemester("5")); // no integer outside range of 1 to 4
        assertFalse(Semester.isValidSemester("01")); // no leading zero
        assertFalse(Semester.isValidSemester("one")); // alphabets
        assertFalse(Semester.isValidSemester("1 6")); // spaces within digits

        // valid year
        assertTrue(Semester.isValidSemester("1")); // exactly zero
    }
}
