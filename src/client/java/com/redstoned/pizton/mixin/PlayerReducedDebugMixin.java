package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.NeverReduceDebugInfo;
import com.redstoned.pizton.module.PersistSpecMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerReducedDebugMixin {
    //todo - there could be module collisions later, fix me :c
    @Unique
    private static final NeverReduceDebugInfo pizton$module = Pizton.fetchModule(NeverReduceDebugInfo.class);

    @ModifyReturnValue(method = "isReducedDebugInfo", at = @At("RETURN"))
    boolean neverReduceDebugInfo(boolean original) {
        return pizton$module.enabled() ? false : original;
    }
}
