package com.vinzy.cataddons.mixin.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.vinzy.cataddons.features.CapeManager.customCape;

@Mixin(AbstractClientPlayerEntity.class)
@Environment(EnvType.CLIENT)
public class AbstractClientPlayerEntityMixin {
    @Inject(at = @At("RETURN"), method = "getSkin", cancellable = true)
    private void getSkinTextures(CallbackInfoReturnable<net.minecraft.entity.player.SkinTextures> cir) {
        if (((AbstractClientPlayerEntity) (Object) this).getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
            SkinTextures textures = cir.getReturnValue();
            if (customCape != null) {
                cir.setReturnValue(new SkinTextures(
                        textures.body(),
                        new AssetInfo.TextureAssetInfo(customCape, customCape),
                        new AssetInfo.TextureAssetInfo(customCape, customCape),
                        textures.model(),
                        textures.secure()
                ));
            } else {
                cir.setReturnValue(new SkinTextures(
                        textures.body(),
                        textures.cape(),
                        textures.elytra(),
                        textures.model(),
                        textures.secure()
                ));
            }
        }
    }
}
