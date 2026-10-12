---
layout: page
title: User Guide
---

LaiNurse is a **desktop application that gives home nurses fast access to patient and appointment details for home visits, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, LaiNurse can help you look up and update patient details faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-F08-2/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for LaiNurse.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   LaiNurse keeps its data in the folder you launch it from, so always launch it from this folder.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all patients.

   * `add --id T0123456B --name John Doe --number 98765432 --address John street, block 123, #01-01` : Adds a patient named `John Doe` with the ID `T0123456B`.

   * `delete S1234567A` : Deletes the patient with the ID `S1234567A` (Alex Yeoh, in the sample data).

   * `clear` : Deletes all patients.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add --name NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `--name NAME [--medical-history MEDICAL_HISTORY]` can be used as `--name John Doe --medical-history Asthma` or as `--name John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[KEYWORD]…​` may be omitted, or written as `John` or `John Jane`.

* Options (words starting with `--`) can be in any order. A parameter that must come first, such as `ID` in `edit`, must stay first.<br>
  For example, if the command specifies `--name NAME --number NUMBER`, `--number NUMBER --name NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Opens a window with a link to this user guide.

![help message](images/helpMessage.png)

Format: `help`


### Adding a patient: `add`

Adds a patient to LaiNurse.

Format: `add --id ID --name NAME --number NUMBER --address ADDRESS [--medical-history MEDICAL_HISTORY] [--next-appointment DATE_OR_DATETIME]…​`

* `--id`, `--name`, `--number`, and `--address` are required. Each must have a non-blank value.
* Medical history and appointments are independently optional. If an option is supplied, its value must not be blank.
* Parameters may appear in any order. Each option may appear only once, except `--next-appointment`, which can be repeated to add several appointments. Separate options and values using spaces or tabs; values may contain spaces.
* IDs contain 1 to 10 letters or digits. IDs are case-insensitive, and a patient with an existing ID cannot be added again.
* Names, addresses, and medical history accept any non-blank text. IDs and these text fields are stored in uppercase, with repeated whitespace collapsed.
* Phone numbers may contain digits, spaces, `+`, and `-`, and must contain at least three digits.
* Each appointment must be a real date in `yyyy-MM-dd` format, optionally followed by a 24-hour time in `HH:mm` format, such as `2026-11-30` or `2026-11-30 09:00`. Past dates are accepted. If the same appointment is given twice, it is kept once. A date-only appointment and a timed one on the same day, including `00:00`, are kept as separate appointments.
* Whitespace-delimited words beginning with `--` are reserved for options, including within text fields. Unknown options, misspellings, and `--name=value` syntax are rejected with the command usage instructions.
* After a patient is added, the list shows all patients again, even if it was showing `find` or `viewappt` results. The list numbers of other patients may change.

Examples:

* `add --id T0123456B --name John Doe --number 98765432 --address John street, block 123, #01-01`
* `add --medical-history Asthma --address Block 312, Clementi Ave 2 --number 91234567 --name Betsy Crowe --id S2222222B --next-appointment 2026-11-30`
* `add --next-appointment 2026-11-30 09:00 --name James Ho --id S3333333C --address 123, Clementi Rd, 1234665 --number 22224444 --next-appointment 2026-12-14 09:00`

### Listing all patients: `list`

Shows a list of all patients, with each patient's ID, name, phone number and address.
Medical history and appointments are shown only if the patient has them. Appointments are listed from earliest to latest. On the same day, an appointment with only a date comes first, like an all-day event in a calendar.

Format: `list`

### Editing a patient: `edit`

Edits the details of an existing patient, and adds or removes their appointments.

Format: `edit ID [--name NAME] [--number NUMBER] [--address ADDRESS] [--medical-history MEDICAL_HISTORY] [--add-appt DATE_OR_DATETIME]…​ [--remove-appt DATE_OR_DATETIME]…​`

