package org.unique_events_queue;

class RecordFactorySettings implements IRecordFactorySettings {
    private static final long SEED = 182376523;

    // 97 is a, 122 is z.
    private static final int LEFT_CHAR_LIMIT = 97;
    private static final int RIGHT_CHAR_LIMIT = 122;
    private static final int MAX_STRING_LENGTH = 50;
    private static final float FLOAT_MIN = (float) 0.001;
    private static final float FLOAT_MAX = (float) 10000000;

    public long getSeed() {
        return SEED;
    }

    public int getLeftCharLimit() {
        return LEFT_CHAR_LIMIT;
    }

    public int getRightCharLimit() {
        return RIGHT_CHAR_LIMIT;
    }

    public int getStringMaxLength() {
        return MAX_STRING_LENGTH;
    }

    public float getFloatMin() {
        return FLOAT_MIN;
    }

    public float getFloatMax() {
        return FLOAT_MAX;
    }
}
