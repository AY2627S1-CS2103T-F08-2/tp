package seedu.address.storage;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;

/**
 * Jackson-friendly version of {@link Appointment}, saved with {@code number}, {@code dateTime}, {@code done}
 * and optional {@code details}, such as "INSULIN REVIEW". It also reads an appointment saved as
 * a plain string, such as {@code "2026-10-05 09:30"}, as earlier versions of LaiNurse save appointments.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedAppointment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Appointment's %s field is missing!";
    public static final String MESSAGE_DONE_CONSTRAINTS = "Appointment's done field must be a boolean.";

    private final String dateTime;
    private final String details; // null if none
    private final JsonNode number; // null for a legacy appointment
    private final JsonNode done; // null for a legacy appointment, which is undone

    /**
     * Constructs a {@code JsonAdaptedAppointment} with the given date and time, and details if not null.
     */
    public JsonAdaptedAppointment(String dateTime, String details) {
        this(dateTime, details, null);
    }

    /**
     * Reads the number as a JSON value to reject coercions such as 1.5 or "2" locally.
     */
    public JsonAdaptedAppointment(String dateTime, String details, JsonNode number) {
        this(dateTime, details, number, null);
    }

    private JsonAdaptedAppointment(String dateTime, String details, JsonNode number, JsonNode done) {
        this.dateTime = dateTime;
        this.details = details;
        this.number = number;
        this.done = done;
    }

    /**
     * Converts a given {@code Appointment} into this class for Jackson use.
     */
    public JsonAdaptedAppointment(Appointment source) {
        dateTime = source.value;
        details = source.getDetails().map(appointmentDetails -> appointmentDetails.value).orElse(null);
        number = source.isNumbered() ? IntNode.valueOf(source.getNumber()) : null;
        done = BooleanNode.valueOf(source.isDone());
    }

    /**
     * Returns the {@code JsonAdaptedAppointment} of an appointment saved as a plain string, which has no details.
     */
    public static JsonAdaptedAppointment fromString(String dateTime) {
        return new JsonAdaptedAppointment(dateTime, null);
    }

    /**
     * Reads legacy strings and appointment objects, distinguishing absent fields from explicit null values.
     */
    @JsonCreator
    public static JsonAdaptedAppointment fromJson(JsonNode json) {
        if (json.isTextual()) {
            return fromString(json.textValue());
        }
        if (!json.isObject()) {
            throw new IllegalArgumentException(Appointment.MESSAGE_CONSTRAINTS);
        }
        JsonNode dateTime = json.get("dateTime");
        JsonNode details = json.get("details");
        if (dateTime != null && !dateTime.isTextual()
                || details != null && !details.isNull() && !details.isTextual()) {
            throw new IllegalArgumentException(Appointment.MESSAGE_CONSTRAINTS);
        }
        return new JsonAdaptedAppointment(dateTime == null ? null : dateTime.textValue(),
                details == null ? null : details.textValue(), json.get("number"), json.get("done"));
    }

    /**
     * Converts this Jackson-friendly adapted appointment into the model's {@code Appointment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted appointment.
     */
    public Appointment toModelType() throws IllegalValueException {
        if (dateTime == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "dateTime"));
        }
        if (!Appointment.isValidAppointment(dateTime)) {
            throw new IllegalValueException(Appointment.MESSAGE_CONSTRAINTS);
        }
        if (details != null && !AppointmentDetails.isValidAppointmentDetails(details)) {
            throw new IllegalValueException(AppointmentDetails.MESSAGE_CONSTRAINTS);
        }
        if (done != null && !done.isBoolean()) {
            throw new IllegalValueException(MESSAGE_DONE_CONSTRAINTS);
        }
        Appointment appointment = new Appointment(dateTime, Optional.ofNullable(details).map(AppointmentDetails::new))
                .withDone(done != null && done.booleanValue());
        if (number == null || number.isMissingNode()) {
            return appointment;
        }
        if (!number.isIntegralNumber() || !number.canConvertToInt() || number.intValue() <= 0) {
            throw new IllegalValueException(Appointment.MESSAGE_NUMBER_CONSTRAINTS);
        }
        return appointment.withNumber(number.intValue());
    }

}
