package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

final class RecordFactory {
    private final Random random;
    private final IRecordFactorySettings settings;

    public RecordFactory(IRecordFactorySettings settings) {
        random = new Random(settings.getSeed());
        this.settings = settings;
    }

    public Record generateRandomTestRecord(){
        String id = generateRandomTestString(settings.getLeftCharLimit(), settings.getRightCharLimit(),
            settings.getStringMaxLength());
        float amount = generateRandomTestFloat(settings.getFloatMin(), settings.getFloatMax());
        Instant dateLowerBound = Instant.now().minus(Duration.ofDays(1000));
        Instant dateUpperBound = Instant.now();
        Date date = generateRandomTestDate(dateLowerBound, dateUpperBound);

        return new Record(id, amount, date);
    }

    private String generateRandomTestString(int leftCharLimit, int rightCharLimit, int maxLength) {
        return random.ints(leftCharLimit, rightCharLimit + 1)
                .limit(random.nextInt(maxLength))
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private float generateRandomTestFloat(float amountMinimum, float amountMaximum) {
        return amountMinimum + this.random.nextFloat() * (amountMaximum - amountMinimum);
    }


    private Date generateRandomTestDate(Instant minimumInclusive, Instant maximumInclusive) {
        long startSeconds = minimumInclusive.getEpochSecond();
        long endSeconds = maximumInclusive.getEpochSecond();
        long resultLong = (long) (startSeconds + this.random.nextFloat() * (endSeconds - startSeconds));

        return new Date(resultLong);
    }
}
