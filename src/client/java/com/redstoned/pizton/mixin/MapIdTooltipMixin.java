package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.AlwaysShowMapId;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MapId.class)
public class MapIdTooltipMixin {
    @Unique
    private static final AlwaysShowMapId pizton$module = Pizton.fetchModule(AlwaysShowMapId.class);

    @Definition(id = "get", method = "Lnet/minecraft/core/component/DataComponentGetter;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")
    @Definition(id = "CUSTOM_NAME", field = "Lnet/minecraft/core/component/DataComponents;CUSTOM_NAME:Lnet/minecraft/core/component/DataComponentType;")
    @Expression("?.get(CUSTOM_NAME) == null")
    @ModifyExpressionValue(method = "addToTooltip", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean showMapIdRegardless(boolean original) {
        return pizton$module.enabled() || original;
    }

}
