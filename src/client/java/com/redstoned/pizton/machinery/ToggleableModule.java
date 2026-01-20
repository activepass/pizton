package com.redstoned.pizton.machinery;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public abstract class ToggleableModule extends PiztonModule {
    public ToggleableModule() {
        super("Toggle");
    }

    protected boolean ENABLED = false;

    public void enable() {
        this.ENABLED = true;
    }

    public void disable() {
        this.ENABLED = false;
    }

    public boolean enabled() {
        return this.ENABLED;
    }

    @Override
    public TextColor color() {
        return TextColor.fromLegacyFormat(this.ENABLED ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}
