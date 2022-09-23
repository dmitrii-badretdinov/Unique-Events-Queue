package org.example;

class RecordFactorySettings implements IRecordFactorySettings {
    private static final long SEED = 182376523;

    // 97 is a, 122 is z.
    private static final int LEFT_CHAR_LIMIT = 97;
    private static final int RIGHT_CHAR_LIMIT = 122;
    private static final int MAX_STRING_LENGTH = 50;

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
}
