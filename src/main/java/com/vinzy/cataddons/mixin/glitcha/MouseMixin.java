package com.vinzy.cataddons.mixin.glitcha;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.render.FreeLookModule;
import com.vinzy.cataddons.modules.render.FreecamModule;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow private double cursorDeltaX;
    @Shadow private double cursorDeltaY;

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void onUpdateMouse(CallbackInfo ci) {
        FreecamModule freecam = (FreecamModule) MainClient.MODULE_MANAGER.getModuleByName("Freecam");
        if (freecam != null && freecam.isEnabled()) {
            freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
            ci.cancel();
            return;
        }

        FreeLookModule freeLook = (FreeLookModule) MainClient.MODULE_MANAGER.getModuleByName("FreeLook");
        if (freeLook == null || !freeLook.isPlayerMode()) return;

        freeLook.cameraYaw += (float)(cursorDeltaX * freeLook.sensitivity.getValue() * 0.15);
        freeLook.smoothYaw  = freeLook.cameraYaw;
        ci.cancel();
    }
}