package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.logic.parser.CliSyntax.OPERATORS;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Tokenizes command in the format of a boolean expression by extracting all arguments with {@link Prefix prefix} and
 * {@link CliSyntax#OPERATORS boolean operators} as tokens.
 * <br><br>
 * This class functions differently from {@link ArgumentTokenizer} (does not use whitespace as delimiter) and
 * {@link java.util.StringTokenizer StringTokenizer} (insufficient in extracting prefixed arguments and boolean
 * operators), and is intended to be used by {@link BooleanExpressionParser}.
 * <br><br>
 * e.g. Tokenizing {@code a/A || (b/B && c/C)} where prefixes are {@code a/ b/ c/} will yield the following tokens: <br>
 * 1. a/A <br>
 * 2. ||  <br>
 * 3. (   <br>
 * 4. b/B <br>
 * 5. &&  <br>
 * 6. c/C <br>
 * 7. )   <br>
 */
public class BooleanExpressionTokenizer {
    private String stringToTokenize;
    private final List<String> prefixes;
    private final List<String> delimiters;

    private Deque<String> tokens = new ArrayDeque<>();
    private int nextTokenStartIndex = 0;
    private int nextTokenEndIndex = 0;

    /**
     * Initializes a {@link BooleanExpressionTokenizer} that tokenizes {@code stringToTokenize} by extracting all
     * arguments with {@link Prefix prefix} and {@link CliSyntax#OPERATORS boolean operators} as tokens.
     * @param stringToTokenize a string to be tokenized
     * @param prefixes         a list of prefixes to be included as delimiters
     */
    public BooleanExpressionTokenizer(String stringToTokenize, List<Prefix> prefixes) {
        requireNonNull(stringToTokenize);
        requireNonNull(prefixes);

        this.stringToTokenize = stringToTokenize;
        this.prefixes = prefixes.stream().map(Prefix::getPrefix).collect(Collectors.toList());
        this.delimiters = Stream.concat(this.prefixes.stream(), OPERATORS.stream()).collect(Collectors.toList());

        tokenizeString();
    }

    /**
     * Tokenizes {@link #stringToTokenize} based on the {@link CliSyntax#OPERATORS boolean operators} and the specified
     * {@link #prefixes} until there are no more tokens to be extracted.
     * <br><br>
     * Note: This method does not parse or validate the contents of the tokens.
     */
    private void tokenizeString() {
        while (hasNextToken()) {
            extractNextToken();
        }
    }

    /**
     * Tests if there are still more tokens available for extraction.<br>
     * If this method returns {@code true}, then a subsequent call to {@link #extractNextToken()} will successfully
     * extract the next token available.
     * @return {@code true} if and only if there is at least one token remaining; {@code false} otherwise.
     */
    private boolean hasNextToken() {
        // ensure token start/end indexes are within valid range
        if (nextTokenStartIndex >= stringToTokenize.length() || nextTokenEndIndex >= stringToTokenize.length()) {
            return false;
        }

        Optional<Integer> optionalNextTokenEndIndex = delimiters.stream()
                .map(delimiter -> {
                    int index = stringToTokenize.indexOf(delimiter, nextTokenStartIndex);
                    if (index == nextTokenStartIndex) {
                        index += delimiter.length();
                    }
                    return index;
                })
                .filter(index -> index != -1) // exclude -1 (indicates delimiter does not exists)
                .min(Integer::compare); // get the smallest end index for next token

        if (!optionalNextTokenEndIndex.isPresent()) {
            nextTokenEndIndex = stringToTokenize.length();
            return nextTokenStartIndex != nextTokenEndIndex;
        } else {
            nextTokenEndIndex = optionalNextTokenEndIndex.get();
            return true;
        }
    }

    /**
     * Extracts the next available trimmed token, and adds it to the {@link #tokens} if it is not empty.
     * <br><br>
     * However, if the last token extracted is prefixed but the next token extracted is not a delimiter, the next token
     * extracted will be concatenated to the last token extracted.
     */
    private void extractNextToken() {
        if (nextTokenStartIndex >= stringToTokenize.length() || nextTokenEndIndex > stringToTokenize.length()) {
            return;
        }

        String nextToken = stringToTokenize.substring(nextTokenStartIndex, nextTokenEndIndex).trim();
        if (!nextToken.isEmpty()) {
            String lastToken = tokens.peekLast();

            // Checks if last token extracted is prefixed, but nextToken is not delimiter
            if (!delimiters.contains(nextToken) && lastToken != null && prefixes.contains(lastToken)) {
                // concatenate both tokens together if so, and update tokens accordingly
                String joinnedToken = lastToken + nextToken;
                tokens.removeLast();
                tokens.addLast(joinnedToken);
            } else {
                tokens.addLast(nextToken);
            }
        }

        nextTokenStartIndex = nextTokenEndIndex;
    }

    /**
     * Returns the next token from this boolean expression tokenizer.
     * @return the next token extracted
     * @throws NoSuchElementException if there is no more tokens remaining
     */
    public String nextToken() throws NoSuchElementException {
        return tokens.removeFirst();
    }

    /**
     * Returns a boolean that indicates whether there are still tokens remaining.<br>
     * If this method returns {@code true}, then a subsequent call to {@link #nextToken()} will successfully return a
     * token.
     * @return {@code true} if and only if there is at least one token remaining; {@code false} otherwise.
     */
    public boolean hasMoreTokens() {
        return tokens.size() > 0;
    }
}
