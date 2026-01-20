package com.redstoned.pizton;

public interface IPiztonModule {
    void register();
    void enable();
    void disable();

    String description();
}