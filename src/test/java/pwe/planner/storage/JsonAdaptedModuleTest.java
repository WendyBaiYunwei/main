package pwe.planner.storage;

import static org.junit.Assert.assertEquals;
import static pwe.planner.storage.JsonAdaptedModule.MISSING_FIELD_MESSAGE_FORMAT;
import static pwe.planner.testutil.TypicalModules.BENSON;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import pwe.planner.commons.exceptions.IllegalValueException;
import pwe.planner.model.module.Code;
import pwe.planner.model.module.Credits;
import pwe.planner.model.module.Name;
import pwe.planner.model.planner.Semester;
import pwe.planner.testutil.Assert;

public class JsonAdaptedModuleTest {
    private static final JsonAdaptedCode VALID_CODE = new JsonAdaptedCode(BENSON.getCode());
    private static final JsonAdaptedName VALID_NAME = new JsonAdaptedName(BENSON.getName());
    private static final JsonAdaptedCredits VALID_CREDITS = new JsonAdaptedCredits(BENSON.getCredits());
    private static final List<JsonAdaptedSemester> VALID_SEMESTERS = BENSON.getSemesters().stream()
            .map(JsonAdaptedSemester::new).collect(Collectors.toList());
    private static final List<JsonAdaptedCode> VALID_COREQUISITES = BENSON.getCorequisites().stream()
            .map(JsonAdaptedCode::new).collect(Collectors.toList());
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new).collect(Collectors.toList());

    private static final JsonAdaptedCode INVALID_CODE = new JsonAdaptedCode("A1234B");
    private static final JsonAdaptedName INVALID_NAME = new JsonAdaptedName("Näme containing invalid characters");
    private static final JsonAdaptedCredits INVALID_CREDITS = new JsonAdaptedCredits("1000");
    private static final JsonAdaptedSemester INVALID_SEMESTER = new JsonAdaptedSemester("5");
    private static final List<JsonAdaptedSemester> INVALID_SEMESTERS = Stream
            .concat(VALID_SEMESTERS.stream(), List.of(INVALID_SEMESTER).stream())
            .collect(Collectors.toList());
    private static final JsonAdaptedCode INVALID_COREQUISITE = new JsonAdaptedCode("Z9876Y");
    private static final List<JsonAdaptedCode> INVALID_COREQUISITES = Stream
            .concat(VALID_COREQUISITES.stream(), List.of(INVALID_COREQUISITE).stream())
            .collect(Collectors.toList());
    private static final JsonAdaptedTag INVALID_TAG = new JsonAdaptedTag("#hashIsInvalid");
    private static final List<JsonAdaptedTag> INVALID_TAGS = Stream
            .concat(VALID_TAGS.stream(), List.of(INVALID_TAG).stream())
            .collect(Collectors.toList());

    @Test
    public void toModelType_validModuleDetails_returnsModule() throws Exception {
        JsonAdaptedModule module = new JsonAdaptedModule(BENSON);
        assertEquals(BENSON, module.toModelType());
    }

    @Test
    public void toModelType_invalidCode_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                INVALID_CODE, VALID_NAME, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Code.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullCode_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                null, VALID_NAME, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Code.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, INVALID_NAME, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, null, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidCredits_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, INVALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Credits.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullCredits_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, null, VALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    // We don't test for null semesters, corequisites, tags because all of them will result in a successful operation.
    // (They can be empty sets).

    @Test
    public void toModelType_invalidSemesters_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, INVALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Semester.MESSAGE_SEMESTER_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidCorequisites_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, VALID_SEMESTERS, INVALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Code.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, INVALID_TAGS
        );
        Assert.assertThrows(IllegalValueException.class, module::toModelType);
    }
}
