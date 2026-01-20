package com.redstoned.pizton;

public abstract class PiztonModule implements IPiztonModule {
    @Override
    public void init() {}

    protected boolean ENABLED = false;

    @Override
    public void enable() {
        this.ENABLED = true;
    }

    @Override
    public void disable() {
        this.ENABLED = false;
    }

    public boolean enabled() {
        return this.ENABLED;
    }
}
