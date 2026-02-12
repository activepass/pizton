package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.AlwaysShowMapId;
import com.redstoned.pizton.module.MouseToggleCompat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(MapId.class)
public class MapIdTooltipMixin {
    @Unique
    private static final AlwaysShowMapId pizton$module = Pizton.fetchModule(AlwaysShowMapId.class);

    @Inject(method = "addToTooltip", at=@At(value = "INVOKE", ordinal=0, target = "Lnet/minecraft/core/component/DataComponentGetter;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    public void inject_new_mapid_call(Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag, DataComponentGetter dataComponentGetter, CallbackInfo ci) {
        if (pizton$module.enabled()) {
            consumer.accept(Component.translatable("filled_map.id", ((MapId)(Object)this).id()).withStyle(ChatFormatting.GRAY));
        }
    }

    // at this ordinal, T is always Component
    //todo(update): this might change in the future
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = "addToTooltip", at = @At(value = "INVOKE", ordinal=1, target = "Lnet/minecraft/core/component/DataComponentGetter;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    public <T> T prevent_old_mapid_call(@Nullable T original) {
        return pizton$module.enabled() ? (T) Component.empty() : original;
    }
}
