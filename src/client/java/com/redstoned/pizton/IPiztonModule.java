package com.redstoned.pizton;

public interface IPiztonModule {
    void init();
    void enable();
    void disable();

    String description();
}