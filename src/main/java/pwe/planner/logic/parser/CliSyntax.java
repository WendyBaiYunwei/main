package pwe.planner.logic.parser;

import java.util.List;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_NAME = new Prefix("name/");
    public static final Prefix PREFIX_CODE = new Prefix("code/");
    public static final Prefix PREFIX_CREDITS = new Prefix("credits/");
    public static final Prefix PREFIX_TAG = new Prefix("tag/");
    public static final Prefix PREFIX_COREQUISITE = new Prefix("coreq/");
    public static final Prefix PREFIX_YEAR = new Prefix("year/");
    public static final Prefix PREFIX_SEMESTER = new Prefix("sem/");

    public static final String OPERATOR_OR = "||";
    public static final String OPERATOR_AND = "&&";
    public static final String OPERATOR_LEFT_BRACKET = "(";
    public static final String OPERATOR_RIGHT_BRACKET = ")";

    public static final List<String> OPERATORS = List.of(
            OPERATOR_AND,
            OPERATOR_OR,
            OPERATOR_LEFT_BRACKET,
            OPERATOR_RIGHT_BRACKET
    );
}
