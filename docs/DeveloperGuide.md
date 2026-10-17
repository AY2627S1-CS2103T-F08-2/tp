---
layout: page
title: Developer Guide
---

* Table of Contents {:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the
  originals.}_

* Codebase adapted from [AddressBook-Level3 (AB3)](https://github.com/se-edu/addressbook-level3) developed by the SE-EDU initiative.
* UI components and controls utilize native [JavaFX 17+](https://openjfx.io/) libraries.

* AI Use Declaration from Qin Fangzheng: "As my career path is not related to software engineering, I choose AI-5/AI-6 as my AI use level, where I get Codex to do the tasks, and then I myself review the results, including feature behavior and partial code."

* AI Use Declaration from Jaydon: "I used Codex and OpenCode to review and generate code.
  Based on the given levels of AI use in coding tasks, this ranges between AI-2 and AI-5".

* AI Use Declaration from Yuhao: "I used Claude Code to write code, tests and documentation, and I review and test the results."

* AI Use Declaration from Yong Kang: "I used Gemini to write and review code, tests and documentation. No automation is done and I go back and forth consistently with AI to ensure I am still largely in charge of the process. AI levels between AI-1 and AI-5."

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
* `DetailAppointments` picks the appointments that the details list: every one that has not passed, and the latest two
  that have, plus a marked older one.
* The appointment list greys out the appointments that have passed.

Each of these decides what has passed, and which day is today, when it is drawn. So that they stay right while the
window is left open, `MinuteClock` reads the clock every few seconds and tells `MainWindow` when the minute changes.
`MainWindow` then asks `AppointmentFormatter#hasChangedBetween` whether an appointment has passed, or a new day has
begun, since the minute before. Only then does it refresh both lists and both details panels, which keep their
selection. Reading the clock every few seconds, rather than once a minute, also catches up within seconds after the
computer wakes from sleep.

`AppointmentListPanel` shows the appointments of `Logic#getAppointmentList()`, which holds every appointment of every
patient, on one of two kinds of page. The weekly view shows one `AppointmentWeek`, from Monday to Sunday. After
`viewappt`, the panel shows only the appointments in the command's range, on one page, and hides the week's arrow
buttons. `AppointmentPage#ofWeek` and `AppointmentPage#ofRange` turn the appointments into rows: a heading for each day
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
end the command. `edit` takes the ID first and options after it, so `IdCompleter` completes the word after `edit` and
keeps the options. `CommandBox` also keeps a `CommandHistory` of the commands entered since the app started, which the
Up and Down keys go through. The history is kept only in the UI, because no command needs it.

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
* Esc resets the view. In the patient view, it executes `list` through the same `executeCommand` method as a typed
  command, so the result is the same as typing `list`. In the appointment view, it goes back from a `viewappt` page to
  the weekly view, and from the weekly view to this week.
* Tab moves the focus between the tabs and the list. The appointment list always takes the focus, so that Left and
  Right can leave an empty week.
* Shift+Up and Shift+Down scroll the details beside the list, with `PatientDetailPanel#scroll`, as the details never
  take the focus.
* Shift+Tab does nothing, also in the command box. `MainWindow` consumes it, or JavaFX would move the focus to another
  node.

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

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

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
  when the list changes.
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

`viewappt` takes either one date, or a range given by `--from`, `--to`, or both. `ViewAppointmentCommandParser`
normalizes option separators in the same way as `add`, so unknown options are rejected. A single date becomes a range
that starts and ends on that date. When both options are given, the parser rejects a `--from` date that is after the
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
`LocalDate.MAX` if the range has no end. `MainWindow` then tells `AppointmentListPanel` to show only the appointments in
that range, on one page, and to select the first one.

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

### Patient and appointment views

The main window shows a patient view or an appointment view, on two tabs. Commands choose the view: a command word
that ends in `appt` shows the appointment view, and `list`, `find`, `view`, `add` and `edit` show the patient view.
Other commands keep the view that is shown.

Each `Command` declares the view it shows by overriding `Command#getView()`, which returns `Optional.empty()` by default.
`LogicManager#execute` adds that view to the command's `CommandResult` with `CommandResult#withView`, and
`MainWindow#executeCommand` switches to it. A `CommandResult` can also hold an `AppointmentRange`, as the result of
`viewappt` does. Then `MainWindow` tells `AppointmentListPanel` to show the appointments in that range on one page.

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

* **Alternative 1 (current choice):** The weekly view lists every appointment, a week at a time. `viewappt` shows only
  the appointments in its range, on one page with a heading for each day that has one, and Esc goes back to the weekly
  view.
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
2. **Targeted CLI Help (`help [COMMAND]` or `help [--COMMAND]`)**: Displays parameter formats, constraints, and syntax examples for a specific command directly in the result display box by reading canonical `MESSAGE_USAGE` definitions.
3. **Pop-up User Guide Window (`help window`, `help --window`, menu bar, or <kbd>F1</kbd>)**: Opens a standalone non-modal window displaying the full, offline User Guide rendered using native JavaFX components.

#### Implementation

The help architecture spans argument parsing in `Logic`, command dispatching in `HelpCommand`, and native rendering in `UI`.

##### 1. Command Parsing & Target Normalization (`HelpCommandParser`)

`HelpCommandParser` processes user input according to the following rules:
* Strips surrounding whitespace and checks for an empty argument string, returning a targetless `HelpCommand()` (Tier 1: General Help).
* Normalizes flags: detects if the argument begins with `--` (via `TARGET_FLAG_MARKER`), strips the prefix, and ensures no whitespace exists between `--` and the target name (e.g., rejecting `help -- add`).
* Normalizes casing using `Locale.ROOT` (`commandTarget.toLowerCase(Locale.ROOT)`) to ensure consistent parsing across system locales (avoiding locale-specific casing anomalies such as Turkish dotted 'i').
* Rejects multi-token arguments (e.g., `help add patient`) or isolated flag markers (`help --`) by throwing a `ParseException` with `HelpCommand.MESSAGE_USAGE`.

##### 2. Dynamic Command Usage Mapping (`HelpCommand`)

Rather than hardcoding command names and usage strings inside `HelpCommand`:
* A static `Map<String, String> COMMAND_USAGE_MAP` is initialized using a `TreeMap` to sort available command words alphabetically.
* Each command registers its canonical `MESSAGE_USAGE` string dynamically (e.g., `AddCommand.COMMAND_WORD -> AddCommand.MESSAGE_USAGE`).
* The help window target (`"window"`) is deliberately decoupled from `COMMAND_USAGE_MAP`. It is handled as a distinct navigation branch returning `new CommandResult(SHOWING_HELP_MESSAGE, true, false)`.
* If a target matches a registered command, its usage string is returned. If unknown, `MESSAGE_UNKNOWN_HELP_TARGET` is returned listing all valid command words.

##### 3. Build-Time Bundling & Extraction

* Gradle's `processResources` task packages `docs/UserGuide.md` into the application JAR classpath under `/docs/UserGuide.md`, serving as the single offline source of truth.
* `HelpBookUtil` loads the document as an input stream via `Class#getResourceAsStream("/docs/UserGuide.md")`.

##### 4. Native UI Node Generation (`MarkdownRenderer`)

`MarkdownRenderer` parses the sanitized Markdown sequentially into native JavaFX nodes without external third-party dependencies:
* **Section Structure & Headings**: Tracks headers (`#`, `##`, `###`) and horizontal separators (`---`), rendering them as styled `Label` and `Separator` nodes, while resetting active list counters.
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

**Use case: UC05 - View appointments by date**

**MSS**

1. Nurse requests to view appointments on a date, or between two dates.
2. LaiNurse shows only the appointments in that range, with the first appointment and its patient's details, and
   lists the patients who have any appointment in the range.
3. Nurse requests to go back to the weekly view.
4. LaiNurse shows the week that it showed before.

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

    * 1d1. LaiNurse uses a range with no end, so that it covers every appointment on or after the start date.

      Use case resumes at step 2.

* 2a. No patients have an appointment on the given date or in the given range.

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

1.  Nurse requests help for a specific command (e.g., `help add` or `help --add`).
2.  LaiNurse displays the parameter syntax, format constraints, and usage examples for that command in the result display.

    Use case ends.

**Extensions**

* 1a. Nurse requests a general overview of all commands (`help`).
    * 1a1. LaiNurse displays an alphabetical list of all available commands and general navigation guidance in the result display.
    * Use case ends.

* 1b. Nurse requests to open the full offline User Guide window (`help window`, `help --window`, press <kbd>F1</kbd>, or select **Help** > **Help**).
    * 1b1. LaiNurse opens the Help Window containing the complete offline User Guide rendered using native JavaFX components.
    * Use case ends.

* 1c. Nurse enters an unrecognized command target (e.g., `help nonexistent` or `help --nonexistent`).
    * 1c1. LaiNurse displays an unknown help target error message, lists all available commands in alphabetical order, and provides instructions to access the full guide window.
    * Use case ends.

* 1d. Nurse provides a malformed help argument (e.g., `help -- add`, `help --`, or multiple tokens such as `help add patient`).
    * 1d1. LaiNurse displays an invalid command format error displaying `HelpCommand.MESSAGE_USAGE`.
    * Use case ends.

* 1e. Nurse enters the target command with mixed or uppercase letters (e.g., `help VIEW` or `help --DELETE`).
    * 1e1. LaiNurse normalizes the target using `Locale.ROOT` and displays the syntax details for the matching command.
    * Use case ends.

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
       Expected: The **Patients** tab is selected. The panel beside the list shows the patient's name, ID, phone number, address and medical history, then their appointments that have not passed, then up to two that have, dimmed. Their row in the list is highlighted. The status message names the patient.

    1. Test case: `find --name bernice`, then `view S1234567A`<br>
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

    1. Test case: type `edit s23 --phone 91234567`, then press Tab<br>
       Expected: The command box shows `edit S2345678B --phone 91234567`. The options are kept.

    1. Test case: type `add --id S1`, `view XYZ`, `view S1 extra` or `edit --phone 91234567`, then press Tab<br>
       Expected: The command box does not change.

1. Moving between the command box and the patient list

    1. Prerequisites: Start with the sample data. Enter `find --address serangoon`, so that the list shows Bernice Yu
       and David Li, and no patient's details are shown.

    1. Test case: the result of the prerequisite<br>
       Expected: The header above the list shows `Filtered patients`, `Esc to show all patients` and `2 of 6`.

    1. Test case: type `abc` in the command box, then press Esc<br>
       Expected: The patient list gets a blue border and the command box is dimmed. `abc` stays in the command box.
       Bernice Yu is selected, and the panel shows her details.

    1. Test case: after the test case above, press Down<br>
       Expected: David Li is selected, and the panel shows his details. Up selects and shows Bernice Yu again.

    1. Test case: with Bernice Yu selected at the top of the list, press Up<br>
       Expected: The **Patients** tab gets a blue border. A second Up does nothing. Down moves the focus back to the
       list, with Bernice Yu still selected.

    1. Test case: after the test cases above, press Esc<br>
       Expected: The list shows all patients again, and the header shows `All patients` and `6 patients`. The status
       message says that all patients are listed. The patient shown in the panel stays selected, and the focus stays in
       the list.

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

    1. Test case: `find --phone 4388`<br>
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
       Expected: The **Appointments** tab is selected. Above the list are `Wed 7 Oct 2026` and `Esc to show the weekly
       view`, and no arrow buttons. The list shows only Irfan Ibrahim's appointment at 14:00, which is selected, and the
       panel beside the list shows his details with that appointment marked. The status message shows
       `1 patient(s) with appointments on 2026-10-07`. On the **Patients** tab, only Irfan Ibrahim is listed.

    1. Test case: `viewappt 2026-11-02`, the date of Alex Yeoh's second appointment<br>
       Expected: Only Alex Yeoh's appointment on Mon 2 Nov is shown, and it is selected. On the **Patients** tab, only
       Alex Yeoh is listed, because every appointment of each patient is checked.

    1. Test case: `viewappt 2026-11-11`, a date on which no patient has an appointment<br>
       Expected: The list says `No appointments in this range.`, and nothing is selected. The status message shows
       `0 patient(s) with appointments on 2026-11-11`. The patient list on the **Patients** tab is empty.

    1. Test case: `viewappt 2026-02-30`, an impossible date<br>
       Expected: The list is unchanged. The status message shows an error: `Dates should be a real date in the format yyyy-MM-dd`.

    1. Test case: `viewappt 15/10/2026`, a date in the wrong format<br>
       Expected: The list is unchanged. The status message shows the same date format error as above.

    1. Other incorrect `viewappt` commands to try: `viewappt` (missing date), `viewappt 2026-10-15 2026-10-20` (more than one date)<br>
       Expected: The list is unchanged. The status message shows the correct command format or the date format error.

1. Viewing patients who have an appointment in a date range

    1. Prerequisites: The same sample data as above.

    1. Test case: `viewappt --from 2026-10-01 --to 2026-10-31`<br>
       Expected: Above the list is `Thu 1 Oct – Sat 31 Oct 2026`. The list shows the three appointments on 5, 6 and
       7 Oct, each day under its own heading, with Alex Yeoh's at 09:30 on Mon 5 Oct selected. On the **Patients** tab,
       Alex Yeoh, Bernice Yu and Irfan Ibrahim are listed. The status message shows
       `3 patient(s) with appointments from 2026-10-01 to 2026-10-31`.

    1. Test case: `viewappt --to 2026-10-06 --from 2026-10-06`<br>
       Expected: Bernice Yu's appointment is selected, and only Bernice Yu is listed on the **Patients** tab. The
       options may be in any order, and a one-day range behaves like `viewappt 2026-10-06`.

    1. Test case: `viewappt --from 2026-10-07`<br>
       Expected: Above the list is `From Wed 7 Oct 2026`. The list shows Irfan Ibrahim's appointment, which is
       selected, and Alex Yeoh's on 2 Nov. Both are listed on the **Patients** tab. The status message shows
       `2 patient(s) with appointments on or after 2026-10-07`.

    1. Test case: `viewappt --to 2099-12-31`<br>
       Expected: Only patients with an appointment from today onwards are listed on the **Patients** tab. The range in
       the status message starts with today's date, which is the current date in your computer's time zone. If no
       appointment is in the range, the list says `No appointments in this range.`

    1. Test case: `viewappt --to 2000-01-01`<br>
       Expected: The list is unchanged. The status message says that the `--to` date is before today.

    1. Test case: `viewappt --from 2026-10-31 --to 2026-10-01`<br>
       Expected: The list is unchanged. The status message says that the `--from` date must not be after the `--to` date.

    1. Other incorrect commands to try: `viewappt 2026-10-15 --to 2026-10-31` (a date and an option),
       `viewappt --till 2026-10-31` (unknown option), `viewappt --from 2026-10-01 --from 2026-10-02` (repeated option)<br>
       Expected: The list is unchanged. The status message shows the command format or a duplicate-option error.

1. Viewing a date-only appointment next to a timed one on the same day

    1. Prerequisites: Start with the sample data, then add a timed appointment on the same date as Bernice Yu's
       date-only one: `add --id T0456789D --name Eve Tan --phone 98765432 --address Blk 123 Clementi Ave 3, #05-12 --appt 2026-10-06 15:00`.

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
       and the panel shows his details with that appointment marked under **Past** or **Appointments**.

    1. Test case: press Esc, then Down<br>
       Expected: The list gets a blue border. Bernice Yu's appointment is selected, skipping the heading of Tue 6 Oct,
       and the panel shows her details. Up selects Alex Yeoh's appointment again.

    1. Test case: with the focus in the list, press Ctrl+Down, then Ctrl+Up (Cmd on macOS)<br>
       Expected: Ctrl+Down selects Irfan Ibrahim's appointment, the last on the page, and Ctrl+Up selects Alex Yeoh's,
       the first.

    1. Test case: with the focus in the list, press Left or Right<br>
       Expected: Nothing changes, because a `viewappt` page has no other weeks.

    1. Test case: with the focus in the list, press Esc<br>
       Expected: The weekly view shows the week that it showed before `viewappt`, with the arrow buttons above the list.
       If you had not moved it, that is this week, marked `This week`. Its first appointment that has not passed is
       selected, if it has one.

    1. Test case: press Right, then Left twice<br>
       Expected: Right shows next week, marked `Next week`, and selects its first appointment, or nothing in a week with
       no appointments. Left twice shows last week, marked `Last week`. Esc goes back to this week.

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

1. Appointments that have passed today

    1. Prerequisites: Do these test cases before 23:59. Start with the sample data, and replace `TODAY` below with
       today's date, such as `2026-10-17`. Add a patient with three appointments today:
       `add --id T0567890E --name Fay Ong --phone 91234567 --address Blk 1 Bedok North Ave 1 --appt TODAY 00:00 --appt TODAY --appt TODAY 23:59`.

    1. Test case: the result of the prerequisite<br>
       Expected: On the **Patients** tab, Fay Ong's row shows `Today` in orange as her next appointment: the one with
       no time, because the one at 00:00 has passed.

    1. Test case: `view T0567890E`<br>
       Expected: Under **Appointments**, the panel lists today's appointment with `No time specified`, then the one at
       23:59, both in orange. Under **Past**, it lists the one at 00:00, dimmed.

    1. Test case: click the **Appointments** tab, then press Esc<br>
       Expected: The weekly view shows this week. Under today's heading, in orange, the appointment at 00:00 comes
       first and is greyed out. Then comes the one with no time, showing `‒‒:‒‒`, and then the one at 23:59. The one
       with no time is selected, as the first appointment that has not passed, and the panel shows Fay Ong's details
       with it marked.

1. Commands that keep the view

    1. Prerequisites: Start with the sample data, and enter `viewappt 2026-10-05`.

    1. Test case: `delete S1234567A`<br>
       Expected: The **Appointments** tab stays selected. Alex Yeoh's appointment disappears from the page, which then
       says `No appointments in this range.` Nothing is selected, and the panel shows its starting message.

    1. Test case: `list`<br>
       Expected: The **Patients** tab is selected, and all patients are listed.

### Editing a patient

1. Editing a patient's details by ID

    1. Prerequisites: Start with the sample data. Alex Yeoh has the ID `S1234567A`.

    1. Test case: `edit S1234567A --phone 91234567`<br>
       Expected: Alex Yeoh's phone number changes to `91234567`. The other details are unchanged.

    1. Test case: `edit S0000000Z --phone 91234567`, an ID that no patient has<br>
       Expected: No patient is changed. The status message shows `The patient ID provided is invalid.`

    1. Test case: `edit S1234567A`, with nothing to change<br>
       Expected: No patient is changed. The status message shows `At least one field to edit must be provided.`

1. Adding and removing appointments

    1. Prerequisites: Start with the sample data. Alex Yeoh has the appointments `2026-10-05 09:30` and `2026-11-02`.

    1. Test case: `edit S1234567A --add-appt 2026-10-20 --add-appt 2026-12-01 10:00`<br>
       Expected: The status message lists Alex Yeoh's four appointments, from earliest to latest.

    1. Test case: `edit S1234567A --remove-appt 2026-10-05 09:30 --add-appt 2026-10-06 09:30`<br>
       Expected: The 5 October appointment is replaced by one on 6 October.

    1. Test case: `edit S1234567A --remove-appt 2026-11-02 09:30`, which does not match the date-only appointment<br>
       Expected: No patient is changed. The status message shows `Patient S1234567A has no appointment on 2026-11-02 09:30.`

    1. Test case: `edit S1234567A --add-appt 2026-02-30`, an impossible date<br>
       Expected: No patient is changed. The status message shows the appointment format error.

### Viewing help and command instructions

1. Command overview via CLI
    * Test case: `help`
        * Expected: An alphabetical summary of all available commands (`add`, `clear`, `delete`, `edit`, `exit`, `find`, `help`, `list`, `view`, `viewappt`) is displayed in the result box.

2. Targeted command help
    * Test case: `help add` (or `help --add`)
        * Expected: Parameter syntax and usage examples for `add` are displayed.
    * Test case: `help viewappt` (or `help --viewappt`)
        * Expected: Both single-date and date-range syntax options (`--from`, `--to`) for `viewappt` are displayed.
    * Test case: `help --DELETE` or `help VIEW`
        * Expected: Case-insensitive parsing via `Locale.ROOT`; displays usage for `delete` and `view` respectively.

3. Rejecting invalid help inputs
    * Test case: `help nonexistent`
        * Expected: Displays `Unknown help command: 'nonexistent'.` along with the list of valid commands.
    * Test case: `help --` or `help -- add`
        * Expected: Displays invalid command format error showing `HelpCommand.MESSAGE_USAGE`.
    * Test case: `help add patient`
        * Expected: Multi-token arguments are rejected; displays invalid command format error.

4. Opening the full User Guide window
    * Test case: `help window` or `help --window`
        * Expected: The Help Window opens displaying the full bundled User Guide formatted with native JavaFX nodes. The status message confirms: `Opened help window.`.
    * Test case: Press <kbd>F1</kbd> or click **Help** > **Help** in the menu bar.
        * Expected: Opens the Help Window.
    * Test case: Inside the Help Window, click any inline link or the **Copy URL** button.
        * Expected: The target URL is copied to the system clipboard, and the label changes to indicate `(Copied!)` temporarily.

### Saving data

1. Dealing with missing/corrupted data files

    1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
