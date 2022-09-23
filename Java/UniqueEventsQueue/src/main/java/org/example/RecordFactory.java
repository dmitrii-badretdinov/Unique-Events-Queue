package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

final class RecordFactory {
    private final Random random;

    public RecordFactory(long seed) {
        random = new Random(seed);
    }

    public Record generateRandomTestRecord(){
        // 97 is a, 122 is z.
        String id = generateRandomTestString(97, 122, 50);
        float amount = generateRandomTestFloat((float) 0.001, (float) 10000000);
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
