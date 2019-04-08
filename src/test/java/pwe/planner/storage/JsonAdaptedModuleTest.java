package pwe.planner.storage;

import static org.junit.Assert.assertEquals;
import static pwe.planner.storage.JsonAdaptedModule.MISSING_FIELD_MESSAGE_FORMAT;
import static pwe.planner.testutil.TypicalModules.BENSON;

import java.util.ArrayList;
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
    private static final String VALID_CODE = BENSON.getCode().toString();
    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_CREDITS = BENSON.getCredits().toString();
    private static final List<JsonAdaptedSemester> VALID_SEMESTERS = BENSON.getSemesters().stream()
            .map(JsonAdaptedSemester::new).collect(Collectors.toList());
    private static final List<JsonAdaptedCode> VALID_COREQUISITES = BENSON.getCorequisites().stream()
            .map(JsonAdaptedCode::new).collect(Collectors.toList());
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    private static final String INVALID_CODE = "A1234B";
    private static final String INVALID_NAME = "Näme containing invalid characters";
    private static final String INVALID_CREDITS = "1000";
    private static final JsonAdaptedSemester INVALID_SEMESTER = new JsonAdaptedSemester("5");
    private static final List<JsonAdaptedSemester> INVALID_SEMESTERS = Stream
            .concat(VALID_SEMESTERS.stream(), List.of(INVALID_SEMESTER).stream())
            .collect(Collectors.toList());
    private static final JsonAdaptedCode INVALID_COREQUISITE = new JsonAdaptedCode("Z9876Y");
    private static final List<JsonAdaptedCode> INVALID_COREQUISITES = Stream
            .concat(VALID_COREQUISITES.stream(), List.of(INVALID_COREQUISITE).stream())
            .collect(Collectors.toList());
    private static final String INVALID_TAG = "#hashIsInvalid";

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

    @Test
    public void toModelType_invalidSemesters_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, INVALID_SEMESTERS, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = Semester.MESSAGE_SEMESTER_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullSemesters_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, null, VALID_COREQUISITES, VALID_TAGS
        );
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Semester.class.getSimpleName());
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
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedModule module = new JsonAdaptedModule(
                VALID_CODE, VALID_NAME, VALID_CREDITS, VALID_SEMESTERS, VALID_COREQUISITES, invalidTags
        );
        Assert.assertThrows(IllegalValueException.class, module::toModelType);
    }
}
