package org.unique_events_queue.tests;

/**
 * The purpose of this interface is to provide an organized way to inject factory settings.
 */
interface IRecordFactorySettings {
    long getSeed();
    int getLeftCharLimit();
    int getRightCharLimit();
    int getStringMaxLength();
    float getFloatMin();
    float getFloatMax();
}
