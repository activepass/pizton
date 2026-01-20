package com.redstoned.pizton.machinery;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

public abstract class StaticModule extends PiztonModule {
    public StaticModule() {
        super("Static");
    }

    @Override
    public TextColor color() {
        return TextColor.fromLegacyFormat(ChatFormatting.AQUA);
    }
}
