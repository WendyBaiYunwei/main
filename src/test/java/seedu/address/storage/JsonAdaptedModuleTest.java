package seedu.address.storage;

import static org.junit.Assert.assertEquals;
import static seedu.address.storage.JsonAdaptedModule.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.address.testutil.TypicalModules.BENSON;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.module.Code;
import seedu.address.model.module.Credits;
import seedu.address.model.module.Name;
import seedu.address.testutil.Assert;

public class JsonAdaptedModuleTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_CREDITS = "+651234";
    private static final String INVALID_CODE = " ";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_CREDITS = BENSON.getCredits().toString();
    private static final String VALID_CODE = BENSON.getCode().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());

    @Test
    public void toModelType_validModuleDetails_returnsModule() throws Exception {
        JsonAdaptedModule module = new JsonAdaptedModule(BENSON);
        assertEquals(BENSON, module.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedModule module =
                new JsonAdaptedModule(INVALID_NAME, VALID_CREDITS, VALID_CODE, VALID_TAGS);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(null, VALID_CREDITS, VALID_CODE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidCredits_throwsIllegalValueException() {
        JsonAdaptedModule module =
                new JsonAdaptedModule(VALID_NAME, INVALID_CREDITS, VALID_CODE, VALID_TAGS);
        String expectedMessage = Credits.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullCredits_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(VALID_NAME, null, VALID_CODE, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Credits.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidCode_throwsIllegalValueException() {
        JsonAdaptedModule module =
                new JsonAdaptedModule(VALID_NAME, VALID_CREDITS, INVALID_CODE, VALID_TAGS);
        String expectedMessage = Code.MESSAGE_CONSTRAINTS;
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_nullCode_throwsIllegalValueException() {
        JsonAdaptedModule module = new JsonAdaptedModule(VALID_NAME, VALID_CREDITS, null, VALID_TAGS);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Code.class.getSimpleName());
        Assert.assertThrows(IllegalValueException.class, expectedMessage, module::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedModule module =
                new JsonAdaptedModule(VALID_NAME, VALID_CREDITS, VALID_CODE, invalidTags);
        Assert.assertThrows(IllegalValueException.class, module::toModelType);
    }

}
