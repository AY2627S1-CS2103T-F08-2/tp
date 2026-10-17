package seedu.address.storage;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.patient.Appointment;
import seedu.address.model.patient.AppointmentDetails;

/**
 * Jackson-friendly version of {@link Appointment}, saved as {@code {"dateTime": "2026-10-05 09:30", "details":
 * "INSULIN REVIEW"}}, with no {@code details} if the appointment has none. It also reads an appointment saved as
 * a plain string, such as {@code "2026-10-05 09:30"}, as earlier versions of LaiNurse save appointments.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class JsonAdaptedAppointment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Appointment's %s field is missing!";

    private final String dateTime;
    private final String details; // null if none

    /**
     * Constructs a {@code JsonAdaptedAppointment} with the given date and time, and details if not null.
     */
    @JsonCreator
    public JsonAdaptedAppointment(@JsonProperty("dateTime") String dateTime,
            @JsonProperty("details") String details) {
        this.dateTime = dateTime;
        this.details = details;
    }

    /**
     * Converts a given {@code Appointment} into this class for Jackson use.
     */
    public JsonAdaptedAppointment(Appointment source) {
        dateTime = source.value;
        details = source.getDetails().map(appointmentDetails -> appointmentDetails.value).orElse(null);
    }

    /**
     * Returns the {@code JsonAdaptedAppointment} of an appointment saved as a plain string, which has no details.
     */
    @JsonCreator
    public static JsonAdaptedAppointment fromString(String dateTime) {
        return new JsonAdaptedAppointment(dateTime, null);
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
        return new Appointment(dateTime, Optional.ofNullable(details).map(AppointmentDetails::new));
    }

}
