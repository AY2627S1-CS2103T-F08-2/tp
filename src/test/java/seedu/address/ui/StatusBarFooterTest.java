package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class StatusBarFooterTest {

    @Test
    public void formatCompletions_fewWords_showsAll() {
        assertEquals("", StatusBarFooter.formatCompletions(List.of()));
        assertEquals("delete · deleteappt", StatusBarFooter.formatCompletions(List.of("delete", "deleteappt")));
        assertEquals("a · b · c · d · e",
                StatusBarFooter.formatCompletions(List.of("a", "b", "c", "d", "e")));
    }

    @Test
    public void formatCompletions_manyWords_showsFirstFewAndCountsTheOthers() {
        assertEquals("a · b · c · d · e · +1 more",
                StatusBarFooter.formatCompletions(List.of("a", "b", "c", "d", "e", "f")));
        assertEquals("a · b · c · d · e · +19 more",
                StatusBarFooter.formatCompletions(List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
                        "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x")));
    }
}
