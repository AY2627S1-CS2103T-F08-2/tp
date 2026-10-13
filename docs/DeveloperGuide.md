---
layout: page
title: Developer Guide
---

* Table of Contents {:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the
  originals.}_

* AI Use Declaration from Qin Fangzheng: "As my career path is not related to software engineering, I choose AI-5/AI-6 as my AI use level, where I get Codex to do the tasks, and then I myself review the results, including feature behavior and partial code."

* AI Use Declaration from Jaydon: "I used Codex and OpenCode to review and generate code.
  Based on the given levels of AI use in coding tasks, this ranges between AI-2 and AI-5".

* AI Use Declaration from Yuhao: "I used Claude Code to write code, tests and documentation, and I review and test the results."

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML
Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit
diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [
`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [
`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in
charge of the app launch and shut down.

* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues
the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API
  interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other
components interact with a component through its interface rather than its concrete class, preventing them from coupling
to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [
`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PatientListPanel`,
`PatientDetailPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart`
class, which captures common behavior among classes that represent visible GUI parts.

`PatientListPanel` shows each patient in the filtered list on one row, and `PatientDetailPanel` beside it shows all the
details of one patient. When a `CommandResult` names a patient to view, as the result of `view` does, `MainWindow` tells
`PatientDetailPanel` to show that patient. After every command, `MainWindow` highlights the shown patient's row in
`PatientListPanel`, or no row if that patient is not in the displayed list. `PatientDetailPanel` finds the
patient by ID in the list of all patients from `Logic#getPatientList()`, and listens to that list. So it shows a
patient's new details after an edit, and goes back to its starting message after the patient is deleted, whatever the
filter on the displayed list is.

When the user presses Tab, `CommandBox` asks `Logic#getCompletions(String)` for the ways to finish the patient ID in the
command, and replaces the text with the first one. Each further Tab gives the next one. `IdCompleter` in the `Logic`
component holds the rules, so `CommandBox` needs no knowledge of the command formats. `view` and `delete` take only an
ID, so the ID must end the command. `edit` takes the ID first and options after it, so `IdCompleter` completes the word
after `edit` and keeps the options. When the user presses Esc in an empty command box, `CommandBox` executes the `list`
command through the same `CommandExecutor` as Enter does, so the result is the same as typing `list`. `CommandBox` also
keeps a `CommandHistory` of the commands entered since the app started, which the Up and Down keys go through. The
history is kept only in the UI, because no command needs it.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in
`src/main/resources/view`. For example, [
`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)
specifies the layout of [
`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Patient` objects from the model.

### Logic component

**API** : [
`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API
call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in
   turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which
   is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a patient).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several
   interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:

* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a
  placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to
  parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that
  object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface
  so they can be treated similarly where appropriate, for example during testing.

### Model component

**API** : [
`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Patient` objects (which are contained in a `UniquePatientList` object).
* stores the `Patient` objects selected by the current filter, such as search results, in a separate _filtered_ list. It
  exposes this list as an unmodifiable `ObservableList<Patient>` that the UI can observe and bind to, so the UI updates
  when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed
  to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they
  should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

### Storage component

**API** : [
`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,

* can save both address book data and user preference data in JSON format, and read them back into corresponding
  objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and
  `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects
  that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Finding patients by substrings

`FindCommandParser` recognizes the five patient-detail options defined in `CliSyntax`: ID, name, phone number, address
and medical history. It normalizes option separators
using the same approach as `add`, then reuses `ArgumentTokenizer` and `ArgumentMultimap` to extract values and reject
duplicate options. At least one option is required, and every supplied value must be non-blank. Matching straight or
curly double quotes surrounding a complete value are removed. Unknown options, unexpected preamble text, and malformed
quotes cause a `ParseException` before the displayed list is changed. Words beginning with `--` remain reserved for
options, including inside quoted values.

`PatientContainsSubstringsPredicate` stores five optional string criteria normalized by `TextNormalizer`. Each criterion
is a literal, contiguous substring of its corresponding stored field. The criteria combine using OR, so a patient is
included if any supplied criterion matches. Missing medical history contributes no match for that field. Phone criteria
are substrings rather than complete phone numbers, so the parser does not apply the validation used when adding a
patient. `find` does not search appointments. `viewappt` handles every appointment search, as described in
[Viewing appointments by date](#viewing-appointments-by-date).

`FindCommand` applies the predicate through `Model#updateFilteredPatientList()` and reports the existing patient count
message. This replaces the previous filter and searches the full patient collection while preserving its order.
Filtering does not change patient records. `list` restores the full list.

### Storing multiple appointments

Each patient holds an `AppointmentSchedule`, an immutable list of `Appointment` objects. `add` builds it from every
`--appt` value, and `edit` changes it with `--remove-appt` and `--add-appt`. Removals are applied first, so
one command can move an appointment. Each change returns a new `AppointmentSchedule` instead of modifying the old one,
so a failed `edit` leaves the patient unchanged.

**Equality.** An `Appointment` stores a date and an optional time. Two appointments are equal when their normalized
text is equal. Dates and times are parsed with strict formatters that accept only one spelling of each value, so equal
text means the same date and the same time (or both having no time).

**Order.** Appointments are ordered by date, then by time. On the same day, an appointment with only a date comes before
any appointment with a time. A date-only appointment means "sometime that day", like an all-day event in a calendar, so
it is listed at the top of that day. `compareTo` is consistent with `equals`, so `AppointmentSchedule` can use a
`TreeSet` to sort the appointments and drop duplicates in one step.

**Duplicates.** Only exact duplicates are dropped. `2026-10-05` and `2026-10-05 00:00` are different appointments: the
first has no fixed time, while the second is at midnight. Merging them would lose information that the nurse entered.
Removing an appointment through `edit` also needs an exact match, so `--remove-appt 2026-10-05` does not remove
`2026-10-05 09:30`.

**Storage and compatibility.** `JsonAdaptedPatient` saves the appointments as an `"appointments"` list, and still reads
the single `"nextAppointment"` field from older data files.

**Time zones.** An `Appointment` holds a `LocalDate` and an optional `LocalTime`, with no time zone or offset, and is
saved as text such as `2026-10-05 09:30`. This assumes that the nurse and their patients are in one time zone, which
holds for home visits, so an appointment means the local date and time of the visit. The app never converts appointments
between time zones, so changing the computer's time zone, or a daylight saving change, does not move them.

### Viewing appointments by date

`viewappt` takes either one date, or a range given by `--from`, `--till`, or both. `ViewAppointmentCommandParser`
normalizes option separators in the same way as `add`, so unknown options are rejected. A single date becomes a range
that starts and ends on that date. When both options are given, the parser rejects a `--from` date that is after the
`--till` date.

`ViewAppointmentCommand` stores the start and end dates as `Optional<LocalDate>` values. It works out the range only
when it runs: a missing start date becomes today, and a missing end date becomes `LocalDate.MAX`. Today is
`LocalDate.now(clock)`, where the clock is `Clock.systemDefaultZone()`, so it is the current date in the computer's
default time zone. A package-private constructor takes another `Clock`, which lets tests fix the date. Because the
command reads the date when it runs rather than when it is parsed, it never compares against a stale day. If only
`--till` is given and its date is before today, the range would be empty, so the command shows an error that suggests
adding `--from`. The command then filters the list with `AppointmentBetweenDatesPredicate`, which shows a patient if any
of their appointments falls in the range. Only dates are compared, so a timed appointment earlier today still counts.

**Design considerations: where appointment searches belong**

* **Alternative 1 (current choice):** Only `viewappt` searches appointments, by real dates.
    * Pros: Each command answers one question. `find` answers "who is this patient?", and `viewappt` answers "whom do I
      visit, and when?". Only `AppointmentBetweenDatesPredicate` matches appointments, and it checks every appointment.
    * Cons: A search such as "Alex, or anyone with an appointment in October" needs two commands. There is no search by
      time of day.
* **Alternative 2:** `find` also takes an appointment option and matches it as a substring of each appointment.
    * Pros: Appointment searches can be combined with other `find` options in one command.
    * Cons: Substrings are ambiguous for dates. For example, `10` matches October, the 10th of any month, and 10:00.
      Appointment matching would also live in two places.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo
history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the
following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and
`Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the
initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls
`Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be
saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls
`Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the
`undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once
to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once
to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as
`list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus,
the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not
pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be
purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern
desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
    * Pros: Easy to implement.
    * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by itself.
    * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
    * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* manages a number of patients, schedules and follow-up appointments
* carries a laptop along to work, prefers using it over mobile devices
* prefers doing work while commuting (does not require a mouse)
* is reasonably comfortable with the keyboard and CLI applications

**Value proposition**: Manages patients details and appointments faster than with a typical mouse-driven GUI application. Lightweight, provides value even when used in short timeframes such as commuting in public transport.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​           | I want to …​                                                                                         | So that …​                                                    |
|----------|------------------|-----------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| `* * *`  | home-based nurse | add an appointment for a patient with a date and time                                                | I can plan my home visits in advance                         |
| `* * *`  | home-based nurse | record and view a patient's medical profile (conditions, allergies, medications, next-of-kin)        | I know their health details and whom to call in an emergency |
| `* * *`  | home-based nurse | record personal notes about a patient (routine, hobbies, pet peeves, past conversations)             | I can build rapport and recall what we've talked about       |
| `* * *`  | home-based nurse | reschedule an appointment in one command                                                            | I don't have to delete and re-add it                         |
| `* * *`  | home-based nurse | view patients with appointments on a specific date                                                   | I can plan my visits for that day                            |
| `* `     | home-based nurse | archive a discharged patient instead of deleting them                                                | they're hidden from my list but I keep their records         |
| `* *`    | home-based nurse | see patients with no upcoming appointment                                                            | no one gets missed                                           |

### Use cases

(For all use cases below, the **System** is `LaiNurse` and the **Actor** is the `nurse`, unless specified otherwise)

**Use case: UC01 - Add a patient**

**MSS**

1.  Nurse requests to add a patient with the patient's details
2.  LaiNurse adds the patient
3.  LaiNurse shows the details of the added patient

   Use case ends.

**Extensions**

* 1a. One or more compulsory fields (ID, name, phone number, address) are missing.

    * 1a1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1b. One or more fields are in an invalid format.

    * 1b1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

* 1c. One of the given appointment dates/times is invalid.

    * 1c1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1d. A patient with the given ID already exists.

    * 1d1. LaiNurse shows an error message.

      Use case ends.

**Use case: UC02 - Find a patient**

**MSS**

1.  Nurse requests to find patients using one or more substrings of their ID, name, phone number, address, or medical history
2.  LaiNurse shows a list of matching patients

    Use case ends.

**Extensions**

* 1a. No search option is supplied, or a supplied value is blank.

    * 1a1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1b. An option is unknown or repeated, or the command contains unexpected text or malformed quotes.

    * 1b1. LaiNurse shows an error message and keeps the displayed list unchanged.

      Use case resumes at step 1.

* 2a. No patients match any supplied field substring.

    * 2a1. LaiNurse shows an empty list and a count of zero matching patients.

      Use case ends.

**Use case: UC03 - Delete a patient**

**MSS**

1.  Nurse <u>finds the patient (UC02)</u>
2.  Nurse requests to delete the patient by ID
3.  LaiNurse deletes the patient and all of the patient's details
4.  LaiNurse shows a message confirming the deletion

    Use case ends.

**Extensions**

* 1a. Nurse already knows the patient's ID.

  Use case resumes at step 2.

* 2a. The ID is missing.

    * 2a1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2b. The given ID does not match any patient.

    * 2b1. LaiNurse shows an error message.

      Use case resumes at step 2.

**Use case: UC04 - Edit a patient's details**

**MSS**

1.  Nurse <u>finds the patient (UC02)</u>
2.  Nurse requests to edit the patient by ID with the fields to change, and any appointments to add or remove
3.  LaiNurse updates the patient's details and appointments
4.  LaiNurse shows the updated details of the patient

    Use case ends.

**Extensions**

* 1a. Nurse already knows the patient's ID.

  Use case resumes at step 2.

* 2a. The given ID does not match any patient.

    * 2a1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2b. No fields to edit are provided.

    * 2b1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2c. One or more fields are in an invalid format.

    * 2c1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 2.

* 2d. Nurse tries to change the patient's ID.

    * 2d1. LaiNurse does not accept an ID among the fields to change, and shows the command format.

      Use case resumes at step 2.

* 2e. An appointment to remove is not one of the patient's appointments.

    * 2e1. LaiNurse shows an error message naming the appointment, and makes no changes.

      Use case resumes at step 2.

* 2f. An appointment to add is one the patient already has.

    * 2f1. LaiNurse keeps a single copy of the appointment.

      Use case resumes at step 3.

**Use case: UC05 - View patients by appointment date**

**MSS**

1. Nurse requests to view patients with appointments on a date, or between two dates.
2. LaiNurse shows patients who have any appointment on that date or in that range.

   Use case ends.

**Extensions**

* 1a. The date is missing, invalid, or does not exist.

    * 1a1. LaiNurse indicates that the date is invalid.

      Use case resumes at step 1.

* 1b. The nurse gives no start date.

    * 1b1. LaiNurse uses today as the start date.

      Use case resumes at step 2.

* 1c. The start date is after the end date, or the nurse gives only an end date that is before today.

    * 1c1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1d. The nurse gives no end date.

    * 1d1. LaiNurse shows patients with any appointment on or after the start date.

      Use case ends.

* 2a. No patients have an appointment on the given date or in the given range.

    * 2a1. LaiNurse indicates that there are no matching patients.

      Use case ends.

**Use case: UC06 - View a patient's details**

**MSS**

1.  Nurse <u>finds the patient (UC02)</u>
2.  Nurse requests to view the patient by ID
3.  LaiNurse shows all of the patient's details beside the patient list, and highlights the patient in the list if
    the patient is shown there

    Use case ends.

**Extensions**

* 1a. Nurse already knows the patient's ID.

  Use case resumes at step 2.

* 2a. The ID is missing or is not a valid ID.

    * 2a1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2b. The given ID does not match any patient.

    * 2b1. LaiNurse shows an error message, and keeps showing the details it showed before.

      Use case resumes at step 2.

* 3a. Nurse edits the patient while the details are shown.

    * 3a1. LaiNurse shows the new details.

      Use case ends.

* 3b. Nurse deletes the patient while the details are shown.

    * 3b1. LaiNurse stops showing the patient's details.

      Use case ends.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed. On macOS, this must be the JDK version given in the User Guide's Quick Start.
2.  Should be able to hold up to 1000 patients without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. Should function fully offline without requiring an internet connection or a remote server, ensuring home nurses can operate in areas without network connectivity.
5. Should respond to any command within 100 milliseconds under typical workloads.
6. Should be packaged as a single portable JAR file and run directly without requiring an installer.
7. Should store all data locally in a human-editable, plain-text format (e.g., JSON) that can be inspected and backed up manually.
8. Should start with an empty patient list instead of crashing if the data file is invalid.
9. Should not depend on any proprietary third-party software, commercial libraries, or paid external APIs.
10. A new home nurse familiar with standard CLI operations should be able to learn the basic command set within 1 hour by reading the User Guide.
11. Should launch and present the user interface ready for input within 2 seconds on standard desktop hardware.

### Glossary

* **LaiNurse**: The desktop application designed for (home) nurses to manage patient contact details, medical histories,
  and home visit schedules via a Command Line Interface (CLI).
* **Mainstream OS**: Windows, Linux, Unix, or macOS.
* **(Home) Nurse**: The primary target user; a healthcare professional who conducts on-site medical checkups and
  caregiving visits at patients' residential addresses.
* **Patient**: An individual receiving home-based medical care or rehabilitation from a visiting nurse, tracked in the
  system alongside their specific treatment location, clinical history, and scheduled visit times.
* **Patient ID (id)**: A unique, case-insensitive alphanumeric identifier (up to 10 characters) used to distinguish
  individual patients and prevent duplicate profile collisions (e.g., Singapore NRIC/FIN format such as T0123456B).
* **NRIC / FIN**: National Registration Identity Card / Foreign Identification Number; the standard national identity
  identification format used in Singapore.
* **Address**: The physical location of the patient where home nursing care is delivered. This is not necessarily the
  true residential location, as some treatment, e.g. rehabilitation post surgery may be done outdoors.
* **Medical History**: Free-form textual records capturing relevant chronic conditions, allergies, past diagnoses, or
  ongoing medical concerns of a patient (e.g., "dementia").
* **Appointment**: A scheduled date, or date and time, for one of the nurse's home visits to a patient. A patient can
  have any number of appointments. It is in valid date format yyyy-MM-dd (year-month-date) or yyyy-MM-dd HH:mm in
  24-hour format, in local time with no time zone.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

    1. Download the JAR file and copy it into an empty folder.

    1. Double-click the JAR file.<br>
       Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

    1. Resize the window to an optimal size. Move the window to a different location. Close the window.

    1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Viewing a patient

1. Viewing a patient's details

    1. Prerequisites: Start with the sample data, which includes a patient with the ID `S1234567A`.

    1. Test case: `view S1234567A`<br>
       Expected: The panel beside the list shows the patient's name, ID, phone number, address, next appointment and medical history. Their row in the list is highlighted. The status message names the patient.

    1. Test case: `find bernice`, then `view S1234567A`<br>
       Expected: The panel shows the patient with the ID `S1234567A`, although the list shows only the `find` results. No row is highlighted.

    1. Test case: `view S1234567A`, then `delete S1234567A`<br>
       Expected: The panel goes back to its starting message.

    1. Test case: `view S0000000Z`, where no patient has that ID<br>
       Expected: The panel does not change. The status message shows an error.

    1. Other incorrect view commands to try: `view`, `view S123-456` (not a valid ID)<br>
       Expected: The panel does not change. The status message shows the correct command format.

1. Completing a patient ID with Tab

    1. Prerequisites: Start with the sample data, which includes patients with the IDs `S1234567A` and `S2345678B`.

    1. Test case: type `view s1`, then press Tab<br>
       Expected: The command box shows `view S1234567A`. The focus stays in the command box.

    1. Test case: type `delete S`, then press Tab several times<br>
       Expected: Each Tab shows the next ID that starts with `S`, in the order of the list, and then starts again from the first.

    1. Test case: type `edit s23 --number 91234567`, then press Tab<br>
       Expected: The command box shows `edit S2345678B --number 91234567`. The options are kept.

    1. Test case: type `add --id S1`, `view XYZ`, `view S1 extra` or `edit --number 91234567`, then press Tab<br>
       Expected: The command box does not change.

1. Showing all patients again with Esc

    1. Prerequisites: Start with the sample data.

    1. Test case: `find bernice`, then press Esc<br>
       Expected: The list shows all patients again. The status message says that all patients are listed.

    1. Test case: `find bernice`, then type `view` and press Esc<br>
       Expected: The command box is cleared, and the list still shows only the `find` results. A second Esc shows all patients.

1. Going through command history with Up and Down

    1. Prerequisites: Start LaiNurse, then enter `list`, `find bernice` and `view S1234567A`.

    1. Test case: type `abc`, then press Up three times<br>
       Expected: The command box shows `view S1234567A`, then `find bernice`, then `list`. A fourth Up keeps `list`.

    1. Test case: after the test case above, press Down three times<br>
       Expected: The command box shows `find bernice`, then `view S1234567A`, then `abc` again.

### Deleting a patient

1. Deleting a patient

    1. Prerequisites: Start with the sample data, which includes a patient with the ID `S1234567A`.

    1. Test case: `delete S1234567A`<br>
       Expected: The patient with the ID `S1234567A` is deleted from the list. The status message shows the deleted patient's details.

    1. Test case: `delete S0000000Z`, where no patient has that ID<br>
       Expected: No patient is deleted. The status message shows an error.

    1. Other incorrect delete commands to try: `delete`, `delete S123-456` (not a valid ID)<br>
       Expected: No patient is deleted. The status message shows the correct command format.

1. _{ more test cases …​ }_

### Finding patients by substrings

1. Searching each patient field

    1. Prerequisites: Start with the unmodified sample data and enter `list`.

    1. Test case: `find --id 0123`<br>
       Expected: Only Charlotte Oliveiro is shown, because her ID contains `0123`.

    1. Test case: `find --name "lex"`<br>
       Expected: Only Alex Yeoh is shown. Partial names match, even though the previous search showed another patient.

    1. Test case: `find --address “  sErAnGoOn   gArDeNs ”`<br>
       Expected: Bernice Yu and David Li are shown. Matching ignores case and repeated whitespace, and accepts curly quotes.

    1. Test case: `find --number 4388`<br>
       Expected: Only Alex Yeoh is shown, because his phone number contains `4388`.

    1. Test case: `find --medical-history penicillin`<br>
       Expected: Only Bernice Yu is shown. Patients without medical history do not match this criterion.

1. Combining substrings and restoring the list

    1. Test case: `find --name Alex --address "Serangoon Gardens"`<br>
       Expected: Alex Yeoh, Bernice Yu and David Li are shown, in their original order. Either field may match.

    1. Test case: `find --name Alex --id 1234567`<br>
       Expected: Alex Yeoh and Irfan Ibrahim are shown. Alex appears once even though both criteria match him.

    1. Test case: `find --address "Gardens Serangoon"`<br>
       Expected: An empty list is shown with a count of zero. Multiword values must match a contiguous substring.

    1. Test case: `list`<br>
       Expected: All six sample patients are shown again.

1. Rejecting invalid commands

    1. Prerequisites: Enter `find --name Alex`, so only Alex Yeoh is shown.

    1. Test cases: `find`, `find Alex`, `find --name ""`, `find --name "Alex`, `find --name “Alex"`,
       `find --name "Alex" trailing`, `find --unknown Alex`, and `find --name=Alex`<br>
       Expected: The displayed list and patient records are unchanged. The status message shows the command usage instructions.

    1. Test case: `find --next-appointment 2026-10`<br>
       Expected: The same as above. `find` does not search appointments; use `viewappt` instead.

    1. Test case: `find --name Alex --name Bernice`<br>
       Expected: The displayed list and patient records are unchanged. The status message shows a duplicate-option error.

    1. Enter `list` after testing to show all patients again.

### Viewing patients by appointment date

1. Viewing patients who have an appointment on a given date

    1. Prerequisites: Start with the sample data, which includes patients with the appointments
       `2026-10-05 09:30` and `2026-11-02` (Alex Yeoh), `2026-10-06` (Bernice Yu) and `2026-10-07 14:00` (Irfan Ibrahim).
       If you have modified the data, delete the data file and relaunch the app to restore the sample data.

    1. Test case: `viewappt 2026-10-07`<br>
       Expected: Only Irfan Ibrahim is shown in the list. The status message shows `1 patient(s) with appointments on 2026-10-07`.

    1. Test case: `viewappt 2026-11-02`, the date of Alex Yeoh's second appointment<br>
       Expected: Only Alex Yeoh is shown, because every appointment of each patient is checked.

    1. Test case: `viewappt 2026-11-11`, a date on which no patient has an appointment<br>
       Expected: An empty list is shown. The status message shows `0 patient(s) with appointments on 2026-11-11`.

    1. Test case: `viewappt 2026-02-30`, an impossible date<br>
       Expected: The list is unchanged. The status message shows an error: `Dates should be a real date in the format yyyy-MM-dd`.

    1. Test case: `viewappt 15/10/2026`, a date in the wrong format<br>
       Expected: The list is unchanged. The status message shows the same date format error as above.

    1. Other incorrect `viewappt` commands to try: `viewappt` (missing date), `viewappt 2026-10-15 2026-10-20` (more than one date)<br>
       Expected: The list is unchanged. The status message shows the correct command format or the date format error.

1. Viewing patients who have an appointment in a date range

    1. Prerequisites: The same sample data as above.

    1. Test case: `viewappt --from 2026-10-01 --till 2026-10-31`<br>
       Expected: Alex Yeoh, Bernice Yu and Irfan Ibrahim are shown. The status message shows
       `3 patient(s) with appointments from 2026-10-01 to 2026-10-31`.

    1. Test case: `viewappt --till 2026-10-06 --from 2026-10-06`<br>
       Expected: Only Bernice Yu is shown. The options may be in any order, and a one-day range behaves like `viewappt 2026-10-06`.

    1. Test case: `viewappt --from 2026-10-07`<br>
       Expected: Alex Yeoh (appointment `2026-11-02`) and Irfan Ibrahim are shown. The status message shows
       `2 patient(s) with appointments on or after 2026-10-07`.

    1. Test case: `viewappt --till 2099-12-31`<br>
       Expected: Only patients with an appointment from today onwards are shown. The range in the status message starts with today's date,
       which is the current date in your computer's time zone.

    1. Test case: `viewappt --till 2000-01-01`<br>
       Expected: The list is unchanged. The status message says that the `--till` date is before today.

    1. Test case: `viewappt --from 2026-10-31 --till 2026-10-01`<br>
       Expected: The list is unchanged. The status message says that the `--from` date must not be after the `--till` date.

    1. Other incorrect commands to try: `viewappt 2026-10-15 --till 2026-10-31` (a date and an option),
       `viewappt --to 2026-10-31` (unknown option), `viewappt --from 2026-10-01 --from 2026-10-02` (repeated option)<br>
       Expected: The list is unchanged. The status message shows the command format or a duplicate-option error.

1. Viewing a date-only appointment next to a timed one on the same day

    1. Prerequisites: Start with the sample data, then add a timed appointment on the same date as Bernice Yu's
       date-only one: `add --id T0456789D --name Eve Tan --number 98765432 --address Blk 123 Clementi Ave 3, #05-12 --appt 2026-10-06 15:00`.

    1. Test case: `viewappt 2026-10-06`<br>
       Expected: Both Bernice Yu (appointment `2026-10-06`, no time) and Eve Tan (appointment `2026-10-06 15:00`) are shown.
       The status message shows `2 patient(s) with appointments on 2026-10-06`.

    1. Note: the list stays filtered after a `viewappt` command. After testing, enter `list` to show all patients again.

1. _{ more test cases …​ }_

### Editing a patient

1. Editing a patient's details by ID

    1. Prerequisites: Start with the sample data. Alex Yeoh has the ID `S1234567A`.

    1. Test case: `edit S1234567A --number 91234567`<br>
       Expected: Alex Yeoh's phone number changes to `91234567`. The other details are unchanged.

    1. Test case: `edit S0000000Z --number 91234567`, an ID that no patient has<br>
       Expected: No patient is changed. The status message shows `The patient ID provided is invalid.`

    1. Test case: `edit S1234567A`, with nothing to change<br>
       Expected: No patient is changed. The status message shows `At least one field to edit must be provided.`

1. Adding and removing appointments

    1. Prerequisites: Start with the sample data. Alex Yeoh has the appointments `2026-10-05 09:30` and `2026-11-02`.

    1. Test case: `edit S1234567A --add-appt 2026-10-20 --add-appt 2026-12-01 10:00`<br>
       Expected: Alex Yeoh's card lists four appointments, from earliest to latest.

    1. Test case: `edit S1234567A --remove-appt 2026-10-05 09:30 --add-appt 2026-10-06 09:30`<br>
       Expected: The 5 October appointment is replaced by one on 6 October.

    1. Test case: `edit S1234567A --remove-appt 2026-11-02 09:30`, which does not match the date-only appointment<br>
       Expected: No patient is changed. The status message shows `Patient S1234567A has no appointment on 2026-11-02 09:30.`

    1. Test case: `edit S1234567A --add-appt 2026-02-30`, an impossible date<br>
       Expected: No patient is changed. The status message shows the appointment format error.

### Saving data

1. Dealing with missing/corrupted data files

    1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
