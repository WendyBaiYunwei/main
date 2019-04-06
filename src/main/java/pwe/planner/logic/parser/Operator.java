package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;

import java.util.Map;

/**
 * Operators that can be used
 */
public enum Operator {
    /*
     We will use 99 for highest precedence, 0 for lowest precedence
     Order of precedence
        && > OR
    */
    // LEFT_BRACKET and RIGHT_BRACKET uses 1 because we handle it in our switch condition.
    LEFT_BRACKET(0),
    RIGHT_BRACKET(0),
    // Boolean OR. We use 11 to indicate lower precedence
    OR(11),
    // Boolean AND. We use 22 to indicate a higher precedence vs boolean OR
    // We increase more than 1 mainly for future references if there is any operators we need between them.
    AND(22);

    private static Map<String, Operator> ops = Map.of(
            OPERATOR_OR, Operator.OR,
            OPERATOR_AND, Operator.AND
    );
    final int precedence;

    Operator(int precedence) {
        this.precedence = precedence;
    }

    public static Operator getOperatorFromString(String input) {
        requireNonNull(input);

        return ops.get(input);
    }

    /**
     * Returns {@code true} if {@code op1} has higher precedence than {@code op2}.<br>
     * Note: If {@code op1} has equal precedence as {@code op2}, it is considered to have higher precedence than
     * {@code op2}.
     * @param op1 first operator to compare
     * @param op2 second operator to compare
     * @return {@code true} if {@code op1} has higher precedence than {@code op2}; {@code false} otherwise
     */
    public static boolean hasHigherPrecedence(Operator op1, Operator op2) {
        requireAllNonNull(op1, op2);
        return op1.precedence >= op2.precedence;
    }
}