* Edits the patient with the given `ID`, which is the patient ID shown on their card, not their position in the list. The patient does not have to be in the list currently shown.
* `ID` must come first; the other parameters may appear in any order. Separate options and values using spaces or tabs.
* As in `add`, whitespace-delimited words beginning with `--` are reserved for options. Unknown options, misspellings, and `--id` are rejected with the command usage instructions.
* The ID itself cannot be edited. To correct a wrong ID, delete the patient and add them again.
* At least one of the optional parameters must be provided.
* `--name`, `--number`, `--address`, and `--medical-history` replace the existing value. Each may appear only once, and follows the same rules as in [`add`](#adding-a-patient-add).
* `--add-appt` adds an appointment, and `--remove-appt` removes one. Both can be repeated. Appointments use the same format as in `add`.
* `--remove-appt` must match an existing appointment exactly. For example, `--remove-appt 2026-10-05` does not remove `2026-10-05 09:30`. If any appointment to remove is not found, nothing is changed.
* Appointments are removed before they are added, so one command can move an appointment to a new date. Adding an appointment the patient already has makes no change. As with `add`, a date-only appointment and a timed one on the same day are separate appointments.
* After a patient is edited, the list shows all patients again.

Examples:
*  `edit S1234567A --number 91234567 --address Blk 30 Geylang Street 29, #06-41` changes the phone number and address of patient `S1234567A`.
*  `edit S1234567A --medical-history Type 2 diabetes, hypertension` replaces the medical history of patient `S1234567A`.
*  `edit S1234567A --add-appt 2026-12-01 10:00 --add-appt 2026-12-15` adds two appointments.
*  `edit S1234567A --remove-appt 2026-10-05 09:30 --add-appt 2026-10-07 14:00` moves an appointment from 5 October to 7 October.

### Locating patients by substrings: `find`

Finds patients whose specified fields contain any of the supplied substrings.

Format: `find [--id ID] [--name NAME] [--address ADDRESS] [--number NUMBER] [--medical-history HISTORY] [--next-appointment DATE_OR_TIME]`

* Supply at least one option. Each supplied option must have a non-blank value and may appear only once.
* Parameters may appear in any order. Separate options and values using spaces or tabs; values may contain spaces.
* Values may be unquoted or enclosed in matching straight double quotes (`"Mary"`) or curly double quotes (`“Mary”`). Quotes must surround the complete value; missing or mismatched closing quotes and text after a closing quote are rejected.
* Matching ignores case and repeated whitespace. For example, `--name han` matches `Hans`, and `--address "marina   bay"` matches `Marina Bay`.
* Each value is one contiguous substring of its corresponding field. For example, `--address "Marina Bay"` matches that phrase, while `--address "Bay Marina"` does not match `Marina Bay`.
* Patients matching **any** supplied option are returned (an `OR` search). For example, `--name John --address "Marina Bay"` finds patients whose name contains `John` **or** whose address contains `Marina Bay`. Each patient appears once, in the original list order.
* Phone and appointment searches match their stored text. Partial values such as `--number 123`, `--next-appointment 2026-10`, and `--next-appointment 09:30` are accepted. Phone punctuation remains significant: `9876-54` does not contain `987654`.
* Patients without medical history or a next appointment do not match searches on that missing field, but may match another supplied option.
* Whitespace-delimited words beginning with `--` are reserved for options, including within quoted values. Unknown options, misspellings, and `--name=value` syntax are rejected. Text such as `Anne--Marie` is accepted.
* Each search checks all patients, even when the list is already filtered. No matches produces an empty list and a count of zero. Invalid commands leave the displayed list unchanged. Enter `list` to show all patients again.

Examples:

* `find --name "John" --address "Marina Bay"` finds patients whose name contains `John` or whose address contains `Marina Bay`.
* `find --id “T04”` finds patients whose ID contains `T04`.
* `find --id "123"` finds patients whose ID contains `123`.
* `find --medical-history penicillin --next-appointment 2026-10` finds patients whose medical history contains `penicillin` or whose next appointment contains `2026-10`.

### Viewing appointments: `viewappt`

Views patients who have an appointment on the specified date.

Format: `viewappt DATE`

* `DATE` must be a real date in `yyyy-MM-dd` format.
* All of each patient's appointments are checked. A patient is shown if any of their appointments is on `DATE`, with or without a time.
* If no patients have appointments on that date, an empty list is shown.

Examples:
* `viewappt 2026-10-15` views all patients with appointments on October 15, 2026.
* `viewappt 2026-12-25` views all patients with appointments on December 25, 2026.

### Deleting a patient: `delete`

Deletes the specified patient from LaiNurse.

Format: `delete ID`

* Deletes the patient with the specified `ID`. IDs are case-insensitive, so `s1234567a` and `S1234567A` refer to the same patient.
* The patient does not need to be in the displayed list, because `delete` searches all patients.
* If no patient has that ID, LaiNurse shows an error and deletes nothing.
* The displayed list keeps its current filter, so the list numbers of the patients below the deleted one go down by one.

Examples:
* `delete S1234567A` deletes the patient with the ID `S1234567A`.

### Deleting all patients: `clear`

Deletes all patients from LaiNurse. This cannot be undone.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

LaiNurse saves your data automatically after every command that succeeds. You do not need to save manually.

### Editing the data file

LaiNurse saves its data as a JSON file, `data/addressbook.json`, inside the folder you launched it from. If you followed the [Quick start](#quick-start), that is the folder containing the JAR file. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, LaiNurse starts with an empty patient list the next time it launches. The next command that succeeds, even `list` or `help`, then overwrites the invalid file with the empty list, and the data in it is lost. Back up the file before you edit it. If LaiNurse starts empty after an edit, close the window without running any command (the `exit` command also saves), and restore your backup.<br>
Furthermore, certain edits can cause LaiNurse to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install LaiNurse on the other computer. Then copy `data/addressbook.json` from your old LaiNurse home folder into a `data` folder next to the JAR file on the new computer, replacing the file there if there is one.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add --id ID --name NAME --number NUMBER --address ADDRESS [--medical-history MEDICAL_HISTORY] [--next-appointment DATE_OR_DATETIME]…​` <br> e.g., `add --id S3333333C --name James Ho --number 22224444 --address 123, Clementi Rd, 1234665 --next-appointment 2026-11-30 09:00`
**Clear** | `clear`
**Delete** | `delete ID`<br> e.g., `delete T0123456B`
**Edit** | `edit ID [--name NAME] [--number NUMBER] [--address ADDRESS] [--medical-history MEDICAL_HISTORY] [--add-appt DATE_OR_DATETIME]…​ [--remove-appt DATE_OR_DATETIME]…​`<br> e.g., `edit S1234567A --number 91234567 --add-appt 2026-12-01 10:00`
**Find** | `find [--id ID] [--name NAME] [--address ADDRESS] [--number NUMBER] [--medical-history HISTORY] [--next-appointment DATE_OR_TIME]`<br> e.g., `find --name "John" --address "Marina Bay"` (matches either field; at least one option required)
**List** | `list`
**ViewAppt** | `viewappt DATE`<br> e.g., `viewappt 2026-10-15`
**Help** | `help`
**Exit** | `exit`
