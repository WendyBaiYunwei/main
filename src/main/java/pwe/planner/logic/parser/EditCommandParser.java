package pwe.planner.logic.parser;

import static java.util.Objects.requireNonNull;
import static pwe.planner.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static pwe.planner.logic.commands.EditCommand.EditModuleDescriptor;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CODE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_COREQUISITE;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_CREDITS;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_NAME;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_SEMESTER;
import static pwe.planner.logic.parser.CliSyntax.PREFIX_TAG;
import static pwe.planner.logic.parser.ParserUtil.parseCode;
import static pwe.planner.logic.parser.ParserUtil.parseCorequisites;
import static pwe.planner.logic.parser.ParserUtil.parseCredits;
import static pwe.planner.logic.parser.ParserUtil.parseIndex;
import static pwe.planner.logic.parser.ParserUtil.parseName;
import static pwe.planner.logic.parser.ParserUtil.parseSemesters;
import static pwe.planner.logic.parser.ParserUtil.parseTags;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import pwe.planner.commons.core.index.Index;
import pwe.planner.logic.commands.EditCommand;
import pwe.planner.logic.parser.exceptions.ParseException;
import pwe.planner.model.module.Code;
import pwe.planner.model.planner.Semester;
import pwe.planner.model.tag.Tag;

/**
 * Parses input arguments and creates a new EditCommand object
 */
public class EditCommandParser implements Parser<EditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);

        if (args.isEmpty()) {
            throw new ParseException(EditCommand.MESSAGE_USAGE);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_CODE, PREFIX_NAME, PREFIX_CREDITS, PREFIX_SEMESTER, PREFIX_COREQUISITE, PREFIX_TAG
        );

        Index index;

        try {
            index = parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE), pe);
        }

        EditModuleDescriptor editModuleDescriptor = new EditCommand.EditModuleDescriptor();
        if (argMultimap.getValue(PREFIX_CODE).isPresent()) {
            editModuleDescriptor.setCode(parseCode(argMultimap.getValue(PREFIX_CODE).get()));
        }
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editModuleDescriptor.setName(parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_CREDITS).isPresent()) {
            editModuleDescriptor.setCredits(parseCredits(argMultimap.getValue(PREFIX_CREDITS).get()));
        }
        parseSemestersForEdit(argMultimap.getAllValues(PREFIX_SEMESTER)).ifPresent(editModuleDescriptor::setSemesters);
        parseCorequisitesForEdit(argMultimap.getAllValues(PREFIX_COREQUISITE))
                .ifPresent(editModuleDescriptor::setCorequisites);
        parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editModuleDescriptor::setTags);

        if (!editModuleDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editModuleDescriptor);
    }

    /**
     * Parses {@code Collection<String> semesters} into a {@code Set<Semester>} if {@code semesters} is non-empty.
     * If {@code semesters} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Semester>} containing zero semesters.
     */
    private Optional<Set<Semester>> parseSemestersForEdit(Collection<String> semesters) throws ParseException {
        assert semesters != null;

        if (semesters.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> corequisitesSet = (semesters.size() == 1 && semesters.contains(""))
                ? Collections.emptySet()
                : semesters;
        return Optional.of(parseSemesters(corequisitesSet));
    }

    /**
     * Parses {@code Collection<String> corequisites} into a {@code Set<Code>} if {@code corequisites} is non-empty.
     * If {@code corequisites} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Code>} containing zero codes.
     */
    private Optional<Set<Code>> parseCorequisitesForEdit(Collection<String> corequisites) throws ParseException {
        assert corequisites != null;

        if (corequisites.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> corequisitesSet = (corequisites.size() == 1 && corequisites.contains(""))
                ? Collections.emptySet()
                : corequisites;
        return Optional.of(parseCorequisites(corequisitesSet));
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * {@code Set<Tag>} containing zero tags.
     */
    private Optional<Set<Tag>> parseTagsForEdit(Collection<String> tags) throws ParseException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        Collection<String> tagSet = tags.size() == 1 && tags.contains("") ? Collections.emptySet() : tags;
        return Optional.of(parseTags(tagSet));
    }
}
