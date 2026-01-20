package com.redstoned.pizton.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.module.MouseToggleCompat;
import net.minecraft.client.ToggleKeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ToggleKeyMapping.class)
public class SwizzleToggleKeyTypeMixin {
	@WrapOperation(
			method = "shouldRestoreStateOnScreenClosed",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/InputConstants$Key;getType()Lcom/mojang/blaze3d/platform/InputConstants$Type;"
			)
	)
	private InputConstants.Type init(InputConstants.Key instance, Operation<InputConstants.Type> original) {
		// Allow any key type to pass type check
		// Probably has some wack edge case (?)
		return Pizton.fetchModule(MouseToggleCompat.class).enabled() ? InputConstants.Type.KEYSYM : original.call(instance);
	}
}