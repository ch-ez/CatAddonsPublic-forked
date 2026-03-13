package com.vinzy.cataddons.mixin.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.vinzy.cataddons.MainClient;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    private float getGamma(float original) {
        if (MainClient.MODULE_MANAGER.getModuleByName("fullbright").isEnabled()) {
            return 1600.0f;
        }
        return original;
    }
}
