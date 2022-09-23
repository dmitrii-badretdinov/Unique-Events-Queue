package org.example;

/**
 * The purpose of this interface is to provide an organized way to inject factory settings.
 */
interface IRecordFactorySettings {
    long getSeed();
    int getLeftCharLimit();
    int getRightCharLimit();
    int getStringMaxLength();
}
