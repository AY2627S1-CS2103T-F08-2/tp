package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Tells the window each time the minute changes, on the JavaFX thread, so that it can show which appointments have
 * passed, and which day is today, while it is left open. It reads the clock every few seconds rather than once a
 * minute, so that it notices a new minute within seconds, also after the computer wakes from sleep.
 */
class MinuteClock {

    private static final Duration CHECK_INTERVAL = Duration.seconds(5);

    private final Timeline timeline;
    private LocalDateTime lastMinute = currentMinute();

    /**
     * Creates a {@code MinuteClock} that, once started, runs {@code onMinuteChanged} with the minute that it saw last
     * and the minute now, each time the two differ.
     */
    MinuteClock(BiConsumer<LocalDateTime, LocalDateTime> onMinuteChanged) {
        requireNonNull(onMinuteChanged);
        timeline = new Timeline(new KeyFrame(CHECK_INTERVAL, event -> {
            LocalDateTime minute = currentMinute();
            if (!minute.equals(lastMinute)) {
                LocalDateTime previousMinute = lastMinute;
                lastMinute = minute;
                onMinuteChanged.accept(previousMinute, minute);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Starts reading the clock.
     */
    void start() {
        timeline.play();
    }

    /**
     * Stops reading the clock.
     */
    void stop() {
        timeline.stop();
    }

    private static LocalDateTime currentMinute() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }
}
