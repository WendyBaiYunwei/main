package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.util.CollectionUtil.requireAllNonNull;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_AND;
import static pwe.planner.logic.parser.CliSyntax.OPERATOR_OR;

import java.util.Map;
import java.util.function.Predicate;

import pwe.planner.logic.commands.FindCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Module;

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

    /**
     * Apply the operator on the 2 predicate
     *
     * @param operator
     * @param predicate1
     * @param predicate2
     * @return a composite predicate
     */
    public static Predicate<Module> applyOperator(Operator operator, Predicate<Module> predicate1,
            Predicate<Module> predicate2) throws ParseException {
        requireAllNonNull(operator, predicate1, predicate2);

        switch (operator) {
        case OR:
            return predicate1.or(predicate2);
        case AND:
            return predicate1.and(predicate2);
        default:
            throw new ParseException(String.format(FindCommand.MESSAGE_INVALID_EXPRESSION, FindCommand.MESSAGE_USAGE));
        }
    }
}
