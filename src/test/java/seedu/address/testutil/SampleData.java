package seedu.address.testutil;

import java.time.LocalDate;

import seedu.address.model.ReadOnlyLaiNurse;
import seedu.address.model.util.SampleDataUtil;

/**
 * Anchors the sample data to a fixed date, so that tests see the sample appointments on known dates whatever
 * the day the tests run on.
 */
public class SampleData {

    /**
     * The Monday that the sample data is anchored to; it is also the start of its week.
     */
    public static final LocalDate SAMPLE_TODAY = LocalDate.parse("2026-10-05");

    /**
     * Returns the sample data anchored to {@link #SAMPLE_TODAY}: Alex Yeoh has the appointments 2026-10-05 09:30
     * and 2026-11-02, Bernice Yu has 2026-10-06, and Irfan Ibrahim has 2026-10-07 14:00.
     */
    public static ReadOnlyLaiNurse getSampleLaiNurse() {
        return SampleDataUtil.getSampleLaiNurse(SAMPLE_TODAY);
    }

}
