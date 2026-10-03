package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_ID = new Prefix("--id ");
    public static final Prefix PREFIX_NAME = new Prefix("--name ");
    public static final Prefix PREFIX_PHONE = new Prefix("--number ");
    public static final Prefix PREFIX_ADDRESS = new Prefix("--address ");
    public static final Prefix PREFIX_MEDICAL_HISTORY = new Prefix("--medical-history ");
    public static final Prefix PREFIX_NEXT_APPOINTMENT = new Prefix("--next-appointment ");
    public static final Prefix PREFIX_EMAIL = new Prefix("--email ");
    public static final Prefix PREFIX_TAG = new Prefix("--tag ");

}
