package org.unique_events_queue;

import java.util.*;

final class RecordFactory {
    private final Random random;
    private final IRecordFactorySettings settings;

    public RecordFactory(IRecordFactorySettings settings) {
        random = new Random(settings.getSeed());
        this.settings = settings;
    }

    public Record generateRandomFakeRecord(){
        String id = generateRandomString(settings.getLeftCharLimit(), settings.getRightCharLimit(),
            settings.getStringMaxLength());
        float amount = generateRandomFloat(settings.getFloatMin(), settings.getFloatMax());
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        /* The dates are arbitrary */
        calendar.set(2022, 0, 1, 1, 1, 1);
        Date dateLowerBound = new Date(0);
        Date dateUpperBound = calendar.getTime();
        Date date = generateRandomDate(dateLowerBound, dateUpperBound);

        return new Record(id, amount, date);
    }

    private String generateRandomString(int leftCharLimit, int rightCharLimit, int maxLength) {
        return random.ints(leftCharLimit, rightCharLimit + 1)
                .limit(random.nextInt(maxLength))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private float generateRandomFloat(float amountMinimum, float amountMaximum) {
        return amountMinimum + this.random.nextFloat() * (amountMaximum - amountMinimum);
    }


    private Date generateRandomDate(Date minimumInclusive, Date maximumInclusive) {
        long startSeconds = minimumInclusive.getTime();
        long endSeconds = maximumInclusive.getTime();
        long resultLong = (long) (startSeconds + this.random.nextFloat() * (endSeconds - startSeconds));

        return new Date(resultLong);
    }
}
