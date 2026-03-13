package com.vinzy.cataddons.mixin.render;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.render.HidePlayersModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    private boolean shouldRenderPlayer(Entity entity) {
        HidePlayersModule mod = (HidePlayersModule) MainClient.MODULE_MANAGER.getModuleByName("HidePlayers");
        if (!mod.isEnabled() || !(entity instanceof PlayerEntity) || entity.getUuid().version() != 4 || entity == MinecraftClient.getInstance().player) return true;
        if (mod.hideAll.getValue()) return false;
        if (MinecraftClient.getInstance().player == null) return true;
        return entity.squaredDistanceTo(MinecraftClient.getInstance().player) > (mod.distance.getValue() * mod.distance.getValue());
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!shouldRenderPlayer(entity)) cir.setReturnValue(false);
    }

}
