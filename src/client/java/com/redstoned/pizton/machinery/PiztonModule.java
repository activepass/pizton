package com.redstoned.pizton.machinery;

import net.minecraft.network.chat.TextColor;

public abstract class PiztonModule {
    public final String kind;
    public PiztonModule(String kind) {
        this.kind = kind;
    }

    //todo: maybe as constructor field
    public abstract String description();
    public abstract TextColor color();

    public void init() {}
}