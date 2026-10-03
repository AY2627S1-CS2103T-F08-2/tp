package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Appointment;
import seedu.address.model.person.Email;
import seedu.address.model.person.Id;
import seedu.address.model.person.MedicalHistory;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String processedIndex = oneBasedIndex.trim().toUpperCase();
        if (!StringUtil.isNonZeroUnsignedInteger(processedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(processedIndex));
    }

    /**
     * Parses a {@code String id} into a {@code id}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code id} is invalid.
     */
    public static Id parseId(String id) throws ParseException {
        requireNonNull(id);
        String processedId = id.trim().toUpperCase();
        if (!Id.isValidId(processedId)) {
            throw new ParseException(Id.MESSAGE_CONSTRAINTS);
        }
        return new Id(processedId);
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String processedName = name.trim().toUpperCase();
        if (!Name.isValidName(processedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(processedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String processedPhone = phone.trim().toUpperCase();
        if (!Phone.isValidPhone(processedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(processedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String processedAddress = address.trim().toUpperCase();
        if (!Address.isValidAddress(processedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(processedAddress);
    }

    /**
     * Parses {@code String medicalHistory} into a {@code MedicalHistory}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code medicalHistory} is invalid.
     */
    public static MedicalHistory parseMedicalHistory(String medicalHistory) throws ParseException {
        requireNonNull(medicalHistory);
        String processedMedicalHistory = medicalHistory.trim().toUpperCase();
        if (!MedicalHistory.isValidMedicalHistory(processedMedicalHistory)) {
            throw new ParseException(MedicalHistory.MESSAGE_CONSTRAINTS);
        }
        return new MedicalHistory(processedMedicalHistory);
    }

    /**
     * Parses {@code String appointment} into a {@code Appointment}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code Appointment} is invalid.
     */
    public static Appointment parseAppointment(String appointment) throws ParseException {
        requireNonNull(appointment);
        String processedAppointment = appointment.trim().toUpperCase();
        if (!Appointment.isValidAppointment(processedAppointment)) {
            throw new ParseException(Appointment.MESSAGE_CONSTRAINTS);
        }
        return new Appointment(processedAppointment);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String processedEmail = email.trim().toUpperCase();
        if (!Email.isValidEmail(processedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(processedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Characters will be converted to uppercase.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String processedTag = tag.trim().toUpperCase();
        if (!Tag.isValidTagName(processedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(processedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
