---
layout: page
title: Developer Guide
---

* Table of Contents {:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* Codebase adapted from [AddressBook-Level3 (AB3)](https://github.com/se-edu/addressbook-level3) developed by the SE-EDU initiative.
* UI components and controls utilize native [JavaFX 17+](https://openjfx.io/) libraries.

* AI Use Declaration from Qin Fangzheng: "As my career path is not related to software engineering, I choose AI-5/AI-6 as my AI use level, where I get Codex to do the tasks, and then I myself review the results, including feature behavior and partial code."

* AI Use Declaration from Jaydon: "I used Codex and OpenCode to review and generate code.
  Based on the given levels of AI use in coding tasks, this ranges between AI-2 and AI-6".

* AI Use Declaration from Yuhao: "I used Claude Code to write code, tests and documentation, and I review and test the results."

* AI Use Declaration from Yong Kang: "I used Gemini to write and review code, tests and documentation. No automation is done and I go back and forth consistently with AI to ensure I am still largely in charge of the process. AI levels between AI-1 and AI-5."

* AI Use Declaration from Darren: "I used Claude Code to write code, tests and documentation for the `edit` command and
  the appointment model, and to review my changes. I decide the design and behavior, and I review, test and revise
  the results before committing them."

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
`AppointmentListPanel`, two `PatientDetailPanel`s, and `StatusBarFooter`. All of these, including `MainWindow`, inherit
from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

`MainWindow` shows one of two views at a time, and a tab for each above them. The patient view holds
`PatientListPanel` and a `PatientDetailPanel`. The appointment view holds `AppointmentListPanel` and a second
`PatientDetailPanel`, so that each view keeps its own state while it is hidden. A `CommandResult` can name the view to
show (see [Patient and appointment views](#patient-and-appointment-views)), and clicking a tab, or Left and Right with the
focus on the tabs, switch the view without running a command.

In each view, `MainWindow#shareWidth` splits the width between the list and the details, so that both grow and shrink
together as the window is resized. Up to a window about 1,000 pixels wide, each takes half. In a wider window, the
list's share falls steadily, to 28% in a window 1,920 pixels wide, a full HD screen, and stays there in a wider one, so
that the details take most of the extra room. The list itself never gets narrower as the window gets wider. The window
can be no narrower than both at their minimum widths. In a narrow list header, the note, such as `This week`, and the
count give up their room before the dates do. With room, `PatientDetailPanel` shows each appointment's date, time and
details side by side, with the same widths in every row, so that they line up. In narrow details, it puts the details
under the time, shrinks the dates of all rows together and lets the times wrap, so that the times stay in line and no
row is wider than the panel.

`PatientListPanel` shows each patient in the filtered list as a `PatientRow`, with their next appointment, and its
`PatientDetailPanel` shows all the details of one patient. Above the rows, a header says whether the list is filtered,
and how many patients it shows. The panel takes both the filtered list and the list of all patients, and counts the list
as filtered while it shows fewer patients than there are, so it needs no knowledge of `find` or `viewappt`. When a
`CommandResult` names a patient to view, as the result of `view` does, `MainWindow` tells that `PatientDetailPanel` to
show the patient. After every command, `MainWindow` highlights the shown patient's row in `PatientListPanel`, or no row if
that patient is not in the displayed list. `PatientDetailPanel` finds the patient by ID in the list of all patients from
`Logic#getPatientList()`, and listens to that list. So it shows a patient's new details after an edit, and goes back to
its starting message after the patient is deleted, whatever the filter on the displayed list is.

An appointment has passed once its time today has gone by, or once its day is over if it has no time.
`AppointmentFormatter#hasPassed` holds this rule, and the three places that show appointments use it:

* A patient's row shows their first appointment that has not passed, from `AppointmentFormatter#findFirstNotPassed`.
* `DetailAppointments` picks the appointments that the details list: every one that has not passed, and the latest ten
  that have, plus a marked older one.
* The appointment list greys out the appointments that have passed.

Each of these decides what has passed, and which day is today, when it is drawn. So that they stay right while the
window is left open, `MinuteClock` reads the clock every few seconds and tells `MainWindow` when the minute changes.
`MainWindow` then asks `AppointmentFormatter#hasChangedBetween` whether an appointment has passed, or a new day has
begun, since the minute before. Only then does it refresh both lists and both details panels, which keep their
selection. Reading the clock every few seconds, rather than once a minute, also catches up within seconds after the
computer wakes from sleep.

`AppointmentListPanel` shows the appointments of `Logic#getAppointmentList()`, which holds every appointment of every
patient, on one of two kinds of page. The weekly view shows one `AppointmentWeek`, from Monday to Sunday, which a plain
`viewappt` shows for this week. After `viewappt` with a date, a range or `--id`, the panel shows only the appointments
in the command's range, on one page, and hides the week's arrow buttons; if the range carries a patient ID, only that
patient's appointments. `AppointmentPage#ofWeek` and
`AppointmentPage#ofRange` turn the appointments into rows: a heading for each day
of the week, or for each day in the range that has an appointment, followed by that day's appointments, numbered down
the page. The panel draws each heading itself, and each appointment as an `AppointmentRow`. An appointment with no time comes first in its day, except today, where it comes after the appointments that
have passed, right above the next one. A page is built as at the time given to it, so the page and the appointment that
it selects first agree, and unit tests can fix the time. While a range is shown, the panel keeps the week of the weekly
view, so that Esc goes back to it. The panel listens to the appointment list and rebuilds the page after a change,
keeping the selected appointment if it is still there. Only appointment rows can be selected, so the panel handles Up,
Down, Home, End, Left and Right itself, with the moves that `AppointmentPage` finds, and headings ignore the mouse. When
the selection changes, the panel tells `MainWindow`, which shows the appointment's patient in the second
`PatientDetailPanel` with that appointment marked.

`DarkTheme.css` gives each highlight colour one meaning, as two named colours at its top: orange for urgency, which is
whatever is today, and a blue for selection, which is the keyboard focus and the selected tab, row and
appointment. So the marked appointment in the details is orange only if it is today.

When the user presses Tab, `CommandBox` asks `Logic#getCompletions(String)` for the ways to finish the patient ID in the
command, and replaces the text with the first one. Each further Tab gives the next one. With nothing to finish, Tab
does nothing, so it never moves the focus out of the command box. `IdCompleter` in the `Logic` component holds the
rules, so `CommandBox` needs no knowledge of the command formats. `view` and `delete` take only an ID, so the ID must
end the command. `edit`, `addappt`, `editappt` and `deleteappt` take the ID first and options after it, so
`IdCompleter` completes the word after the command word and keeps the options. `CommandBox` also keeps a `CommandHistory` of the commands entered since the app
started, which the Up and Down keys go through. The history is kept only in the UI, because no command needs it.

The keyboard focus is in one of three areas, given by `FocusArea`: the command box, and in the view that is shown, the
view tabs or the list. The details beside the list are not an area, so the keyboard never moves the focus there. In the
view, Tab moves between the tabs and the list, as `FocusArea#afterTab` gives. Up and Down move between them too: each
list calls back to `MainWindow` when Up is pressed at its first row, and `MainWindow` handles Down on the tabs. Up on
the tabs does nothing: `MainWindow` consumes it, so that JavaFX does not move the focus to another node. In both lists,
Ctrl+Up and Ctrl+Down (Cmd on macOS, as given by `KeyEvent#isShortcutDown`) select the first and last row. The tabs
take the focus only from the keyboard, so a click on a tab does not move the focus. If a tab has the focus when the view
changes, `MainWindow` moves the focus to the tab of the view being shown.

Esc in the command box calls back to `MainWindow`, which moves the focus to the list, or to the tabs if the patient list
is empty, and leaves the typed text in place. Outside the command box, `MainWindow` handles keys with event filters on
the window, which see each key before the focused part does. `BrowseKeys` decides what Enter, Esc, Tab, Shift+Up,
Shift+Down and typed text do there:

* Enter, and any printable character, move the focus back to the command box. A typed character is added to the text
  there.
* Esc resets the view, and keeps the selected entry so that it is seen among the rest. In the patient view,
  `MainWindow#resetView` calls `Logic#showAllPatients`, which removes the filter of the patient list as `list` does,
  but runs no command. So nothing is saved, the status message stays, and no `Navigation` selects the first patient;
  `resetView` only selects again, in the longer list, the patient whose details are shown. (Running `list` itself would
  show the first patient's details and then the earlier patient's again.) In the appointment view,
  `AppointmentListPanel#reset` goes back from a
  `viewappt` page to the weekly view of the selected appointment's week, or of this week if none is selected, and
  keeps the selection. In the weekly view, there is no range to remove, so Esc does nothing.
* Tab moves the focus between the tabs and the list. The appointment list always takes the focus, so that Left and
  Right can leave an empty week.
* Shift+Up and Shift+Down scroll the details beside the list, with `PatientDetailPanel#scroll`, as the details never
  take the focus.
* Shift+Tab does nothing, also in the command box. `MainWindow` consumes it, or JavaFX would move the focus to another
  node.

The menu bar must not take these keys. JavaFX's `MenuBar` selects a menu that the mouse is over, in a focused window,
and while a menu is selected, a key filter that it adds to the scene takes Left and Right to move between the menus.
That filter runs before the filters on the tabs and the lists, and the menu stays selected after the mouse has left,
until a click outside the menu bar. So `MainWindow#ignoreHoverOfClosedMenus` stops the mouse-entered events of the
menus while no menu is open. As JavaFX then sets no `:hover`, it gives the menu under the mouse the `hovered`
pseudo-class, which `DarkTheme.css` styles as Modena styles `:hover`. A click still opens a menu, and an open menu
still follows the mouse to the next one.

`FocusArea`, `BrowseKeys`, `AppointmentWeek`, `AppointmentPage`, `DetailAppointments` and `AppointmentFormatter` hold
no JavaFX nodes, so unit tests cover these rules, and the UI parts only connect them to JavaFX.

The selected patient in the list is always the patient whose details are shown. When the focus moves to the list and
no row is selected, `MainWindow` selects the first patient and shows them. `PatientListPanel` shows the details of a
newly selected patient only while the user holds a key that moves the selection, such as Up or Down, or clicks a row.
This is because a command that filters or removes rows also moves the selection in the `ListView`, and that must not
change the shown patient. After each command, `MainWindow` selects the shown patient's row again.

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

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete
S1234567A")` API
call as an example.

![Interactions Inside the Logic Component for the `delete S1234567A` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

The appointment commands follow the same shape. The diagram below shows the `addappt` command, which uses
`Model#findPatientById()` to find the patient among all patients and adds the appointment to their schedule.

<img src="images/AddAppointmentSequenceDiagram.png" width="800" />

The `deleteappt` command also follows this shape, up to the lookup: it finds the matching appointment with
`AppointmentLookup#find()` and removes it from the patient's schedule.

<img src="images/DeleteAppointmentSequenceDiagram.png" width="800" />

The `viewappt` command follows the same shape: it builds an `AppointmentBetweenDatesPredicate` from the date
range and filters the patient list with it (`--id` narrows both the appointments and the patient list to one
patient), then returns a `CommandResult` carrying the range for the appointment view.

<img src="images/ViewAppointmentSequenceDiagram.png" width="800" />

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to a `LaiNurseParser` object, which in
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

* When called upon to parse a user command, the `LaiNurseParser` class creates an `XYZCommandParser` (`XYZ` is a
  placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to
  parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `LaiNurseParser` returns that
  object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface
  so they can be treated similarly where appropriate, for example during testing.

### Model component

**API** : [
`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the LaiNurse data i.e., all `Patient` objects (which are contained in a `UniquePatientList` object).
* stores the `Patient` objects selected by the current filter, such as search results, in a separate _filtered_ list. It
  exposes this list as an unmodifiable `ObservableList<Patient>` that the UI can observe and bind to, so the UI updates
  when the list changes. The filtered list is in name order: `ModelManager` keeps a `SortedList` with
  `PatientNameComparator`, which compares by name ignoring case and then by patient ID, between the patient list and
  the filter. A second `SortedList` over the filter orders the results when a command gives a comparator, such as by
  the number of fields matched by `find`. The stored patient order is unchanged.
* keeps a list of every appointment of every patient, each as a `PatientAppointment`, in time order. It rebuilds the
  list whenever a patient is added, edited or deleted, and exposes it as an unmodifiable
  `ObservableList<PatientAppointment>` for the appointment view. The filter on the patient list does not change it.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed
  to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they
  should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `LaiNurse`, and each `Person` references tags from that list. This lets `LaiNurse` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>

### Storage component

**API** : [
`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,

* can save both LaiNurse data and user preference data in JSON format, and read them back into corresponding
  objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonLaiNurseStorage` and
  `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects
  that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Finding patients by substrings

`FindCommandParser` recognizes the six patient-detail options defined in `CliSyntax`: ID, name, phone number, address,
medical history and notes. It reuses `ArgumentTokenizer.normalizeOptionSeparators`, as `add` and `edit` do,
then uses `ArgumentTokenizer` and `ArgumentMultimap` to extract values and reject
duplicate options. At least one option is required, and every supplied value must be non-blank. Matching straight or
curly double quotes surrounding a complete value are removed. Unknown options, unexpected preamble text, and malformed
quotes cause a `ParseException` before the displayed list is changed. Words beginning with `--` remain reserved for
options, including inside quoted values.

`PatientContainsSubstringsPredicate` stores six optional string criteria normalized by `TextNormalizer`. Each criterion
is a literal, contiguous substring of its corresponding stored field. `countMatchingFields()` counts each matching
supplied field once, and `test()` includes a patient when this count exceeds zero, preserving the OR search. Repeated
occurrences of a substring in one field contribute only one match. Missing medical history or notes contributes no
match for that field. Phone criteria are substrings rather than complete phone numbers, so the parser does not apply
the validation used when adding a patient. `find` does not search appointments. `viewappt` handles every appointment
search, as described in [Viewing appointments by date](#viewing-appointments-by-date).

`FindCommand` passes the predicate and a comparator to `Model#updateFilteredPatientList(predicate, comparator)` and
reports the existing patient count message. The comparator orders patients by descending match count, then by
`PatientNameComparator`, so ties are in name order. Each search replaces the previous filter and searches the full
patient collection. `ModelManager` keeps one `SortedList` over its `FilteredList`, so changes to patient records update
the same observable view. Filtering and sorting do not change stored records or their order. The single-argument
filter method clears sorting, as does a null comparator; existing calls from `list`, `add`, `edit` and `viewappt`
therefore restore name order. The appointment list is independent of this sorting.

### Editing a patient's details

`edit ID [OPTION VALUE]...` changes the fields of the patient with the given ID. The sequence diagram below shows how
`edit S1234567A --phone 91234567` changes the phone number of patient `S1234567A`.

<img src="images/EditSequenceDiagram.png" width="800" />

`EditCommandParser` reads the ID from the preamble and stores each change in an `EditPatientDescriptor`. A field that
is not given stays `null` in the descriptor, so `EditCommand#createEditedPatient()` keeps the patient's value for it.
Each value is checked by the same `ParserUtil` method as in `add`, so a blank value is rejected for every field,
including the optional medical history and notes. `EditCommand` finds the patient among all patients, not only those
in the displayed list, then replaces them through `Model#setPatient()`. A new `Patient` is built because `Patient` is
immutable, so a command that fails leaves the original patient unchanged.

**Design considerations**

* **Identifying the patient: by ID (chosen) or by list number.** An ID stays the same when the list is filtered or
  sorted, so the nurse cannot edit the wrong patient by using a stale list number, and can edit a patient who is not
  shown. The cost is typing a longer value, which the command box's ID completion reduces.
* **A blank optional value: rejected (chosen) or read as clearing the field.** Rejecting it keeps `edit` consistent
  with `add`, and a stray option cannot remove a medical history or notes by accident. The cost is that `edit` cannot
  clear these fields.

### Storing multiple appointments

Each patient holds an `AppointmentSchedule`, an immutable list of `Appointment` objects. `add` always creates a
patient whose schedule is empty, and `addappt` adds an appointment to it, `editappt` replaces one, and `deleteappt`
removes one. Each change returns a new `AppointmentSchedule` instead of modifying the old one,
so a failed command leaves the patient unchanged.

**Equality.** An `Appointment` stores a date, an optional time and optional details. Its date and time identify it, as
`Appointment#isSameAppointment` gives, while `equals` also compares the details. Dates and times are parsed with strict
formatters that accept only one spelling of each value, so equal text means the same date and the same time (or both
having no time).

**Order.** Appointments are ordered by date, then by time. On the same day, an appointment with only a date comes before
any appointment with a time. A date-only appointment means "sometime that day", like an all-day event in a calendar, so
it is listed at the top of that day. `compareTo` ignores the details, as `isSameAppointment` does, so
`AppointmentSchedule` can use a `TreeSet` to sort the appointments and keep one at each date and time.

**Duplicates.** Only appointments at the same date and time are duplicates. `2026-10-05` and `2026-10-05 00:00` are
different appointments: the first has no fixed time, while the second is at midnight. Merging them would lose
information that the nurse entered. Deleting an appointment with `deleteappt` also targets the same date and time, so
`deleteappt` with `--appt 2026-10-05` does not delete `2026-10-05 09:30`.

**Storage and compatibility.** `JsonAdaptedPatient` saves the appointments as an `"appointments"` list of objects, such
as `{"dateTime": "2026-10-05 09:30", "details": "INSULIN REVIEW"}`. It still reads appointments saved as plain strings
from older data files. `JsonSerializableLaiNurse` saves the patients under the `"patients"` key. A data file without
it, such as one from v1.3 that saved them under `"persons"`, fails to load, and LaiNurse starts with an empty list.

**Time zones.** An `Appointment` holds a `LocalDate` and an optional `LocalTime`, with no time zone or offset, and is
saved as text such as `2026-10-05 09:30`. This assumes that the nurse and their patients are in one time zone, which
holds for home visits, so an appointment means the local date and time of the visit. The app never converts appointments
between time zones, so changing the computer's time zone, or a daylight saving change, does not move them.

### Patient notes and appointment details

A patient has optional `Notes` for other personal information not covered by the other fields, such as hobbies, pet peeves
and daily routines. An appointment has optional `AppointmentDetails`, free text about the visit, such as what to do or
bring. Both follow `MedicalHistory`: `TextNormalizer` trims the text, collapses its whitespace and stores it in upper case, and
blank text is rejected. `Patient#getNotes()` and `Appointment#getDetails()` return an `Optional`.

The details panel shows the notes under the medical history, and each appointment's details beside its time, or under it
in a narrow panel. `AppointmentRow` shows an appointment's details on a second line under its patient, and only when it
has some.

`add` and `edit` accept the optional, single-valued `--notes` option defined by `CliSyntax.PREFIX_NOTES`.
`ParserUtil.parseNotes` validates it using `Notes.isValidNotes` and throws a `ParseException` with
`Notes.MESSAGE_CONSTRAINTS` for blank text. It logs parsing success or rejection at `FINE` level without logging the text.
`add` starts a patient without notes when the option is omitted. `EditPatientDescriptor` stores a supplied notes value;
`edit` replaces the patient's notes when supplied and preserves them otherwise. An empty option is rejected, so it does
not clear notes. Notes are included in patient feedback through `Messages.format`. `find --notes` uses the substring
search described above, and `view ID` shows the notes using the existing detail panel and its live updates.

`editappt` sets an appointment's details with the `--details` option defined by `CliSyntax.PREFIX_DETAILS`.
`ParserUtil.parseAppointmentDetails` rejects blank details, as `parseNotes` does for notes, and logs without the text.
`editappt` and `deleteappt` touch only the appointment they target; every other appointment keeps its details.

`editappt ID --appt DATE_OR_DATETIME` finds the appointment to edit with `AppointmentSchedule#find`, which returns the
patient's appointment at that exact date and time, with its details. Because the match is exact, a date alone never
matches a timed appointment. If there is no match, `EditAppointmentCommand` lists the patient's appointments on that
day, from `AppointmentSchedule#getOnDate`, in the error message, so that the nurse can retype the right one. The edited
appointment is built from the new date and time from `--to`, if given, and the new details from `--details`, if given;
the rest is kept from the old appointment. The command rejects a move onto another of the patient's appointments,
because the schedule keeps only one appointment at each date and time, and one of them would be lost. Otherwise, it
replaces the patient through `Model#setPatient()` with a schedule that has the old appointment removed and the edited
one added, and selects the edited appointment with `Navigation.toAppointment`.

The activity diagram below shows the checks that `editappt` makes, in order. Every error leaves the patient and the
data file unchanged.

<img src="images/EditAppointmentActivityDiagram.png" width="750" />

`addappt` adds a new appointment to a patient's schedule with the same options as `editappt` uses to find and set
one: `--appt` takes the date, or date and time, and `--details` takes the appointment's
details, validated by `ParserUtil.parseAppointmentDetails`. `AddAppointmentCommand` finds the patient by
`Model#findPatientById()`, then checks `AppointmentSchedule#contains` before adding: `AppointmentSchedule#add` keeps
only the first appointment at each date and time, so without that check, adding onto an existing slot would silently
replace nothing and lose the new appointment. The check makes the duplicate slot a `CommandException` instead. The
command then replaces the patient through `Model#setPatient()` with a schedule that has the appointment added, and
selects it with `Navigation.toAppointment`, as `editappt` does for the edited one. `Model#setPatient()` does not
change the filter of the patient list, so a patient hidden by `find` or `viewappt` stays hidden there.

### Deleting an appointment

`deleteappt ID --appt DATE_OR_DATETIME` removes one appointment from a patient's schedule. `DeleteAppointmentCommandParser`
normalizes option separators in the same way as `addappt`, with its `--appt` required; details are not an option,
because an appointment is found by its date and time alone. `DeleteAppointmentCommand` finds the patient with
`Model#findPatientById()`, then looks the appointment up with `AppointmentSchedule#find` at the exact date and time,
ignoring details on both sides: whichever appointment is scheduled there, with whatever details, is the one deleted.
If there is no
match, the command fails with a `CommandException` that lists the patient's appointments on that day, from
`AppointmentSchedule#getOnDate`, so the nurse can retype the one they meant, as `editappt` does. Otherwise, it
replaces the patient through `Model#setPatient()` with a schedule that has the appointment removed.

Unlike `addappt` and `editappt`, the command clears the selection with `CommandResult#withSelectionCleared`: after
it succeeds, no appointment exists to select, and the deleted one cannot be highlighted again. The returned result
has no `Navigation`, while `getView()` still shows the appointments view, so `MainWindow` switches to that view with
the selection cleared, and `AppointmentListPanel` keeps the page that was shown; the deleted row disappears from
it. `Model#setPatient()` does not change the filter
of the patient list, so a patient hidden by `find` or `viewappt` stays hidden there, as with `addappt`.

### Viewing appointments by date

`viewappt` takes one date, a range given by `--from`, `--to`, or both, or none of these. A plain `viewappt`, with none
of these and no `--id`, shows the weekly view of this week: the command filters the patient list to the patients with
an appointment from this week's Monday to its Sunday, and returns no range, only a `Navigation.toView`, so that
`MainWindow` shows this week and selects the next appointment from now. The command and the weekly view's
`AppointmentWeek` both take the week from `DateUtil#getStartOfWeek` and `DateUtil#getEndOfWeek` in Commons, so the
patient list and the week shown cannot disagree on its days. `ViewAppointmentCommandParser`
normalizes option separators in the same way as `add`, so unknown options are rejected. A single date becomes a range
that starts and ends on that date. A word of digits and punctuation before the options, such as `2026-02-30` or
`2026/10/15`, can only be meant as a date, so it gets the date error from `ParserUtil#parseDate`, as a date given to
`--from` or `--to` does. Any other word, such as `invalid`, could be a mistyped option, so it gets the command format. When both options are given, the parser rejects a `--from` date that is after the
`--to` date.

`ViewAppointmentCommand` stores the start and end dates as `Optional<LocalDate>` values. It works out the range only
when it runs: a missing start date becomes today, and a missing end date becomes `LocalDate.MAX`. Today is
`LocalDate.now(clock)`, where the clock is `Clock.systemDefaultZone()`, so it is the current date in the computer's
default time zone. A package-private constructor takes another `Clock`, which lets tests fix the date. Because the
command reads the date when it runs rather than when it is parsed, it never compares against a stale day. If only
`--to` is given and its date is before today, the range would be empty, so the command shows an error that suggests
adding `--from`. The command then filters the list with `AppointmentBetweenDatesPredicate`, which shows a patient if any
of their appointments falls in the range. Only dates are compared, so a timed appointment earlier today still counts.

The command also returns the range in its `CommandResult`, as a `CommandResult.AppointmentRange` whose end is
`LocalDate.MAX` if the range has no end, and a `Navigation.toView(View.APPOINTMENTS)`. `MainWindow` then tells
`AppointmentListPanel` to show only the appointments in that range, on one page, and to select the next appointment
from now.

With `--id`, the parser also reads a `PatientId`. `--id` alone, with no date, shows every appointment of that patient:
the range runs from the date of their first appointment to the date of their last, or is today alone if they have
none. A date typed after the options ends up inside the ID value, which
`ParserUtil#parseId` fails as an invalid ID. When the command runs, it looks the patient up by ID,
and if none matches, fails with the invalid-patient-ID error and leaves the list and the page as they were. Otherwise
it narrows the list to that patient with an appointment in the range, and counts that patient's appointments in the
range for the status message, so a patient seen twice on the day reads "2 appointment(s)", where the all-patients
messages count patients. The `AppointmentRange` also carries the ID, and `AppointmentListPanel` filters the page's
appointments by it, so the page shows only that patient's until Esc or another `viewappt` replaces the range.

**Design considerations: where appointment searches belong**

* **Alternative 1 (current choice):** Only `viewappt` searches appointments, by real dates.
    * Pros: Each command answers one question. `find` answers "who is this patient?", and `viewappt` answers "whom do I
      visit, and when?". Only `AppointmentBetweenDatesPredicate` matches appointments, and it checks every appointment.
    * Cons: A search such as "anyone called Alex who has an appointment in October" still needs two commands, one for
      each question. There is no search by time of day.
* **Alternative 2:** `find` also takes an appointment option and matches it as a substring of each appointment.
    * Pros: Appointment searches can be combined with other `find` options in one command.
    * Cons: Substrings are ambiguous for dates. For example, `10` matches October, the 10th of any month, and 10:00.
      Appointment matching would also live in two places.

### Patient and appointment views

The main window shows a patient view or an appointment view, on two tabs. Commands choose the view: a command word
that ends in `appt` shows the appointment view, and `list`, `find`, `view`, `add` and `edit` show the patient view.
Other commands keep the view that is shown.

Each `Command` declares the view it shows by overriding `Command#getView()`, which returns `Optional.empty()` by default.
`LogicManager#execute` adds that view to the command's `CommandResult` with `CommandResult#withView`, and
`MainWindow#executeCommand` switches to it. A `CommandResult` can also hold an `AppointmentRange`, as the result of
`viewappt` with a date, a range or `--id` does. Then `MainWindow` tells `AppointmentListPanel` to show the appointments
in that range on one page. The result of a plain `viewappt` has no range, so the panel shows the weekly view of this
week.

A command can also choose the entry to select, by adding a `CommandResult.Navigation` with
`CommandResult#withNavigation`. `Navigation.toPatient(id)` selects that patient in the patient view, and
`Navigation.toAppointment(entry)` selects that appointment in the appointment view. If the result has no range, the
appointment view stays on the page shown if that page has the appointment, so that a nurse working through a range
stays on it, or else shows the appointment's week. `Navigation.toView(view)` selects the entry to start from: the
first patient in the list, or the next appointment from now (the first, if all have passed), in the result's range
or, if the result has no range, in this week. A command that filters the patient list or shows a range can select an
entry in the same result, because the entry is selected after the filter and the range are applied. A result can
instead clear the selection with `CommandResult#withSelectionCleared`, which `MainWindow` applies to both views, so
that no patient's details are shown.

An `AppointmentRange` can also name a patient, with `new AppointmentRange(start, end, id)`. The page
then shows only that patient's appointments, and its header starts with their ID.

After the command, `LogicManager` checks that the entry to select is in the list that the view shows: a patient in
the filtered patient list, and an appointment in the range, including the range's patient. If it is not, the
command has a bug, and `LogicManager` throws an `IllegalStateException`.

Each command navigates as follows:

| Command | Result |
| --- | --- |
| `add`, `edit`, `view` | `Navigation.toPatient` of the patient that they add, edit or show. All three show all patients first, so that patient is always in the list. |
| `list`, `find` | `Navigation.toView(View.PATIENTS)`, which selects the first patient in the list |
| `viewappt` | `Navigation.toView(View.APPOINTMENTS)`, with the range of a date, of `--from`/`--to` or of `--id` alone, and with no range for a plain `viewappt` |
| `addappt`, `editappt` | `Navigation.toAppointment` of the added or edited appointment, with no range, so the page shown stays if it has the appointment |
| `delete`, `deleteappt`, `clear` | `withSelectionCleared()`: keeps the view and selects nothing |
| `help`, `exit` | no navigation: keeps the view and the selection |

Switching the tab by mouse or key runs no command and changes no model state. So the result display, the filtered
patient list and the shown page of appointments all keep their state. If the keyboard focus is in the view being hidden, `MainWindow`
moves it to the tab of the view being shown.

**Design considerations: where the view of a command is decided**

* **Alternative 1 (current choice):** Each command class declares its view, and `LogicManager` adds it to the result.
    * Pros: The rule for a command lives in its own class, and a new command that ends in `appt` declares its view in
      one method. Commands build their results as before, so the tests that compare a command's whole
      `CommandResult` did not change.
    * Cons: A command cannot pick a different view depending on its input.
* **Alternative 2:** Each command sets the view in the `CommandResult` it builds.
    * Pros: A command can choose the view at run time.
    * Cons: Every command that shows a view must build its result differently, and every test of those commands must
      expect the view.

**Design considerations: what the appointment view lists**

* **Alternative 1 (current choice):** The weekly view lists every appointment, a week at a time, and a plain `viewappt`
  shows this week. `viewappt` with a date, a range or `--id` shows only the appointments in its range, on one page with
  a heading for each day that has one, and Esc goes back to the weekly view.
    * Pros: The view shows exactly the appointments that the command found, however many weeks the range covers, and
      no day looks empty because of a filter. The filter lives in the UI, so the model needs no second filtered list.
    * Cons: The panel has two kinds of page, and the user presses Esc to get back to the weekly view.
* **Alternative 2:** The view always shows one week of every appointment, and `viewappt` only chooses the week and the
  selected appointment.
    * Pros: There is one kind of page, and a week always shows all the visits of that week.
    * Cons: After `viewappt` with a range, appointments outside the range still show, and a range of several weeks
      needs paging to see.

### Three-tier help system and offline User Guide rendering

The help system provides a three-tier support model designed to give nurses immediate CLI feedback as well as complete offline reference documentation without network connectivity:

1. **General CLI Overview (`help`)**: Displays a concise summary of all available command words directly in the result display box.
2. **Targeted CLI Help (`help [COMMAND]`)**: Displays parameter formats, constraints, and syntax examples for a specific command (e.g., `help add`) directly in the result display box by reading canonical `MESSAGE_USAGE` definitions.
3. **Pop-up User Guide Window (`help window`, menu bar, or <kbd>F1</kbd>)**: Opens a standalone non-modal window displaying the full, offline User Guide rendered using native JavaFX components.

#### Implementation

The help architecture spans argument parsing in `Logic`, command dispatching in `HelpCommand`, and native rendering in `UI`.

##### 1. Command Parsing & Target Normalization (`HelpCommandParser`)

`HelpCommandParser` processes user input according to the following rules:
* Strips surrounding whitespace and checks for an empty argument string, returning a targetless `HelpCommand()` (Tier 1: General Help).
* Normalizes casing using `Locale.ROOT` (`commandTarget.toLowerCase(Locale.ROOT)`) to ensure consistent parsing across system locales (avoiding locale-specific casing anomalies such as Turkish dotted 'i').
* Accepts only a single bare command word: rejects the deprecated `--` flag form (e.g., `help --add`, `help -- add`, `help --`) and multi-token arguments (e.g., `help add patient`) by throwing a `ParseException` with `HelpCommand.MESSAGE_INVALID_HELP_ARGUMENT`.
* Validates the target against `HelpCommand.COMMAND_USAGE_MAP` and `HelpCommand.TARGET_WINDOW`, rejecting unknown targets (e.g., `help 3`, `help nonexistent`) at parse time with the same `ParseException`.

##### 2. Dynamic Command Usage Mapping (`HelpCommand`)

Rather than hardcoding command names and usage strings inside `HelpCommand`:
* A static `Map<String, String> COMMAND_USAGE_MAP` is initialized using a `TreeMap` to sort available command words alphabetically.
* Each command registers its canonical `MESSAGE_USAGE` string dynamically (e.g., `AddCommand.COMMAND_WORD -> AddCommand.MESSAGE_USAGE`).
* `HelpCommand` registers itself in `COMMAND_USAGE_MAP` (`HelpCommand.COMMAND_WORD -> HelpCommand.MESSAGE_USAGE`), so `help help` displays the syntax of the `help` command, as specified in issue #102.
* The help window target (`"window"`) is deliberately decoupled from `COMMAND_USAGE_MAP`. It is handled as a distinct navigation branch returning `new CommandResult(SHOWING_HELP_MESSAGE, true, false)`.
* If a target matches a registered command, its usage string is returned. Unknown targets cannot reach `HelpCommand` because `HelpCommandParser` validates the target before constructing the command.

##### 3. Build-Time Bundling & Extraction

* Gradle's `processResources` task packages `docs/UserGuide.md` into the application JAR classpath under `/docs/UserGuide.md`, serving as the single offline source of truth.
* `HelpBookUtil` loads the document as an input stream via `Class#getResourceAsStream("/docs/UserGuide.md")`.

##### 4. Native UI Node Generation (`MarkdownRenderer`)

`MarkdownRenderer` parses the sanitized Markdown sequentially into native JavaFX nodes without external third-party dependencies:
* **Section Structure & Headings**: Tracks headers (`#`, `##`, `###`) and horizontal separators (`---`), rendering them as styled `Label` and `Separator` nodes, while resetting active list counters.
* **Offline Image Placeholders**: Image syntax (`![alt](src)`) cannot be bundled with the offline copy, so a standalone image line renders as a subdued placeholder note that keeps the alt text and directs the reader to the online User Guide via the window's **Copy URL** button. Inline occurrences are substituted with the bracket-free text `(image: alt)`, so they can neither form hyperlinks by themselves nor merge with a link that follows on the same line.
* **List Counter Tracking across Continuations**:
    * Tracks numbered items with monotonic counter progression (`1., 2., 3., ...`), supporting lazy markdown numbering where all raw items start with `1.`.
    * Secondary continuation notes (e.g., `**Mac users:**`, multi-line descriptions) and sub-bullet items (`*`, `-`) are left-padded (`28px`–`32px`) without resetting the parent numbered list counter.
    * Empty lines flush pending paragraphs but do not terminate loose list counting.
* **Callout & Icon Normalization**:
    * Collapses Jekyll/GitHub callout icon clusters (e.g., `[:exclamation: ... ] **Caution:**`) into unified badge components (`⚠ Caution:` in NUS Orange, `ℹ Note:` in Blue).
    * Wraps blockquote lines (`> ...`) in styled callout cards with an NUS Orange left border.
* **Table Layout**: Groups contiguous piped lines (`|`) into a JavaFX `GridPane` with fixed column constraints (28% label / 72% description) and dark theme styling.
* **Inline Markup & Hyperlinks**:
    * Uses regex pattern matching to format bold text (`**`), inline code (`` ` ``), and keyboard keys (`<kbd>`).
    * Parses links `[text](url)` so that clicking an external URL copies it to the system clipboard and toggles the link label to `(Copied!)` for 1.5 seconds via a JavaFX `PauseTransition`, eliminating OS browser dependencies in headless testing environments.
* **HTML `<br>` Sanitization**: Trailing `<br>` tags are stripped to eliminate excessive vertical padding, while consecutive internal `<br>` breaks are collapsed to a single `\n`.

#### Design Considerations

**Aspect: How command usages are maintained in `HelpCommand`**

* **Alternative 1 (Current choice): Dynamic registry referencing static `COMMAND_WORD` and `MESSAGE_USAGE` from individual command classes.**
    * *Pros:* Single source of truth. Modifying a command's syntax in its respective class automatically updates `help [COMMAND]` without code duplication.
    * *Cons:* Requires `HelpCommand` to reference all concrete command classes.
* **Alternative 2: Storing all usage strings inside `HelpCommand`.**
    * *Pros:* All help-related strings are located in one file.
    * *Cons:* High maintenance overhead; updating a command's parameters requires synchronizing two separate classes, risking documentation drift.

**Aspect: How the offline User Guide is rendered**

* **Alternative 1 (Current choice): Native custom Markdown renderer using standard JavaFX nodes (`TextFlow`, `VBox`, `ScrollPane`, `GridPane`).**
    * *Pros:* Fully compliant with course constraints (zero external libraries, no `javafx-web`/`WebView`), runs reliably across platforms and headless CI test environments, and natively adopts application styling.
    * *Cons:* Requires maintaining internal parser logic for markdown constructs.
* **Alternative 2: JavaFX `WebView` component.**
    * *Pros:* Renders full HTML/Markdown with complete CSS out of the box.
    * *Cons:* Requires native WebKit binaries not bundled across all platforms, violates course external dependency rules, and is prone to crashes in headless test runners.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedLaiNurse`. It extends `LaiNurse` with an undo/redo
history, stored internally as a `laiNurseStateList` and `currentStatePointer`. Additionally, it implements the
following operations:

* `VersionedLaiNurse#commit()` — Saves the current LaiNurse state in its history.
* `VersionedLaiNurse#undo()` — Restores the previous LaiNurse state from its history.
* `VersionedLaiNurse#redo()` — Restores a previously undone LaiNurse state from its history.

These operations are exposed in the `Model` interface as `Model#commitLaiNurse()`, `Model#undoLaiNurse()` and
`Model#redoLaiNurse()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedLaiNurse` will be initialized with the
initial LaiNurse state, and the `currentStatePointer` pointing to that single LaiNurse state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in LaiNurse. The `delete` command calls
`Model#commitLaiNurse()`, causing the modified state of LaiNurse after the `delete 5` command executes to be
saved in the `laiNurseStateList`, and the `currentStatePointer` is shifted to the newly inserted LaiNurse state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add --id T0123456B --name David …​` to add a new person. The `add` command also calls
`Model#commitLaiNurse()`, causing another modified LaiNurse state to be saved into the `laiNurseStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitLaiNurse()`, so the LaiNurse state will not be saved into the `laiNurseStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the
`undo` command. The `undo` command will call `Model#undoLaiNurse()`, which will shift the `currentStatePointer` once
to the left, pointing it to the previous LaiNurse state, and restores LaiNurse to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial LaiNurse state, then there are no previous LaiNurse states to restore. The `undo` command uses `Model#canUndoLaiNurse()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoLaiNurse()`, which shifts the `currentStatePointer` once
to the right, pointing to the previously undone state, and restores LaiNurse to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `laiNurseStateList.size() - 1`, pointing to the latest LaiNurse state, then there are no undone LaiNurse states to restore. The `redo` command uses `Model#canRedoLaiNurse()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify LaiNurse, such as
`list`, will usually not call `Model#commitLaiNurse()`, `Model#undoLaiNurse()` or `Model#redoLaiNurse()`. Thus,
the `laiNurseStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitLaiNurse()`. Since the `currentStatePointer` is not
pointing at the end of the `laiNurseStateList`, all LaiNurse states after the `currentStatePointer` will be
purged. Reason: It no longer makes sense to redo the `add --id T0123456B --name David …​` command. This is the behavior that most modern
desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire LaiNurse state.
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
| `* * *`  | home-based nurse | add a new patient's ID, name, phone number and address                                               | I have their contact details in one place                    |
| `* * *`  | home-based nurse | see all my patients in one list                                                                      | I can see everyone I care for at a glance                    |
| `* * *`  | home-based nurse | view all the details of a patient in one place                                                       | I can check their address and health details before a visit  |
| `* * *`  | home-based nurse | find patients by part of their name, ID, phone number, address or medical history                    | I can find a patient quickly without typing their full details |
| `* *`    | home-based nurse | see the patient list sorted by name                                                                  | I can find a patient quickly without scanning the whole list   |
| `* *`    | home-based nurse | see the patients that match the most search fields first                                             | the best matches are at the top of a long list                 |
| `* * *`  | home-based nurse | edit a patient's details                                                                             | their records stay correct when something changes            |
| `* * *`  | home-based nurse | delete a patient I no longer care for                                                                | my list shows only my current patients                       |
| `* * *`  | home-based nurse | add an appointment for a patient with a date and time                                                | I can plan my home visits in advance                         |
| `* * *`  | home-based nurse | reschedule an appointment in one command                                                            | I don't have to delete and re-add it                         |
| `* * *`  | home-based nurse | view patients with appointments on a specific date                                                   | I can plan my visits for that day                            |
| `* * *`  | home-based nurse | view patients with appointments over the next few days or weeks                                      | I know how busy I'll be and can plan ahead                   |
| `* * *`  | home-based nurse | record and view a patient's medical profile (conditions, allergies, medications, next-of-kin)        | I know their health details and whom to call in an emergency |
| `* * *`  | home-based nurse | quickly check the syntax and usage examples of a specific command via the CLI                       | I can recall command arguments without breaking my typing workflow |
| `* *`    | home-based nurse | record personal notes about a patient (routine, hobbies, pet peeves, past conversations)             | I can build rapport and recall what we've talked about       |
| `* *`    | home-based nurse | see patients with no upcoming appointment                                                            | no one gets missed                                           |
| `* *`    | home-based nurse | view one patient's appointments on a date or in a range                                              | I can prepare for that patient's visits                      |
| `*`      | home-based nurse | archive a discharged patient instead of deleting them                                                | they're hidden from my list but I keep their records         |

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

1.  Nurse requests to find patients using one or more substrings of their ID, name, phone number, address, medical history or notes
2.  LaiNurse shows a list of matching patients, with the patients that match the most fields first, and patients that match the same number of fields in name order

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

* 2a. The ID is missing or is not a valid ID.

    * 2a1. LaiNurse shows an error message with the command format.

      Use case resumes at step 2.

* 2b. The given ID does not match any patient.

    * 2b1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2c. No fields to edit are provided.

    * 2c1. LaiNurse shows an error message.

      Use case resumes at step 2.

* 2d. One or more fields are in an invalid format.

    * 2d1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 2.

* 2e. Nurse tries to change the patient's ID.

    * 2e1. LaiNurse does not accept an ID among the fields to change, and shows the command format.

      Use case resumes at step 2.

* 2f. An appointment to remove is not one of the patient's appointments.

    * 2f1. LaiNurse shows an error message naming the appointment, and makes no changes.

      Use case resumes at step 2.

* 2g. An appointment to add is one the patient already has.

    * 2g1. LaiNurse keeps a single copy of the appointment.

      Use case resumes at step 3.

**Use case: UC05 - View appointments**

**MSS**

1. Nurse requests to view appointments on a date or in a range, optionally for one patient by ID.
2. LaiNurse shows only the matching appointments, with the next appointment from now and its patient's details, and
   lists the matching patients.
3. Nurse requests to go back to the weekly view.
4. LaiNurse shows the week of the selected appointment, with that appointment still selected.

   Use case ends.

**Extensions**

* 1a. The nurse gives a date that is not a real date in `yyyy-MM-dd`.

    * 1a1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1b. The nurse gives no start date.

    * 1b1. LaiNurse uses today as the start date.

      Use case resumes at step 2.

* 1c. The start date is after the end date, or the nurse gives only an end date that is before today.

    * 1c1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1d. The nurse gives no end date.

    * 1d1. LaiNurse uses a range with no end, so that it covers every appointment on or after the start date.

      Use case resumes at step 2.

* 1e. The nurse gives no date and no ID.

    * 1e1. LaiNurse shows the weekly view of this week, with the next appointment from now selected, and lists the
      patients with an appointment this week.

      Use case ends.

* 1f. The nurse gives an ID and no date.

    * 1f1. LaiNurse shows every appointment of that patient, past and upcoming, on one page.

      Use case resumes at step 2.

* 1g. The patient ID the nurse gives is invalid or belongs to no patient.

    * 1g1. LaiNurse shows an error message, and the patient list and the appointment page stay as they were.

      Use case resumes at step 1.

* 2a. No appointment is in the range, or, with an ID given, the patient has none in it.

    * 2a1. LaiNurse shows that the range has no appointments, and indicates that there are no matching patients.

      Use case resumes at step 3.

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

**Use case: UC07 - Access in-app help**

**MSS**

1.  Nurse requests help for a specific command (e.g., `help add`).
2.  LaiNurse displays the parameter syntax, format constraints, and usage examples for that command in the result display.

    Use case ends.

**Extensions**

* 1a. Nurse requests a general overview of all commands (`help`).
    * 1a1. LaiNurse displays an alphabetical list of all available commands and general navigation guidance in the result display.
    * Use case ends.

* 1b. Nurse requests to open the full offline User Guide window (`help window`, press <kbd>F1</kbd>, or select **Help** > **Help**).
    * 1b1. LaiNurse opens the Help Window containing the complete offline User Guide rendered using native JavaFX components.
    * Use case ends.

* 1c. Nurse enters an unrecognized command target (e.g., `help nonexistent`) or the discontinued flag form (e.g., `help --add`).
    * 1c1. LaiNurse displays an invalid command format error that shows `HelpCommand.MESSAGE_USAGE` and lists all valid targets.
    * Use case ends.

* 1d. Nurse provides a malformed help argument (e.g., `help -- add`, `help --`, or multiple tokens such as `help add patient`).
    * 1d1. LaiNurse displays an invalid command format error displaying `HelpCommand.MESSAGE_USAGE`.
    * Use case ends.

* 1e. Nurse enters the target command with mixed or uppercase letters (e.g., `help VIEW`).
    * 1e1. LaiNurse normalizes the target using `Locale.ROOT` and displays the syntax details for the matching command.
    * Use case ends.

**Use case: UC08 - Edit an appointment**

**MSS**

1.  Nurse requests to edit a patient's appointment by the patient's ID and the appointment's date and time, with a
    new date and time, new details, or both
2.  LaiNurse updates the appointment
3.  LaiNurse shows the edited appointment

    Use case ends.

**Extensions**

* 1a. The ID or the appointment's date and time is missing, or nothing to change is given.

    * 1a1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1c. The patient has no appointment at the given date and time.

    * 1c1. LaiNurse shows an error message that lists the patient's appointments on that day, if any.

      Use case resumes at step 1.

* 1d. The new date and time is that of another of the patient's appointments.

    * 1d1. LaiNurse shows an error message, and makes no changes.

      Use case resumes at step 1.

* 1e. A date, time or the details are in an invalid format.

    * 1e1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

**Use case: UC09 - Add an appointment**

**MSS**

1.  Nurse requests to add an appointment to a patient's schedule by the patient's ID and the appointment's date and
    time, with details, if any
2.  LaiNurse adds the appointment to the patient's schedule
3.  LaiNurse shows the new appointment, selected in the appointments view

    Use case ends.

**Extensions**

* 1a. The ID or the appointment's date and time is missing, or in an invalid format.

    * 1a1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1c. The patient already has an appointment at the given date and time.

    * 1c1. LaiNurse shows an error message, and makes no changes.

      Use case resumes at step 1.

* 1d. The details are blank.

    * 1d1. LaiNurse shows an error message.

      Use case resumes at step 1.

**Use case: UC10 - Delete an appointment**

**MSS**

1.  Nurse requests to delete a patient's appointment by the patient's ID and the appointment's date and time
2.  LaiNurse removes the appointment from the patient's schedule
3.  LaiNurse shows the appointments view without the deleted appointment

    Use case ends.

**Extensions**

* 1a. The ID or the appointment's date and time is missing, or in an invalid format.

    * 1a1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1c. The patient has no appointment at the given date and time.

    * 1c1. LaiNurse shows an error message that lists the patient's appointments on that day, if any.

      Use case resumes at step 1.

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
* **Notes**: Optional free-form personal information about a patient that the other fields do not cover, such as their
  hobbies, pet peeves and daily routines (e.g., "walks every morning"). Health information goes in the Medical History
  instead.
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
       Expected: The **Patients** tab is selected. The panel beside the list shows the patient's name, ID, phone number, address, medical history and notes, then their appointments that have not passed, then up to ten that have, dimmed. Each appointment shows its details beside its time, or under it if the window is narrow. Their row in the list is highlighted. The status message names the patient.

    1. Test case: `find --name bernice`, then `view S1234567A`<br>
       Expected: The list shows all patients again, the row of the patient with the ID `S1234567A` is highlighted, and the panel shows their details.

    1. Test case: `view S1234567A`, then `delete S2345678B`, another patient<br>
       Expected: No patient is selected, and the panel goes back to its starting message, because a command that removes patients selects nothing.

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

    1. Test case: type `edit s23 --phone 91234567`, then press Tab<br>
       Expected: The command box shows `edit S2345678B --phone 91234567`. The options are kept.

    1. Test case: type `add --id S1`, `view XYZ`, `view S1 extra` or `edit --phone 91234567`, then press Tab<br>
       Expected: The command box does not change.

1. Moving between the command box and the patient list

    1. Prerequisites: Start with the sample data. Enter `find --address serangoon`, so that the list shows Bernice Yu
       and David Li.

    1. Test case: the result of the prerequisite<br>
       Expected: The header above the list shows `Filtered patients` and `2 of 6`. Bernice Yu, the first match, is
       selected, and the panel shows their details.

    1. Test case: type `abc` in the command box, then press Esc<br>
       Expected: The patient list gets a blue border and the command box is dimmed. `abc` stays in the command box.
       Bernice Yu is selected, and the panel shows their details.

    1. Test case: after the test case above, press Down<br>
       Expected: David Li is selected, and the panel shows their details. Up selects and shows Bernice Yu again.

    1. Test case: with Bernice Yu selected at the top of the list, press Up<br>
       Expected: The **Patients** tab gets a blue border. A second Up does nothing. Down moves the focus back to the
       list, with Bernice Yu still selected.

    1. Test case: after the test cases above, press Esc<br>
       Expected: The list shows all patients again, and the header shows `All patients` and `6 patients`. The status
       message does not change, as Esc runs no command. The patient shown in the panel stays selected, and the panel
       does not show another patient, even for a moment. The focus stays in the list.

    1. Test case: with the focus in the list, press Tab twice<br>
       Expected: The focus moves to the **Patients** tab, then back to the list. It never moves to the command box or to
       the patient details. Shift+Tab does nothing.

    1. Test case: make the window short enough that the details do not fit, then, with the focus in the list, press
       Shift+Down, then Shift+Up<br>
       Expected: The details scroll down, then back up. The selection in the list does not move.

    1. Test case: with the focus in the list, type `l`<br>
       Expected: The focus moves to the command box, which now shows `abcl`, with the caret at the end. Enter instead of
       `l` also moves the focus to the command box, without adding anything.

    1. Test case: clear the command box, then press Tab<br>
       Expected: Nothing happens, and the focus stays in the command box.

    1. Test case: `find --name zzz`, then press Esc<br>
       Expected: The list says `No patients match the filter.`, so the focus goes to the **Patients** tab instead. A
       second Esc shows all patients.

1. Going through command history with Up and Down

    1. Prerequisites: Start LaiNurse, then enter `list`, `find --name bernice` and `view S1234567A`.

    1. Test case: type `abc`, then press Up three times<br>
       Expected: The command box shows `view S1234567A`, then `find --name bernice`, then `list`. A fourth Up keeps
       `list`.

    1. Test case: after the test case above, press Down three times<br>
       Expected: The command box shows `find --name bernice`, then `view S1234567A`, then `abc` again.

### Deleting a patient

1. Deleting a patient

    1. Prerequisites: Start with the sample data, which includes a patient with the ID `S1234567A`.

    1. Test case: `delete S1234567A`<br>
       Expected: The patient with the ID `S1234567A` is deleted from the list. The status message shows the deleted patient's details. No patient is selected, and the panel shows its starting message.

    1. Test case: `delete S0000000Z`, where no patient has that ID<br>
       Expected: No patient is deleted. The status message shows an error.

    1. Other incorrect delete commands to try: `delete`, `delete S123-456` (not a valid ID)<br>
       Expected: No patient is deleted. The status message shows the correct command format.

1. _{ more test cases …​ }_

### Recording and viewing patient notes

1. Adding, editing and saving notes

    1. Test case: `add --id NOTES1 --name Mary Tan --phone 91234567 --address Blk 10 Clementi Ave 2 --notes Enjoys gardening`<br>
       Expected: The patient is added. The feedback includes `Notes: ENJOYS GARDENING`.

    1. Test case: `view NOTES1`, then `edit NOTES1 --notes Dislikes loud noises; walks every morning`<br>
       Expected: The panel shows the notes below medical history, then updates to `DISLIKES LOUD NOISES; WALKS EVERY MORNING`.

    1. Test case: `edit NOTES1 --phone 98765432`<br>
       Expected: The phone changes, and the notes are preserved.

    1. Test case: Close and restart LaiNurse, then enter `view NOTES1` and `find --notes "walks every morning"`.<br>
       Expected: The notes remain saved, and the search finds Mary Tan.

1. Rejecting invalid notes

    1. Test cases: `edit NOTES1 --notes`, `edit NOTES1 --notes --phone 11112222`, and
       `add --id NOTES2 --name Jane Tan --phone 91234567 --address Clementi --notes`<br>
       Expected: A blank-notes error is shown. No patient, displayed list or saved record changes.

    1. Test cases: `edit NOTES1 --notes First --notes Second`, and
       `add --id NOTES2 --name Jane Tan --phone 91234567 --address Clementi --notes First --notes Second`<br>
       Expected: A duplicate-option error is shown, and nothing changes.

    1. Enter `delete NOTES1`, then `list` after testing to restore the sample patients.

### Finding patients by substrings

1. Searching each patient field

    1. Prerequisites: Start with the unmodified sample data and enter `list`.

    1. Test case: `find --id 0123`<br>
       Expected: Only Charlotte Oliveiro is shown, because their ID contains `0123`.

    1. Test case: `find --name "lex"`<br>
       Expected: Only Alex Yeoh is shown. Partial names match, even though the previous search showed another patient.

    1. Test case: `find --address “  sErAnGoOn   gArDeNs ”`<br>
       Expected: Bernice Yu and David Li are shown. Matching ignores case and repeated whitespace, and accepts curly quotes.

    1. Test case: `find --phone 4388`<br>
       Expected: Only Alex Yeoh is shown, because their phone number contains `4388`.

    1. Test case: `find --medical-history penicillin`<br>
       Expected: Only Bernice Yu is shown. Patients without medical history do not match this criterion.

    1. Test case: `find --notes "has a dog"`<br>
       Expected: Only Bernice Yu is shown. Patients without notes do not match this criterion.

1. Combining substrings and restoring the list

    1. Test case: `find --name Alex --address "Serangoon Gardens"`<br>
       Expected: Alex Yeoh, Bernice Yu and David Li are shown, in name order, because each matches one field.

    1. Test case: `find --name Alex --id 1234567`<br>
       Expected: Alex Yeoh and Irfan Ibrahim are shown. Alex appears first and once, because Alex matches both criteria.

    1. Test case: `find --name Irfan --id 1234567`<br>
       Expected: Irfan Ibrahim appears before Alex Yeoh. Irfan matches both fields, while Alex matches only ID. Irfan
       Ibrahim, the first match, is selected, and the panel shows their details.

    1. Test case: `find --id 1234567`<br>
       Expected: Alex Yeoh appears before Irfan Ibrahim again, because both match one field and ties are in name order.

    1. Test case: `find --notes dog --name Charlotte`<br>
       Expected: Bernice Yu and Charlotte Oliveiro are shown, in name order. Either field may match.

    1. Test case: `find --address "Gardens Serangoon"`<br>
       Expected: An empty list is shown with a count of zero. Multiword values must match a contiguous substring.

    1. Test case: `list`<br>
       Expected: All six sample patients are shown again. Alex Yeoh, the first, is selected, and the panel shows their
       details.

1. Rejecting invalid commands

    1. Prerequisites: Enter `find --name Alex`, so only Alex Yeoh is shown.

    1. Test cases: `find`, `find Alex`, `find --name ""`, `find --name "Alex`, `find --name “Alex"`,
       `find --name "Alex" trailing`, `find --unknown Alex`, `find --name=Alex`, `find --notes`,
       `find --notes ""`, and `find --notes "dog`<br>
       Expected: The displayed list and patient records are unchanged. The status message shows the command usage instructions.

    1. Test case: `find --next-appointment 2026-10`<br>
       Expected: The same as above. `find` does not search appointments; use `viewappt` instead.

    1. Test case: `find --name Alex --name Bernice`<br>
       Expected: The displayed list and patient records are unchanged. The status message shows a duplicate-option error.

    1. Test case: `find --notes dog --notes morning`<br>
       Expected: The same duplicate-option error is shown, and nothing changes.

    1. Enter `list` after testing to show all patients again.

### Sorting by name

1. The order of the patient list

    1. Prerequisites: Start with the sample data, which is already in name order: Alex Yeoh, Bernice Yu,
       Charlotte Oliveiro, David Li, Irfan Ibrahim, Roy Balakrishnan.

    1. Test case: `list`<br>
       Expected: All six patients are shown, in the name order above.

    1. Test case: `add --id S0245678G --name Zara Lim --phone 98765432 --address "Blk 123 Clementi Ave 3, #05-12"`<br>
       Expected: The list shows all patients again, and Zara Lim is last, after Roy Balakrishnan.

    1. Test case: `edit S0245678G --name Aaron Tan`<br>
       Expected: Aaron Tan moves to the top of the list, above Alex Yeoh.

    1. Test case: `add --id S0356789H --name aaron tan --phone 91234567 --address "Blk 456 Bedok Ave 2"`<br>
       Expected: Two rows read `AARON TAN`, adjacent at the top: `S0245678G` above `S0356789H`, because the patient
       ID breaks the tie.

### Viewing patients by appointment date

1. Viewing patients who have an appointment on a given date

    1. Prerequisites: Start with the sample data, which includes patients with the appointments
       `2026-10-05 09:30` and `2026-11-02` (Alex Yeoh), `2026-10-06` (Bernice Yu) and `2026-10-07 14:00` (Irfan Ibrahim).
       If you have modified the data, delete the data file and relaunch the app to restore the sample data.

    1. Test case: `viewappt 2026-10-07`<br>
       Expected: The **Appointments** tab is selected. Above the list is `Wed 7 Oct 2026`, with no arrow buttons. The
       list shows only Irfan Ibrahim's appointment at 14:00, which is selected, and the
       panel beside the list shows their details with that appointment marked. The status message shows
       `1 patient(s) with appointments on 2026-10-07`. On the **Patients** tab, only Irfan Ibrahim is listed.

    1. Test case: `viewappt 2026-11-02`, the date of Alex Yeoh's second appointment<br>
       Expected: Only Alex Yeoh's appointment on Mon 2 Nov is shown, and it is selected. On the **Patients** tab, only
       Alex Yeoh is listed, because every appointment of each patient is checked.

    1. Test case: `viewappt 2026-11-11`, a date on which no patient has an appointment<br>
       Expected: The list says `No appointments in this range.`, and nothing is selected. The status message shows
       `0 patient(s) with appointments on 2026-11-11`. The patient list on the **Patients** tab is empty.

    1. Test case: `viewappt 2026-02-30`, an impossible date<br>
       Expected: The list is unchanged. The status message shows the date format error, as for
       `viewappt --from 2026-02-30`.

    1. Test case: `viewappt 15/10/2026`, a date in the wrong format<br>
       Expected: The list is unchanged. The status message shows the date format error, as above.

    1. Test case: `viewappt S1234567A`, a patient ID without `--id`<br>
       Expected: The list is unchanged. The status message shows the command format, because a word with letters could
       be a mistyped option.

    1. Test case: `viewappt`, with no date and no options<br>
       Expected: The weekly view of this week is shown, marked `This week`, with the arrow buttons above the list. Its
       next appointment from now is selected, or nothing if the week has none. The status message names this week's
       Monday and Sunday, in your computer's time zone, and the **Patients** tab lists the patients with an
       appointment this week. To see the appointments from today onwards instead, use `viewappt --from` with today's
       date.

    1. Other incorrect `viewappt` commands to try: `viewappt 2026-10-15 2026-10-20` (more than one date),
       `viewappt --from 2026-02-30` (an impossible date for an option)<br>
       Expected: The list is unchanged. The first shows the command format; the second shows the date format error.

1. Viewing patients who have an appointment in a date range

    1. Prerequisites: The same sample data as above.

    1. Test case: `viewappt --from 2026-10-01 --to 2026-10-31`<br>
       Expected: Above the list is `Thu 1 Oct – Sat 31 Oct 2026`. The list shows the three appointments on 5, 6 and
       7 Oct, each day under its own heading, with Alex Yeoh's at 09:30 on Mon 5 Oct selected. On the **Patients** tab,
       Alex Yeoh, Bernice Yu and Irfan Ibrahim are listed. The status message shows
       `3 patient(s) with appointments from 2026-10-01 to 2026-10-31`.

    1. Test case: `viewappt --to 2026-10-06 --from 2026-10-06`<br>
       Expected: Bernice Yu's appointment is selected, and only Bernice Yu is listed on the **Patients** tab. The
       options may be in any order, and a one-day range is the same as `viewappt 2026-10-06`, including the status
       message `1 patient(s) with appointments on 2026-10-06`.

    1. Test case: `viewappt --from 2026-10-07`<br>
       Expected: Above the list is `From Wed 7 Oct 2026`. The list shows Irfan Ibrahim's appointment and Alex Yeoh's
       on 2 Nov. The next appointment from now is selected: Alex Yeoh's on 2 Nov if today is on or before that date,
       because Irfan Ibrahim's has passed, and otherwise Irfan Ibrahim's, the first on the page. Both are listed on the
       **Patients** tab. The status message shows `2 patient(s) with appointments on or after 2026-10-07`.

    1. Test case: `viewappt --to 2099-12-31`<br>
       Expected: Only patients with an appointment from today onwards are listed on the **Patients** tab. The range in
       the status message starts with today's date, which is the current date in your computer's time zone. If no
       appointment is in the range, the appointment page says `No appointments in this range.`

    1. Test case: `viewappt --to 2000-01-01`<br>
       Expected: The list is unchanged. The status message says that the `--to` date is before today.

    1. Test case: `viewappt --from 2026-10-31 --to 2026-10-01`<br>
       Expected: The list is unchanged. The status message says that the `--from` date must not be after the `--to` date.

    1. Other incorrect commands to try: `viewappt 2026-10-15 --to 2026-10-31` (a date and an option),
       `viewappt --till 2026-10-31` (unknown option), `viewappt --from 2026-10-01 --from 2026-10-02` (repeated option)<br>
       Expected: The list is unchanged. The status message shows the command format or a duplicate-option error.

1. Viewing one patient's appointments with `--id`

    1. Prerequisites: The same sample data as above.

    1. Test case: `viewappt --id S1234567A`<br>
       Expected: Above the list is `S1234567A, Mon 5 Oct – Mon 2 Nov 2026`. The page shows both of Alex Yeoh's
       appointments, past and upcoming, and the next one from now is selected: the one on Mon 2 Nov if today is on or
       before that date, and otherwise the one on Mon 5 Oct. On the **Patients** tab, only Alex Yeoh is listed. The
       status message shows `2 appointment(s) of ALEX YEOH`.

    1. Test case: `viewappt --id S1234567A --from 2026-10-01 --to 2026-10-31`<br>
       Expected: Above the list is `S1234567A, Thu 1 Oct – Sat 31 Oct 2026`. The list shows only Alex Yeoh's
       appointment at 09:30 on Mon 5 Oct, which is selected. On the
       **Patients** tab, only Alex Yeoh is listed. The status message shows
       `1 appointment(s) of ALEX YEOH from 2026-10-01 to 2026-10-31`.

    1. Test case: `viewappt 2026-10-05 --id S1234567A`, a date before the option<br>
       Expected: The same page as `viewappt 2026-10-05` gives, but the status message says
       `1 appointment(s) of ALEX YEOH on 2026-10-05`. A date combines with `--id` when it comes before the options.

    1. Test case: `viewappt --id S1234567A --from 2026-11-01`<br>
       Expected: Only Alex Yeoh's appointment on Mon 2 Nov is shown, and the status message shows
       `1 appointment(s) of ALEX YEOH on or after 2026-11-01`.

    1. Test case: `viewappt --id s1234567a`, the ID in lower case<br>
       Expected: The same as `viewappt --id S1234567A` gives. IDs are case-insensitive.

    1. Test case: `viewappt --id S1234567A --from 2026-12-01`, a range without appointments<br>
       Expected: The list says `No appointments in this range.`, and nothing is selected. The status message shows
       `0 appointment(s) of ALEX YEOH on or after 2026-12-01`. The patient list on the **Patients** tab is empty.

    1. Test case: `viewappt --id S9999999Z`, an ID that no patient has<br>
       Expected: The list is unchanged. The status message shows `The patient ID provided is invalid.`

    1. Other incorrect commands to try: `viewappt --id S1234567A 2026-10-05` (a date after the option, which makes
       the ID invalid), `viewappt --id S1234567A --id S2345678B` (repeated option), `viewappt --id` (missing ID)<br>
       Expected: The list is unchanged. The status message shows the patient ID constraints or a duplicate-option
       error.

1. Viewing a date-only appointment next to a timed one on the same day

    1. Prerequisites: Start with the sample data, then add a patient, and give her a timed appointment on the same
       date as Bernice Yu's date-only one: `add --id T0456789D --name Eve Tan --phone 98765432 --address Blk 123
       Clementi Ave 3, #05-12`, then `addappt T0456789D --appt 2026-10-06 15:00`.

    1. Test case: `viewappt 2026-10-06`<br>
       Expected: Under `Tue 6 Oct`, Bernice Yu's appointment comes first and shows `‒‒:‒‒`, then Eve Tan's at 15:00.
       Bernice Yu's appointment is selected. Both are listed on the **Patients** tab. The status message shows
       `2 patient(s) with appointments on 2026-10-06`.

    1. Note: the patient list stays filtered after a `viewappt` command. After testing, enter `list` to show all
       patients again.

1. _{ more test cases …​ }_

### Switching views and browsing appointments

1. Browsing a `viewappt` page and the weekly view

    1. Prerequisites: Start with the sample data, which has appointments on Mon 5 Oct (Alex Yeoh, 09:30), Tue 6 Oct
       (Bernice Yu, no time) and Wed 7 Oct 2026 (Irfan Ibrahim, 14:00). Enter
       `viewappt --from 2026-10-01 --to 2026-10-31`.

    1. Test case: the result of the prerequisite<br>
       Expected: The **Appointments** tab is selected. The list shows the three appointments numbered 1 to 3, each under
       a heading for its day, and no heading for the days without appointments. Alex Yeoh's appointment is selected,
       and the panel shows their details with that appointment marked under **Past** or **Appointments**.

    1. Test case: press Esc, then Down<br>
       Expected: The list gets a blue border. Bernice Yu's appointment is selected, skipping the heading of Tue 6 Oct,
       and the panel shows their details. Up selects Alex Yeoh's appointment again.

    1. Test case: with the focus in the list, press Ctrl+Down, then Ctrl+Up (Cmd on macOS)<br>
       Expected: Ctrl+Down selects Irfan Ibrahim's appointment, the last on the page, and Ctrl+Up selects Alex Yeoh's,
       the first.

    1. Test case: with the focus in the list, press Left or Right<br>
       Expected: Nothing changes, because a `viewappt` page has no other weeks.

    1. Test case: with the focus in the list, press Esc<br>
       Expected: The weekly view of the selected appointment's week is shown, `Mon 5 – Sun 11 Oct 2026`, with the
       arrow buttons above the list. Alex Yeoh's appointment, selected by Ctrl+Up above, stays selected.

    1. Test case: press Esc again<br>
       Expected: Nothing changes, because the weekly view has no range to remove.

    1. Test case: press Right, then Left twice, then enter `viewappt`<br>
       Expected: Right shows the week of Mon 12 Oct, and selects its first appointment, or nothing in a week with no
       appointments. Left twice shows the week of Mon 28 Sep. `viewappt` goes back to this week, marked `This week`.

1. Switching views without a command

    1. Prerequisites: Start with the sample data, and enter `viewappt 2026-10-05`.

    1. Test case: press Esc, so that Alex Yeoh's appointment, the first on the page, is selected, then press Up<br>
       Expected: The **Appointments** tab gets a blue border.

    1. Test case: after the test case above, press Left<br>
       Expected: The **Patients** tab is selected. The list shows only Alex Yeoh, as `viewappt` left it, and the header
       above it shows `Filtered patients` and `1 of 6`. The status message does not change. Right shows the appointment
       view again, with the same page and selection as before.

    1. Test case: after the test cases above, press Down, then Up twice<br>
       Expected: Down moves the focus to the appointment list, with Alex Yeoh's appointment still selected. The first
       Up moves it back to the **Appointments** tab. The second Up does nothing, and the focus stays on the tab.

    1. Test case: with the focus on the **Appointments** tab, click the **Patients** tab, then the **Appointments** tab<br>
       Expected: The same as with Left and Right. The blue border moves to the tab that you click.

    1. Test case: type `abc` in the command box, then click the **Patients** tab<br>
       Expected: The view switches, and the focus stays in the command box, with `abc` still in it.

1. Resizing the window

    1. Prerequisites: Start with the sample data, and enter `view S1234567A`.

    1. Test case: drag an edge of the window to make it as narrow as it goes, then wide<br>
       Expected: The patient list and the details beside it shrink and grow together. Up to a window about 1,000
       pixels wide, each takes about half. In a wider window, the details take more, up to 72% when the window is
       1,920 pixels wide. At the narrowest, nothing is cut off at the sides of the window, and neither the list nor the
       details scroll sideways. In the details, long values such as Alex Yeoh's address wrap without breaking a word.
       Beside Mon 2 Nov, `No time specified` wraps onto two lines, with the appointment's details,
       `MONTHLY BLOOD TEST`, under it. In a wide window, the details of each appointment move beside its time.

    1. Test case: `viewappt --from 2026-10-01 --to 2026-10-31`, then make the window as narrow as it goes<br>
       Expected: Above the list, `Thu 1 Oct – Sat 31 Oct 2026` stays whole. If the header runs out of room, the count
       beside it is cut short instead. Widening the window shows it in full.

1. Appointments that have passed today

    1. Prerequisites: Do these test cases before 23:55. Start with the sample data, and replace `TODAY` below with
       today's date, such as `2026-10-17`. Add a patient with no appointments:
       `add --id T0567890E --name Fay Ong --phone 91234567 --address Blk 1 Bedok North Ave 1`, then give her three
       appointments today: `addappt T0567890E --appt TODAY 00:00`, then `addappt T0567890E --appt TODAY`, and
       `addappt T0567890E --appt TODAY 23:59`.

    1. Test case: the result of the prerequisite<br>
       Expected: On the **Patients** tab, Fay Ong's row shows `Today` in orange as their next appointment: the one with
       no time, because the one at 00:00 has passed.

    1. Test case: `view T0567890E`<br>
       Expected: Under **Appointments**, the panel lists today's appointment with `No time specified`, then the one at
       23:59, both in orange. Under **Past**, it lists the one at 00:00, dimmed.

    1. Test case: click the **Appointments** tab, then press Esc<br>
       Expected: The weekly view shows this week. Under today's heading, in orange, the appointment at 00:00 comes
       first and is greyed out. Then comes the one with no time, showing `‒‒:‒‒`, and then the one at 23:59. The one
       with no time is selected, as the first appointment that has not passed, and the panel shows Fay Ong's details
       with it marked.

    1. Test case: `addappt T0567890E --appt TODAY HH:MM`, where `HH:MM` is two minutes from now, then
       `view T0567890E`. Wait until the minute after `HH:MM`, without entering a command.<br>
       Expected: At first, the panel lists the new appointment under **Appointments**, in orange. Within a few seconds
       of the minute after `HH:MM`, when the appointment has passed, it moves under **Past**, dimmed. On the
       **Appointments** tab, it is greyed out.

1. Commands that keep the view

    1. Prerequisites: Start with the sample data, and enter `viewappt 2026-10-05`.

    1. Test case: `delete S1234567A`<br>
       Expected: The **Appointments** tab stays selected. Alex Yeoh's appointment disappears from the page, which then
       says `No appointments in this range.` Nothing is selected, and the panel shows its starting message.

    1. Test case: `list`<br>
       Expected: The **Patients** tab is selected, and all patients are listed. Bernice Yu, now the first, is selected.

### Adding a patient

1. Adding a patient while the list is filtered

    1. Prerequisites: Start with the sample data. No patient has the ID `T0123456B`.

    1. Test case: `viewappt 2026-10-06`, then `find --name bernice`, then
       `add --id T0123456B --name John Doe --phone 98765432 --address John street, block 123, #01-01`<br>
       Expected: The **Patients** tab is selected, and the list shows all patients again. John Doe's row is
       highlighted, and the panel beside the list shows their details. The status message names the new patient.

    1. Test case: the same `add` command again<br>
       Expected: No patient is added. The status message shows `This patient already exists in LaiNurse.` The
       selection does not change.

### Editing a patient

1. Editing a patient's details by ID

    1. Prerequisites: Start with the sample data. Alex Yeoh has the ID `S1234567A`.

    1. Test case: `edit S1234567A --phone 91234567`<br>
       Expected: Alex Yeoh's phone number changes to `91234567`. The other details are unchanged.

    1. Test case: `viewappt 2026-10-06`, then `find --name bernice`, then `edit S1234567A --phone 91234567`<br>
       Expected: The **Patients** tab is selected, and the list shows all patients again. Alex Yeoh's row is
       highlighted, and the panel beside the list shows their details.

    1. Test case: `edit S0000000Z --phone 91234567`, an ID that no patient has<br>
       Expected: No patient is changed. The status message shows `The patient ID provided is invalid.`

    1. Test case: `edit S1234567A`, with nothing to change<br>
       Expected: No patient is changed. The status message shows `At least one field to edit must be provided.`

    1. Test case: `edit --phone 91234567`, with no ID<br>
       Expected: No patient is changed. The status message shows the command format.

1. Appointments alongside edits

    1. Prerequisites: Start with the sample data. Alex Yeoh has the appointments `2026-10-05 09:30`, with the details
       `INSULIN REVIEW`, and `2026-11-02`.

    1. Test case: `edit S1234567A --phone 91234567`<br>
       Expected: Only the phone number changes. Both appointments keep their date and time, and the 5 October
       appointment keeps its details.

### Editing an appointment

1. Moving an appointment and setting its details

    1. Prerequisites: Start with the sample data. Alex Yeoh (`S1234567A`) has the appointments `2026-10-05 09:30`, with
       the details `INSULIN REVIEW`, and `2026-11-02`, with the details `MONTHLY BLOOD TEST`.

    1. Test case: `editappt S1234567A --appt 2026-10-05 09:30 --to 2026-10-06 10:00`<br>
       Expected: The **Appointments** tab is selected, showing the week of 6 October, with Alex Yeoh's appointment at
       10:00 selected and still showing `INSULIN REVIEW`. The status message ends with
       `(moved from 2026-10-05 09:30)`.

    1. Test case: `editappt S1234567A --appt 2026-11-02 --details Fasting blood test`<br>
       Expected: The 2 November appointment keeps its date, and its details become `FASTING BLOOD TEST`.

1. Editing an appointment that cannot be edited

    1. Prerequisites: Start with the sample data.

    1. Test case: `editappt S1234567A --appt 2026-10-05 --to 2026-10-06`, a date alone for a timed appointment<br>
       Expected: No appointment is changed. The status message says that the patient has no appointment on
       `2026-10-05`, and lists `2026-10-05 09:30` as their appointment that day.

    1. Test case: `editappt S1234567A --appt 2026-10-05 09:30 --to 2026-11-02`<br>
       Expected: No appointment is changed. The status message says that the patient already has an appointment on
       `2026-11-02`.

    1. Test case: `editappt S1234567A --appt 2026-10-05 09:30`, with nothing to change<br>
       Expected: No appointment is changed. The status message says that `--to` or `--details` must be given.

### Adding an appointment

1. Adding an appointment

    1. Prerequisites: Start with the sample data. Bernice Yu (`S2345678B`) has no appointment on 14 October.

    1. Test case: `addappt S2345678B --appt 2026-10-14 --details Check blood pressure`<br>
       Expected: The **Appointments** tab is selected, showing the week of 14 October, with Bernice Yu's appointment
       selected and showing `CHECK BLOOD PRESSURE`. The list shows the appointment with no time.

    1. Test case: `addappt S1234567A --appt 2026-12-01 10:00`<br>
       Expected: The week of 1 December is shown, with Alex Yeoh's appointment at 10:00 selected.

1. Adding an appointment that cannot be added

    1. Prerequisites: Start with the sample data.

    1. Test case: `addappt S1234567A --appt 2026-10-05 09:30`<br>
       Expected: No appointment is added. The status message says that the patient `S1234567A` already has an
       appointment on `2026-10-05 09:30`.

    1. Test case: `addappt S0000000Z --appt 2026-10-14`<br>
       Expected: No appointment is added. The status message shows `The patient ID provided is invalid.`

### Deleting an appointment

1. Deleting an appointment by its date and time

    1. Prerequisites: Start with the sample data. Alex Yeoh (`S1234567A`) has the appointments `2026-10-05 09:30` and
       `2026-11-02`.

    1. Test case: `deleteappt S1234567A --appt 2026-10-05 09:30`<br>
       Expected: The appointment is deleted. The status message names the patient's ID and the deleted appointment with
       its details. The **Appointments** tab is selected, on the page that was shown, and the appointment is gone from it.

    1. Test case: `deleteappt S1234567A --appt 2026-11-02`<br>
       Expected: The date-only appointment on 2 November is deleted, as above.

1. Deleting an appointment that cannot be deleted

    1. Prerequisites: Start with the sample data.

    1. Test case: `deleteappt S1234567A --appt 2026-10-05`<br>
       Expected: No appointment is deleted. The status message says that the patient `S1234567A` has no appointment on
       `2026-10-05`, and lists `2026-10-05 09:30` as their appointment that day.

    1. Test case: `deleteappt S0000000Z --appt 2026-10-14`<br>
       Expected: No appointment is deleted. The status message shows `The patient ID provided is invalid.`

    1. Other incorrect deleteappt commands to try: `deleteappt` (missing everything), `deleteappt S1234567A`
       (nothing to delete), `deleteappt S1234567A --appt` (no date and time)<br>
       Expected: No appointment is deleted. The status message shows the correct command format.

1. Deleting an appointment of a patient hidden by a filter

    1. Prerequisites: Start with the sample data. Run `find --name bernice`, so only Bernice Yu is shown.

    1. Test case: `deleteappt S1234567A --appt 2026-10-05 09:30`<br>
       Expected: The appointment of Alex Yeoh, who is not in the list shown, is deleted. The find filter stays, so the
       patient list still shows only Bernice Yu.

    1. Test case: run `viewappt 2026-10-07` first, then `deleteappt G1234567E --appt 2026-10-07 14:00`<br>
       Expected: The appointments view keeps showing 7 October, now without Irfan Ibrahim's appointment.

1. _{ more test cases …​ }_

### Viewing help and command instructions

1. Command overview via CLI
    * Test case: `help`
        * Expected: An alphabetical summary of all available commands (`add`, `addappt`, `clear`, `delete`, `deleteappt`, `edit`, `editappt`, `exit`, `find`, `help`, `list`, `view`, `viewappt`) is displayed in the result box.

2. Targeted command help
    * Test case: `help add`
        * Expected: Parameter syntax and usage examples for `add` are displayed.
    * Test case: `help help`
        * Expected: The syntax and example of the `help` command itself is displayed.
    * Test case: `help viewappt`
        * Expected: The `viewappt` syntax is displayed: a date, a `--from`/`--to` range, or neither for this week, each
          with an optional `--id ID`. With `--id ID` alone, it shows all of that patient's appointments.
    * Test case: `help DELETE` or `help VIEW`
        * Expected: Case-insensitive parsing via `Locale.ROOT`; displays usage for `delete` and `view` respectively.

3. Rejecting invalid help inputs
    * Test case: `help nonexistent`
        * Expected: Displays `Invalid command format!`, the usage of `help`, and the list of valid targets.
    * Test case: `help --add` or `help --window`
        * Expected: The discontinued `--` flag form is rejected; displays the same invalid command format error.
    * Test case: `help -- add` or `help --`
        * Expected: Displays the same invalid command format error.
    * Test case: `help add patient`
        * Expected: Multi-token arguments are rejected; displays invalid command format error.

4. Opening the full User Guide window
    * Test case: `help window`
        * Expected: The Help Window opens displaying the full bundled User Guide formatted with native JavaFX nodes. The status message confirms: `Opened help window.`.
    * Test case: Press <kbd>F1</kbd> or click **Help** > **Help** in the menu bar.
        * Expected: Opens the Help Window.
    * Test case: Inside the Help Window, click any inline link or the **Copy URL** button.
        * Expected: The target URL is copied to the system clipboard, and the label changes to indicate `(Copied!)` temporarily.

### Saving data

1. Dealing with missing/corrupted data files

    1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
