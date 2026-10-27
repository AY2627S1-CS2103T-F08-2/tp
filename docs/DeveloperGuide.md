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

* AI Use Declaration from Yuhao: "I used Claude Code to write code, tests and documentation, and I review and test the results. AI levels between AI-5 and AI-6"

* AI Use Declaration from Yong Kang: "I used Gemini to write and review code, tests and documentation. No automation is done and I go back and forth consistently with AI to ensure I am still largely in charge of the process. AI levels between AI-1 and AI-5."

* AI Use Declaration from Darren: "I used Claude Code to write code, tests and documentation, and I review and test the results. The level ranges between AI-2 and AI-6" 

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

* A patient's row filters out done visits, then shows their first appointment that has not passed, from
  `AppointmentFormatter#findFirstNotPassed`. Completion does not affect the other date-based rules below.
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
end the command. `edit`, `addappt`, `editappt`, `deleteappt`, `markappt` and `unmarkappt` take the ID first, so
`IdCompleter` completes the word after the command word and keeps later options or appointment numbers.
`CommandBox` also keeps a `CommandHistory` of the commands entered since the app
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
`Storage.java`](https://github.com/AY2627S1-CS2103T-F08-2/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,

* can save both LaiNurse data and user preference data in JSON format, and read them back into corresponding
  objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonLaiNurseStorage` and
  `JsonUserPrefsStorage` (one class per data file).
* keeps a backup of the LaiNurse data file: `JsonLaiNurseStorage` writes every save to `data/lainurse.json` and then to
  `data/lainurse.backup.json`, and can copy a data file that cannot be loaded to `data/lainurse.corrupt.json`.
* decides, through `LaiNurseLoader`, which data the app starts with: that in the data file, the sample data, or that
  in the backup (see [Backing up the data file](#backing-up-the-data-file)).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects
  that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

Every date and time in the app goes through `DateUtil`, which accepts a date only in `yyyy-MM-dd` with the year exactly four digits and no sign, and a time only in `HH:mm`; `0000-01-01` stays valid, as ISO-8601 allows it, while `+12345-01-01`, `-2026-10-05` and `9:00` are rejected. A rejected date's exception text names the part whose shape is wrong, the year, the month or the day.

### Finding patients by substrings

`FindCommandParser` recognizes the six patient-detail options defined in `CliSyntax`: ID, name, phone number, address,
medical history and notes. It reuses `ArgumentTokenizer.normalizeOptionSeparators`, as `add` and `edit` do,
then uses `ArgumentTokenizer` and `ArgumentMultimap` to extract values and reject
duplicate options. `LaiNurseParser` translates every horizontal whitespace character, such as the non-breaking space
pasted from formatted text, to a plain space before the command word is matched, so every command accepts them around
options, IDs and values. At least one option is required, and every supplied value must be non-blank. Matching straight or
curly double quotes surrounding a complete value are removed. Unknown options, unexpected preamble text, and malformed
quotes cause a `ParseException` before the displayed list is changed. Words beginning with `--` remain reserved for
options, including inside quoted values.

`PatientContainsSubstringsPredicate` stores six optional string criteria normalized by `TextNormalizer`, which like the
command parser replaces horizontal spaces such as the non-breaking space with plain spaces before collapsing repeated
whitespace. Each criterion
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

**Identity and order.** An `Appointment` has a patient-local immutable positive number, a date, optional time
and optional details, plus a manually set completion boolean. Existing date/time constructors create unnumbered,
undone inputs. `AppointmentSchedule` assigns
missing numbers before exposing its immutable list. `isSameAppointment` compares assigned numbers; `equals` and
`hashCode` also compare date/time, details and completion. `hasSameDateTime` handles timestamp matching independently
of identity and completion. `isDone()` reads completion; `withDone(boolean)` copies the appointment with a new status,
preserving every other value. Assigning a number also preserves completion. Ordering and `toString()` remain date-based.
`PatientAppointment#isSameEntry` combines patient ID and appointment number, so moving a visit preserves selection.
The entry constructor verifies that the patient owns that exact numbered appointment.

Schedules sort by date, optional time (absent first), then number. The global appointment list retains date/time,
patient name and ID ordering, then uses number to distinguish a patient's simultaneous visits. Explicit duplicate
numbers are rejected before sorting; timestamp duplicates are preserved. `addappt` assigns the highest remaining
number plus one, starting at 1. Deletion leaves gaps and does not reserve deleted numbers; reaching the maximum
integer number produces a `CommandException` before mutation.

**Storage and compatibility.** `JsonAdaptedAppointment` saves `number`, `dateTime`, boolean `done` and optional
`details`, such as `{"number": 2, "dateTime": "2026-10-05 09:30", "done": false, "details": "INSULIN REVIEW"}`.
It accepts legacy strings and objects
without numbers. Missing numbers are assigned in chronological order, preserving input order for equal timestamps,
using the lowest unused positive numbers while retaining every explicit number. A local JSON-node creator rejects
non-integer, null, non-positive and out-of-range number values without changing global Jackson settings.
Missing `done` fields default to false. An explicit `done` must be a JSON boolean: null, strings, numbers and
other JSON types produce an `IllegalValueException`, without coercion. Both statuses persist across restarts.
`JsonAdaptedPatient` translates duplicate-number validation into the existing `IllegalValueException` loading path.
Migration does not save immediately; the normal save after a successful command persists the assigned numbers.
`JsonSerializableLaiNurse` continues to require the `patients` key.

An appointment date is written `yyyy-MM-dd`, optionally followed by a time in `HH:mm`, with the year exactly
four digits and no sign. A data file without the `patients` key, such as one from v1.3 that saved them under
`persons`, fails to load, and LaiNurse starts with an empty list. A data file from an earlier version with a
signed or wider year, such as `+12345-01-01` or `-2026-10-05`, also fails to load.

**Clashes.** `AppointmentClashes.find` compares exact date and optional time over the unfiltered appointment list,
excluding the target's patient ID and number and every done visit. A done target has no clashes. Two date-only
undone visits on one day clash; a date-only visit and a
timed visit do not. Both commands and UI use this query. Clashes never reject commands or data files, and are not
stored as flags. Add/edit/unmark success messages include every conflicting patient and appointment number.

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
in a narrow panel. `AppointmentRow` shows its Done/Undone status under its patient and its details below that,
only when it has some. The detail panel shows the same status under the appointment's date.

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

`editappt ID NUMBER` selects a visit by patient-local number. Both the edit and delete parsers require the ID and
number before any options and retain the existing option normalization and duplicate option checks.
`ParserUtil.parseAppointmentNumber` rejects signs, fractions and integer overflow. `AppointmentLookup.Target`
holds a positive number; the shared lookup uses `findByNumber` and reports a missing number before any mutation.
An assertion checks that the resolved appointment has the requested number.

An edit builds the replacement with the old appointment number and completion status, and preserves date/time
or details not supplied.
It removes the old identity, adds the replacement and uses `Model#setPatient()` and `Navigation.toAppointment`.
Moving onto another visit is permitted. After updating the model, `Messages.formatClashWarning` describes all
other undone visits at the resulting time when the edited appointment is undone, including on details-only edits.

The activity diagram below shows the checks that `editappt` makes, in order. Every error leaves the patient and the
data file unchanged.

<img src="images/EditAppointmentActivityDiagram.png" width="750" />

`addappt ID DATE_OR_DATETIME [--details DETAILS]` takes the appointment date after the ID and before optional
details. Its parser reuses `ArgumentTokenizer` and `ParserUtil.parseAppointment`, preserving strict date validation
and whitespace normalization, and rejecting duplicate details options. It logs parsing success or a missing
positional argument at `FINE` level without logging appointment details. It finds the patient through
`Model#findPatientById()`,
assigns `schedule.getNextNumber()`, adds the numbered appointment, and replaces the patient. The result names its
number and navigates to that exact stored appointment, followed by any clash warning. The existing patient filter
is retained, including when the patient or a clashing visit is hidden.

### Deleting an appointment

`deleteappt ID NUMBER` removes exactly one numbered visit. `AppointmentLookup` reports a missing number before
any change. The command replaces the patient with the selected identity removed; every other number and every
other visit's details remain intact, even when visits share a timestamp. An assertion checks that the removed
number is absent from the resulting schedule.

Unlike `addappt` and `editappt`, the command clears the selection with `CommandResult#withSelectionCleared`: after
it succeeds, no appointment exists to select, and the deleted one cannot be highlighted again. The returned result
has no `Navigation`, while `getView()` still shows the appointments view, so `MainWindow` switches to that view with
the selection cleared, and `AppointmentListPanel` keeps the page that was shown; the deleted row disappears from
it. `Model#setPatient()` does not change the filter
of the patient list, so a patient hidden by `find` or `viewappt` stays hidden there, as with `addappt`.

### Marking and unmarking appointments

`markappt ID NUMBER` sets an appointment to done; `unmarkappt ID NUMBER` sets it back to undone. Each has its own
command and parser. The parsers require exactly two positional arguments, reuse `ParserUtil.parseId` and
`parseAppointmentNumber`, and reject date/time selectors and extra arguments. Both commands are registered in
`LaiNurseParser`, `HelpCommand` and `IdCompleter`.

The package-private `AppointmentStatusCommand` base shares execution. It finds the patient in the full model and
resolves the number through `AppointmentLookup.Target`. A missing patient, missing appointment or repeated status
throws a `CommandException` before any mutation. The base uses `withDone`, replaces the appointment in the immutable
schedule and calls `Model#setPatient`, preserving the patient filter. Assertions check that identity, date/time and
details survive; `FINE` logging records rejection or success without appointment details.

The result navigates to the updated appointment using the same page-preservation behavior as `editappt`. Unmarking
appends `Messages.formatClashWarning` after the update. The normal `LogicManager` save persists completion. Existing
model listeners redraw status labels, next-appointment summaries and clashes, including conflicts with hidden
patients. Done visits remain visible and keep their date-based grouping, counts, ordering and automatic selection.
Both commands allow any date; editing preserves completion until an explicit status command changes it.

<img src="images/MarkUnmarkAppointmentActivityDiagram.png" width="450" />

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
`--to` date. A second date typed into one option's value, such as `--from 2026-10-01 2026-10-05`, gets a
message that each option takes one date, rather than the date error.

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
none. A date typed after the options ends up inside the ID value; when the value then ends in a real date, which
its punctuation keeps from being a patient ID, the parser reports that a `DATE` must come before other options,
rather than failing the ID. A value that ends in anything else, such as a digit-only ID typed with a stray space
(`--id 123 456`), fails as an ID. When the command runs, it looks the patient up by ID,
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
| `addappt`, `editappt`, `markappt`, `unmarkappt` | `Navigation.toAppointment` of the changed appointment, with no range, so the page shown stays if it has the appointment |
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

### Backing up the data file

`JsonLaiNurseStorage` keeps three files in the folder of the data file, all named after it by
`JsonLaiNurseStorage#withSuffix`:

* the data file, `lainurse.json`, which the app loads and saves as before.
* the backup, `lainurse.backup.json`. `JsonLaiNurseStorage#saveLaiNurse(ReadOnlyLaiNurse)` writes the data file and then
  the backup from the same data, so after every save that succeeds both hold the data as it was last saved. The
  overload that takes a path writes only that file, so tests and other callers can still write one file.
* the corrupt copy, `lainurse.corrupt.json`. `JsonLaiNurseStorage#keepCorruptLaiNurseFile()` copies the data file there,
  replacing any earlier copy.

`LogicManager` saves after every command that succeeds, as before, so it writes the backup without knowing about it.

At launch, `MainApp#initModelManager` asks `LaiNurseLoader#load(Storage)` for the data to start with.
`LaiNurseLoader` returns a `LaiNurseLoader.Result`: the data, and a message for the user if the data did not come from
the data file as usual.

| Data file | Backup | Data | Message |
|---|---|---|---|
| Loads | Any | The data file's | None |
| Missing | Any | The sample data | None |
| Cannot be loaded | Loads | The backup's | `MESSAGE_BACKUP_LOADED` |
| Cannot be loaded | Missing | None | `MESSAGE_BACKUP_MISSING` |
| Cannot be loaded | Cannot be loaded | None | `MESSAGE_BACKUP_NOT_LOADED` |

Whenever the data file cannot be loaded, `LaiNurseLoader` calls `Storage#keepCorruptLaiNurseFile()` before it reads the
backup, and adds `MESSAGE_CORRUPT_FILE_KEPT`, or `MESSAGE_CORRUPT_FILE_NOT_KEPT` if the copy failed, to the message.
Without the copy, the next save would overwrite the file, and the user's edits in it would be lost. `MainApp` passes
the message to `UiManager`, which shows it in the result display through `MainWindow#showFeedback` once the window
opens. Before this, a data file that could not be loaded was only reported in the log.

The order of the two writes matters. The backup is written only after the data file has been, so if the app stops
while writing the data file, the backup still holds the data saved before, and the next launch loads it.

**Aspect: When to fall back to the backup**

* **Alternative 1 (Current choice): Only when the data file cannot be loaded.**
    * *Pros:* A missing data file still loads the sample data, so deleting the file stays the way to start again,
      as the User Guide says.
    * *Cons:* If the user deletes the data file by mistake, the next save replaces the backup with the sample data.
* **Alternative 2: Also when the data file is missing.**
    * *Pros:* Recovers from a data file deleted by mistake.
    * *Cons:* Deleting the data file no longer resets the app: the user would have to delete both files.

**Aspect: How to restore the backup**

* **Alternative 1 (Current choice): Automatically at launch, with no command.**
    * *Pros:* Covers the common case, a data file broken by an edit, with no new command to learn, parse and test.
    * *Cons:* The user restores the corrupt copy by hand, by copying it over the data file with the app closed.
* **Alternative 2: A `restore` command.**
    * *Pros:* The user decides when to restore.
    * *Cons:* The app would need to stop saving until the user decides, and a new command adds risk this close to the
      feature freeze.

**Aspect: Protecting the backup from edits**

* **Alternative 1 (Current choice): Do not protect it; the User Guide says not to edit it.**
    * *Pros:* Works the same on every OS, and does not change OS settings, as the course's constraints require.
    * *Cons:* A user can still edit the backup. If both files then cannot be loaded, the app starts with no data,
      and only the corrupt copy of the data file is kept.
* **Alternative 2: Make the backup hidden or read-only.**
    * *Pros:* Harder to edit by mistake.
    * *Cons:* How to do so depends on the OS, and a read-only file must be made writable again before each save.

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
| `* * *`  | home-based nurse | mark an appointment as done by patient ID and appointment number                                      | I can record completed visits                               |
| `* * *`  | home-based nurse | unmark a done appointment                                                                            | I can correct its completion status                         |
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

1.  Nurse requests to edit a patient's appointment by the patient's ID and the appointment's number, with a
    new date and time, new details, or both
2.  LaiNurse updates the appointment
3.  LaiNurse shows the edited appointment

    Use case ends.

**Extensions**

* 1a. The ID or appointment number is missing or invalid, or nothing to change is given.

    * 1a1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1c. The patient has no appointment with the given number.

    * 1c1. LaiNurse shows an error naming the missing appointment number.

      Use case resumes at step 1.

* 2a. The edited appointment clashes with another visit, including to the same patient.

    * 2a1. LaiNurse keeps the edit and appends a warning naming the other patients and appointment numbers.

      Use case resumes at step 3.

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

* 1a. The ID or appointment date is missing, or in an invalid format.

    * 1a1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 2a. Another undone appointment has the given date and time, including one of this patient's visits.

    * 2a1. LaiNurse adds the appointment and appends a clash warning naming the other patients and visit numbers.

      Use case resumes at step 3.

* 1d. The details are blank.

    * 1d1. LaiNurse shows an error message.

      Use case resumes at step 1.

**Use case: UC10 - Delete an appointment**

**MSS**

1.  Nurse requests to delete a patient's appointment by the patient's ID and the appointment's number
2.  LaiNurse removes the appointment from the patient's schedule
3.  LaiNurse shows the appointments view without the deleted appointment

    Use case ends.

**Extensions**

* 1a. The ID or appointment number is missing, or in an invalid format.

    * 1a1. LaiNurse shows an error message with the valid format.

      Use case resumes at step 1.

* 1b. The given ID does not match any patient.

    * 1b1. LaiNurse shows an error message.

      Use case resumes at step 1.

* 1c. The patient has no appointment with the given number.

    * 1c1. LaiNurse shows an error naming the missing appointment number.

      Use case resumes at step 1.

**Use case: UC11 - Mark or unmark an appointment**

**MSS**

1. Nurse requests to mark or unmark an appointment using its patient ID and appointment number.
2. LaiNurse changes only the appointment's completion status and saves the data.
3. LaiNurse shows and selects the updated appointment, with its Done/Undone label.

   Use case ends.

**Extensions**

* 1a. The arguments are missing, invalid or include extra parameters.
    * 1a1. LaiNurse displays an error describing the valid format or appointment number.
    * Use case resumes at step 1.
* 1b. The patient or appointment does not exist.
    * 1b1. LaiNurse displays the existing missing-patient or missing-appointment error.
    * Use case resumes at step 1.
* 1c. The appointment already has the requested status.
    * 1c1. LaiNurse reports the existing status and changes nothing.
    * Use case resumes at step 1.
* 2a. Unmarking restores a clash with another undone visit.
    * 2a1. LaiNurse keeps the change and appends the clash warning, including hidden patients.
    * Use case resumes at step 3.

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
       Expected: The GUI opens with the sample patients. The window size may not be optimal.

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

    1. Prerequisites: Start with the sample data. It anchors Alex Yeoh's appointments to `MON` 09:30 and to `BLOOD`,
       Bernice Yu's to `TUE`, and Irfan Ibrahim's to `WED` 14:00: `MON`, `TUE` and `WED` are the Monday, Tuesday and
       Wednesday of the week you launched the app in, `SAT` is that week's Saturday, which has no appointment, and
       `BLOOD` is the Monday four weeks after `MON`. In the test cases below, write the dates themselves in commands
       and expect them in messages; for a launch in the week of Monday 5 October 2026, `MON` is `2026-10-05`, `TUE` is
       `2026-10-06`, `WED` is `2026-10-07`, `SAT` is `2026-10-10`, and `BLOOD` is `2026-11-02`.
       If you have modified the data, delete the data file and relaunch the app: the sample data anchors to the new
       launch week.

    1. Test case: `viewappt WED`<br>
       Expected: The **Appointments** tab is selected. Above the list is the heading for Wednesday, e.g. `Wed 7 Oct
       2026`, with no arrow buttons. The
       list shows only Irfan Ibrahim's appointment at 14:00, which is selected, and the
       panel beside the list shows their details with that appointment marked. The status message shows
       `1 patient(s) with appointments on WED`. On the **Patients** tab, only Irfan Ibrahim is listed.

    1. Test case: `viewappt BLOOD`, the date of Alex Yeoh's second appointment<br>
       Expected: Only Alex Yeoh's appointment on that Monday is shown, and it is selected. On the **Patients** tab, only
       Alex Yeoh is listed, because every appointment of each patient is checked.

    1. Test case: `viewappt SAT`, a date on which no patient has an appointment<br>
       Expected: The list says `No appointments in this range.`, and nothing is selected. The status message shows
       `0 patient(s) with appointments on SAT`. The patient list on the **Patients** tab is empty.

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

    1. Test case: `viewappt --from MON --to SAT`<br>
       Expected: Above the list is the heading for the range, e.g. `Mon 5 Oct – Sat 10 Oct 2026`. The list shows the
       three sample appointments, each day under its own heading, with the next appointment from now selected, or the
       first on the page if all three have passed. On the
       **Patients** tab, Alex Yeoh, Bernice Yu and Irfan Ibrahim are listed. The status message shows
       `3 patient(s) with appointments from MON to SAT`.

    1. Test case: `viewappt --to TUE --from TUE`<br>
       Expected: Bernice Yu's appointment is selected, and only Bernice Yu is listed on the **Patients** tab. The
       options may be in any order, and a one-day range is the same as `viewappt TUE`, including the status
       message `1 patient(s) with appointments on TUE`.

    1. Test case: `viewappt --from WED`<br>
       Expected: Above the list is the heading for the open range, e.g. `From Wed 7 Oct 2026`. The list shows Irfan
       Ibrahim's appointment and Alex Yeoh's blood test. The next appointment from now is selected: Alex Yeoh's if
       today is on or before that date and Irfan Ibrahim's has passed, and otherwise Irfan Ibrahim's, the first on the
       page. Both are listed on the
       **Patients** tab. The status message shows `2 patient(s) with appointments on or after WED`.

    1. Test case: `viewappt --to 2099-12-31`<br>
       Expected: Only patients with an appointment from today onwards are listed on the **Patients** tab. The range in
       the status message starts with today's date, which is the current date in your computer's time zone. If no
       appointment is in the range, the appointment page says `No appointments in this range.`

    1. Test case: `viewappt --to 2000-01-01`<br>
       Expected: The list is unchanged. The status message says that the `--to` date is before today.

    1. Test case: `viewappt --from 2026-10-31 --to 2026-10-01`<br>
       Expected: The list is unchanged. The status message says that the `--from` date must not be after the `--to` date.

    1. Other incorrect commands to try: `viewappt 2026-10-15 --to 2026-10-31` (a date and an option),
       `viewappt --till 2026-10-31` (unknown option), `viewappt --from 2026-10-01 --from 2026-10-02` (repeated
       option), `viewappt --from 2026-10-01 2026-10-05` (a second date without `--to`)<br>
       Expected: The list is unchanged. The status message shows the command format, a duplicate-option error,
       or a message that each option takes one date.

1. Viewing one patient's appointments with `--id`

    1. Prerequisites: The same sample data as above.

    1. Test case: `viewappt --id S1234567A`<br>
       Expected: Above the list is `S1234567A,` followed by the dates of Alex Yeoh's first and last appointments, e.g.
       `S1234567A, Mon 5 Oct – Mon 2 Nov 2026`. The page shows both of Alex Yeoh's
       appointments, past and upcoming, and the next one from now is selected: the insulin review on `MON` if it has
       not passed yet, and otherwise the one on `BLOOD`. On the **Patients** tab, only Alex Yeoh is listed. The
       status message shows `2 appointment(s) of ALEX YEOH`.

    1. Test case: `viewappt --id S1234567A --from MON --to SAT`<br>
       Expected: Above the list is the heading for the range with the ID, e.g. `S1234567A, Mon 5 Oct – Sat 10 Oct 2026`.
       The list shows only Alex Yeoh's
       appointment at 09:30 on the Monday, which is selected. On the
       **Patients** tab, only Alex Yeoh is listed. The status message shows
       `1 appointment(s) of ALEX YEOH from MON to SAT`.

    1. Test case: `viewappt MON --id S1234567A`, a date before the option<br>
       Expected: The same page as `viewappt MON` gives, but the status message says
       `1 appointment(s) of ALEX YEOH on MON`. A date combines with `--id` when it comes before the options.

    1. Test case: `viewappt --id S1234567A --from BLOOD`<br>
       Expected: Only Alex Yeoh's blood-test appointment is shown, and the status message shows
       `1 appointment(s) of ALEX YEOH on or after BLOOD`.

    1. Test case: `viewappt --id s1234567a`, the ID in lower case<br>
       Expected: The same as `viewappt --id S1234567A` gives. IDs are case-insensitive.

    1. Test case: `viewappt --id S1234567A --from AFTER`, a range without appointments, where `AFTER` is any date after
       `BLOOD`, such as the day after it<br>
       Expected: The list says `No appointments in this range.`, and nothing is selected. The status message shows
       `0 appointment(s) of ALEX YEOH on or after` the date. The patient list on the **Patients** tab is empty.

    1. Test case: `viewappt --id S9999999Z`, an ID that no patient has<br>
       Expected: The list is unchanged. The status message shows `The patient ID provided is invalid.`

    1. Test case: `viewappt --id S1234567A MON`, a date after the option<br>
       Expected: The list is unchanged. The status message says that a `DATE` must come before other options,
       rather than blaming the ID, which on its own is valid.

    1. Other incorrect commands to try: `viewappt --id MON S1234567A`, an ID value that ends in an ID rather
       than a date, `viewappt --id 123 456`, a digit-only ID typed with a stray space, `viewappt --id` (missing
       ID), `viewappt --id S1234567A --id S2345678B` (repeated option)<br>
       Expected: The list is unchanged. The status message shows the patient ID constraints or a duplicate-option
       error.

1. Viewing a date-only appointment next to a timed one on the same day

    1. Prerequisites: Start with the sample data, then add a patient, and give her a timed appointment on the same
       date as Bernice Yu's date-only one: `add --id T0456789D --name Eve Tan --phone 98765432 --address Blk 123
       Clementi Ave 3, #05-12`, then `addappt T0456789D TUE 15:00`.

    1. Test case: `viewappt TUE`<br>
       Expected: Under Tuesday's heading, e.g. `Tue 6 Oct`, Bernice Yu's appointment comes first and shows `‒‒:‒‒`,
       then Eve Tan's at 15:00.
       Bernice Yu's appointment is selected. Both are listed on the **Patients** tab. The status message shows
       `2 patient(s) with appointments on TUE`.

    1. Note: the patient list stays filtered after a `viewappt` command. After testing, enter `list` to show all
       patients again.

1. _{ more test cases …​ }_

### Switching views and browsing appointments

1. Browsing a `viewappt` page and the weekly view

    1. Prerequisites: Start with the sample data, which has appointments on `MON` 09:30 (Alex Yeoh), `TUE` (Bernice
       Yu, no time) and `WED` 14:00 (Irfan Ibrahim). Enter `viewappt --from MON --to SAT`.

    1. Test case: the result of the prerequisite<br>
       Expected: The **Appointments** tab is selected. The list shows the three appointments numbered 1 to 3, each under
       a heading for its day, and no heading for the days without appointments. The next appointment from now is
       selected: Alex Yeoh's, Bernice Yu's or Irfan Ibrahim's, whichever is the first that has not passed, or Alex
       Yeoh's again, as the first on the page, once all three have passed. The panel shows its details with that
       appointment marked under **Past** or **Appointments**.

    1. Test case: press Esc, then Ctrl+Up, then Down<br>
       Expected: The list gets a blue border. Ctrl+Up selects Alex Yeoh's appointment, the first on the page, whatever
       the time. Down then selects Bernice Yu's appointment, skipping Tuesday's heading,
       and the panel shows their details. Up selects Alex Yeoh's appointment again.

    1. Test case: with the focus in the list, press Ctrl+Down, then Ctrl+Up (Cmd on macOS)<br>
       Expected: Ctrl+Down selects Irfan Ibrahim's appointment, the last on the page, and Ctrl+Up selects Alex Yeoh's,
       the first.

    1. Test case: with the focus in the list, press Left or Right<br>
       Expected: Nothing changes, because a `viewappt` page has no other weeks.

    1. Test case: with the focus in the list, press Esc<br>
       Expected: The weekly view of the selected appointment's week is shown, e.g. `Mon 5 – Sun 11 Oct 2026`, with the
       arrow buttons above the list. Alex Yeoh's appointment, selected by Ctrl+Up above, stays selected.

    1. Test case: press Esc again<br>
       Expected: Nothing changes, because the weekly view has no range to remove.

    1. Test case: press Right, then Left twice, then enter `viewappt`<br>
       Expected: Right shows the week after the sample week, e.g. the week of Mon 12 Oct, and selects its first
       appointment, or nothing in a week with no appointments. Left twice shows the week before the previous one,
       e.g. the week of Mon 28 Sep. `viewappt` goes back to this week, marked `This week`.

1. Switching views without a command

    1. Prerequisites: Start with the sample data, and enter `viewappt MON`.

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
       Beside Alex Yeoh's blood-test appointment, e.g. Mon 2 Nov, `No time specified` wraps onto two lines, with the
       appointment's details,
       `MONTHLY BLOOD TEST`, under it. In a wide window, the details of each appointment move beside its time.

    1. Test case: `viewappt --from MON --to BLOOD`, then make the window as narrow as it goes<br>
       Expected: Above the list, the range heading, e.g. `Mon 5 Oct – Mon 2 Nov 2026`, stays whole. If the header runs
       out of room, the count
       beside it is cut short instead. Widening the window shows it in full.

1. Appointments that have passed today

    1. Prerequisites: Do these test cases before 23:55. Start with the sample data, and replace `TODAY` below with
       today's date, such as `2026-10-17`. Add a patient with no appointments:
       `add --id T0567890E --name Fay Ong --phone 91234567 --address Blk 1 Bedok North Ave 1`, then give her three
       appointments today: `addappt T0567890E TODAY 00:00`, then `addappt T0567890E TODAY`, and
       `addappt T0567890E TODAY 23:59`.

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

    1. Test case: `addappt T0567890E TODAY HH:MM`, where `HH:MM` is two minutes from now, then
       `view T0567890E`. Wait until the minute after `HH:MM`, without entering a command.<br>
       Expected: At first, the panel lists the new appointment under **Appointments**, in orange. Within a few seconds
       of the minute after `HH:MM`, when the appointment has passed, it moves under **Past**, dimmed. On the
       **Appointments** tab, it is greyed out.

1. Commands that keep the view

    1. Prerequisites: Start with the sample data, and enter `viewappt MON`.

    1. Test case: `delete S1234567A`<br>
       Expected: The **Appointments** tab stays selected. Alex Yeoh's appointment disappears from the page, which then
       says `No appointments in this range.` Nothing is selected, and the panel shows its starting message.

    1. Test case: `list`<br>
       Expected: The **Patients** tab is selected, and all patients are listed. Bernice Yu, now the first, is selected.

### Adding a patient

1. Adding a patient while the list is filtered

    1. Prerequisites: Start with the sample data. No patient has the ID `T0123456B`.

    1. Test case: `viewappt TUE`, then `find --name bernice`, then
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

    1. Test case: `viewappt TUE`, then `find --name bernice`, then `edit S1234567A --phone 91234567`<br>
       Expected: The **Patients** tab is selected, and the list shows all patients again. Alex Yeoh's row is
       highlighted, and the panel beside the list shows their details.

    1. Test case: `edit S0000000Z --phone 91234567`, an ID that no patient has<br>
       Expected: No patient is changed. The status message shows `The patient ID provided is invalid.`

    1. Test case: `edit S1234567A`, with nothing to change<br>
       Expected: No patient is changed. The status message shows `At least one field to edit must be provided.`

    1. Test case: `edit --phone 91234567`, with no ID<br>
       Expected: No patient is changed. The status message shows the command format.

1. Appointments alongside edits

    1. Prerequisites: Start with the sample data. Alex Yeoh has the appointments `MON 09:30`, with the details
       `INSULIN REVIEW`, and `BLOOD`.

    1. Test case: `edit S1234567A --phone 91234567`<br>
       Expected: Only the phone number changes. Both appointments keep their date and time, and the Monday
       appointment keeps its details.

### Editing an appointment

1. Moving an appointment and setting its details

    1. Prerequisites: Start with the sample data. Alex Yeoh (`S1234567A`) has appointment #1 at `MON 09:30`, with
       the details `INSULIN REVIEW`, and #2 at `BLOOD`, with the details `MONTHLY BLOOD TEST`.

    1. Test case: `editappt S1234567A 1 --to TUE 10:00`<br>
       Expected: The **Appointments** tab is selected, showing the sample week, with Alex Yeoh's appointment at
       10:00 selected and still showing `INSULIN REVIEW`. The status message ends with
       `(moved from MON 09:30)`.

    1. Test case: `editappt S1234567A 2 --details Fasting blood test`<br>
       Expected: The blood-test appointment keeps its date, and its details become `FASTING BLOOD TEST`.

1. Resolving lookup errors and moving onto an occupied time

    1. Prerequisites: Start with the sample data.

    1. Test case: `editappt S1234567A 99 --to TUE`, an appointment number the patient does not have<br>
       Expected: No appointment is changed. The status message says `Patient S1234567A has no appointment #99.`

    1. Test case: `editappt S1234567A 1 --to BLOOD`<br>
       Expected: The appointment moves to `BLOOD`, keeps its number and details, and both visits are preserved.
       The result warns about the other appointment number; both dates/times appear in red.

    1. Test case: `editappt S1234567A 1`, with nothing to change<br>
       Expected: No appointment is changed. The status message says that `--to` or `--details` must be given.

### Adding an appointment

1. Adding an appointment

    1. Prerequisites: Start with the sample data. Bernice Yu (`S2345678B`) has an appointment only on `TUE`.

    1. Test case: `addappt S2345678B SAT --details Check blood pressure`<br>
       Expected: The **Appointments** tab is selected, showing the sample week, with Bernice Yu's appointment
       selected and showing `CHECK BLOOD PRESSURE`. The list shows the appointment with no time.

    1. Test case: `addappt S1234567A AFTER 10:00`<br>
       Expected: The week of `AFTER` is shown, with Alex Yeoh's appointment at 10:00 selected.

1. Adding at an occupied time and rejecting an unknown patient

    1. Prerequisites: Start with the sample data.

    1. Test case: `addappt S1234567A MON 09:30`<br>
       Expected: Appointment #3 is added at `MON 09:30`. Both visits remain, and the result warns about appointment
       #1 of Alex Yeoh. Their times and that day's heading appear in red.

    1. Test case: `addappt S0000000Z 2026-10-14`<br>
       Expected: No appointment is added. The status message shows `The patient ID provided is invalid.`

### Deleting an appointment

1. Deleting an appointment by its number

    1. Prerequisites: Start with the sample data. Alex Yeoh (`S1234567A`) has appointment #1 at `MON 09:30` and
       #2 at `BLOOD`.

    1. Test case: `deleteappt S1234567A 1`<br>
       Expected: The appointment is deleted. The status message names the patient's ID and the deleted appointment with
       its details. The **Appointments** tab is selected, on the page that was shown, and the appointment is gone from it.

    1. Test case: `deleteappt S1234567A 2`<br>
       Expected: The date-only blood-test appointment is deleted, as above.

1. Deleting an appointment that cannot be deleted

    1. Prerequisites: Start with the sample data.

    1. Test case: `deleteappt S1234567A 99`<br>
       Expected: No appointment is deleted. The status message says `Patient S1234567A has no appointment #99.`

    1. Test case: `deleteappt S0000000Z 1`<br>
       Expected: No appointment is deleted. The status message shows `The patient ID provided is invalid.`

    1. Other incorrect deleteappt commands to try: `deleteappt` (missing everything), `deleteappt S1234567A`
       (missing number), `deleteappt S1234567A 1 extra` (extra argument)<br>
       Expected: No appointment is deleted. The status message shows the correct command format.

1. Deleting an appointment of a patient hidden by a filter

    1. Prerequisites: Start with the sample data. Run `find --name bernice`, so only Bernice Yu is shown.

    1. Test case: `deleteappt S1234567A 1`<br>
       Expected: The appointment of Alex Yeoh, who is not in the list shown, is deleted. The find filter stays, so the
       patient list still shows only Bernice Yu.

    1. Test case: run `viewappt WED` first, then `deleteappt G1234567E 1`<br>
       Expected: The appointments view keeps showing Wednesday, now without Irfan Ibrahim's appointment.

1. _{ more test cases …​ }_

### Appointment numbers, migration and clash highlighting

1. Start with sample data. Run `addappt S2345678B MON 09:30`, using the sample week's Monday for `MON`.
   Expected: Bernice's new appointment is #2. The warning names Alex Yeoh, ID `S1234567A`, appointment #1.
   Both times and Monday's heading are red. A `viewappt --id S2345678B` filter still shows the warning color.
1. Run `addappt S1234567A MON 09:30 --details Second visit`.
   Expected: Alex's new visit is #3 and the warning names Alex's #1 and Bernice's #2. Both Alex visits survive.
1. Run `editappt S1234567A --appt MON 09:30 --details Review`, then the equivalent `deleteappt` selector.
   Expected: Each rejects the unsupported option, shows its number-based command format and saves no changes.
1. Run `editappt S1234567A 3 --to TUE 10:00`, then `view S1234567A`.
   Expected: Only #3 moves; its number/details and selection survive the move. The detail date starts with `#3`.
1. Run `deleteappt S1234567A 3`, restart, then add another visit to Alex.
   Expected: Surviving numbers stay #1 and #2. The new visit receives #3.
1. Load a legacy file with two appointment objects for one patient at the same time and different details, plus
   an earlier plain-string appointment. Expected: All three load, numbered chronologically. Equal-time visits
   keep their file order and appear red immediately. After a successful command and restart, all numbers persist.
1. Check clashing visits from yesterday and today, including selected rows and the marked detail appointment.
   Expected: Date/time text remains red. Other past text stays dimmed, today's non-clashing dates stay orange,
   and selection backgrounds stay blue. Moving/deleting the other visit removes obsolete red highlighting.
1. Resize to a narrow window. Expected: `#N`, dates, times and long details remain readable with wrapping;
   page positions remain distinct from patient-local appointment numbers.

### Marking and unmarking appointments

1. Completion, preservation and persistence

    1. Prerequisites: Start with sample data. Alex Yeoh (`S1234567A`) has appointments #1 and #2.
    1. Test case: `markappt S1234567A 2`<br>
       Expected: Appointment #2 shows **Done** in the list and details and is selected in **Appointments**.
       Its number, date, time and details stay unchanged. Appointment #1 stays undone. If #2 is in the future,
       it is no longer eligible for Alex's next-appointment summary, but stays in its date-based group.
    1. Test case: `markappt S1234567A 2` again<br>
       Expected: An already-done error; model, page, selection and saved file stay unchanged.
    1. Test case: `editappt S1234567A 2 --to 2099-12-31 --details Follow up`, then restart<br>
       Expected: #2 keeps **Done**, its number and the new details after rescheduling and restart.
    1. Test case: `unmarkappt S1234567A 2`, then the same command again<br>
       Expected: The first command changes only the status to **Undone** and selects #2. The second reports
       already undone and changes nothing. Restarting preserves the undone status.

1. Clashes, hidden patients and page preservation

    1. Prerequisites: Start with sample data; use its Monday date for `MON`.
    1. Test case: `addappt S2345678B MON 09:30`, then `viewappt --id S1234567A`<br>
       Expected: Alex's #1 clashes with Bernice's #2, even though Bernice is hidden on this appointment page.
    1. Test case: `markappt S2345678B 2`, then `viewappt --id S1234567A`<br>
       Expected: Alex's #1 and the day heading are no longer red. Bernice's visit remains visible when viewed.
    1. Test case: `unmarkappt S2345678B 2`<br>
       Expected: The warning names Alex and #1. Both undone visits clash again; the updated visit is selected.
    1. Test case: `viewappt --id S2345678B`, then `markappt S2345678B 2`<br>
       Expected: The page stays, #2 remains selected and shows **Done**. The list and details update immediately,
       including in a narrow window. Done status does not itself grey out a future visit.

1. Invalid commands and completion

    1. Test cases: `markappt S0000000Z 1`, `unmarkappt S1234567A 99`, `markappt S1234567A 0`,
       `unmarkappt S1234567A 2147483648`, `markappt S1234567A`, `markappt S1234567A 1 extra`,
       `unmarkappt S1234567A --appt MON`<br>
       Expected: The applicable missing-patient, missing-number, number-constraint or syntax error; no changes.
    1. Test case: type `markappt s12 2`, then press Tab<br>
       Expected: The patient ID is completed and the appointment number is kept. Try `unmarkappt` likewise.
    1. Test cases: `help markappt`, `help unmarkappt`<br>
       Expected: The correct two-positional-argument syntax and examples appear.
    1. Test case: load a legacy file without `done` fields<br>
       Expected: Every visit starts undone. Saving writes booleans; an explicit non-boolean `done` value is
       rejected through the existing invalid-data loading behavior.

### Viewing help and command instructions

1. Command overview via CLI
    * Test case: `help`
        * Expected: An alphabetical summary of all available commands (`add`, `addappt`, `clear`, `delete`, `deleteappt`, `edit`, `editappt`, `exit`, `find`, `help`, `list`, `markappt`, `unmarkappt`, `view`, `viewappt`) is displayed in the result box.

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

Run these from a terminal in the folder containing the JAR file, with `java -jar lainurse.jar`, and close the app
before you change any file in its `data` folder.

1. Saving the data file and its backup

    1. Launch the app in an empty folder, and run `list`.<br>
       Expected: `data/lainurse.json` and `data/lainurse.backup.json` are created, and hold the same data.

    1. Test case: `delete` a patient, then close the app.<br>
       Expected: Both files no longer hold that patient.

1. Dealing with a missing data file

    1. Prerequisites: Both `data/lainurse.json` and `data/lainurse.backup.json` exist.

    1. Test case: Delete `data/lainurse.json`, and launch the app.<br>
       Expected: The app starts with the sample patients, and the result display is empty. After `list`, both files
       hold the sample data.

1. Dealing with a corrupted data file

    1. Prerequisites: `data/lainurse.backup.json` holds valid data, such as after `list`.

    1. Test case: Replace the contents of `data/lainurse.json` with `{ not json`, and launch the app.<br>
       Expected: The app starts with the data in the backup. The result display says that `data/lainurse.json` could
       not be loaded, that the backup was loaded, and that a copy was kept at `data/lainurse.corrupt.json`, which
       holds `{ not json`.

    1. Test case: As above, but instead change a patient's `phone` in `data/lainurse.json` to `abc`.<br>
       Expected: As above. The corrupt copy holds the edited file, with `abc`.

    1. Test case: After either test case above, run `list`, close the app, and launch it again.<br>
       Expected: `data/lainurse.json` holds the backup's data again, so the app starts with it and with an empty
       result display. `data/lainurse.corrupt.json` is unchanged.

1. Dealing with a corrupted data file and backup

    1. Test case: Replace the contents of both `data/lainurse.json` and `data/lainurse.backup.json` with
       `{ not json`, and launch the app.<br>
       Expected: The app starts with no patients. The result display says that neither file could be loaded, and that
       a copy of the data file was kept at `data/lainurse.corrupt.json`.

    1. Test case: Replace the contents of `data/lainurse.json` with `{ not json`, delete
       `data/lainurse.backup.json`, and launch the app.<br>
       Expected: As above, but the result display says that there is no backup. It also warns that the next command
       that succeeds, even `exit`, saves the empty data, and that the window's close button does not.

    1. Test case: After either test case above, close the window with its close button, without running any
       command.<br>
       Expected: `data/lainurse.json` and `data/lainurse.backup.json` (if there was one) are unchanged.

    1. Test case: After either test case above, run `exit`.<br>
       Expected: `data/lainurse.json` and `data/lainurse.backup.json` both now hold no patients.
       `data/lainurse.corrupt.json` still holds `{ not json`.
