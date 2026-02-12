package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.machinery.PiztonModule;
import com.redstoned.pizton.machinery.StaticModule;
import com.redstoned.pizton.module.PersistSpecMenu;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpectatorGui.class)
public class PreventSpecMenuFade {
    @Unique
    private static final PersistSpecMenu pizton$module = Pizton.fetchModule(PersistSpecMenu.class);

    @ModifyReturnValue(at = @At("RETURN"), method = "getHotbarAlpha()F")
    public float prevent_fade(float original) {
        return pizton$module.enabled() ? 1f : original;
    }
}
