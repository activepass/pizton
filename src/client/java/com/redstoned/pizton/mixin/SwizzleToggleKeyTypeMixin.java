package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.MouseToggleCompat;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(ToggleKeyMapping.class)
public class SwizzleToggleKeyTypeMixin {
	@Unique
	private static final MouseToggleCompat pizton$module = Pizton.fetchModule(MouseToggleCompat.class);

	@Definition(id = "key", field = "Lnet/minecraft/client/ToggleKeyMapping;key:Lcom/mojang/blaze3d/platform/InputConstants$Key;")
	@Definition(id = "getType", method = "Lcom/mojang/blaze3d/platform/InputConstants$Key;getType()Lcom/mojang/blaze3d/platform/InputConstants$Type;")
	@Definition(id = "KEYSYM", field = "Lcom/mojang/blaze3d/platform/InputConstants$Type;KEYSYM:Lcom/mojang/blaze3d/platform/InputConstants$Type;")
	@Expression("this.key.getType() == KEYSYM")
	@ModifyExpressionValue(method = "shouldRestoreStateOnScreenClosed", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean init(boolean original) {
		return pizton$module.enabled() || original;
	}
}