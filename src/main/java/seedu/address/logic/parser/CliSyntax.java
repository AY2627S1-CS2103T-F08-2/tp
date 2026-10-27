package seedu.address.logic.parser;

/**
 * Contains Command Line Interface (CLI) syntax definitions common to multiple commands
 */
public class CliSyntax {

    /* Prefix definitions */
    public static final Prefix PREFIX_ID = new Prefix("--id ");
    public static final Prefix PREFIX_NAME = new Prefix("--name ");
    public static final Prefix PREFIX_PHONE = new Prefix("--phone ");
    public static final Prefix PREFIX_ADDRESS = new Prefix("--address ");
    public static final Prefix PREFIX_MEDICAL_HISTORY = new Prefix("--medical-history ");
    public static final Prefix PREFIX_ADD_MEDICAL_HISTORY = new Prefix("--add-medical-history ");
    public static final Prefix PREFIX_NOTES = new Prefix("--notes ");
    public static final Prefix PREFIX_ADD_NOTES = new Prefix("--add-notes ");
    public static final Prefix PREFIX_FROM = new Prefix("--from ");
    public static final Prefix PREFIX_TO = new Prefix("--to ");
    public static final Prefix PREFIX_DETAILS = new Prefix("--details ");
    public static final Prefix PREFIX_ADD_DETAILS = new Prefix("--add-details ");

}
