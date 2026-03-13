package com.vinzy.cataddons.mixin.entity;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract UUID getUuid();

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void forceGlow(CallbackInfoReturnable<Boolean> cir) {
        Module esp = MainClient.MODULE_MANAGER.getModuleByName("esp");
        if (esp.isEnabled()) {
            if (((Entity)(Object)this) instanceof PlayerEntity && (boolean) esp.getSettingByName("Players").getValue()) {
                if(this.getUuid().version() != 4) cir.setReturnValue(false); else cir.setReturnValue(true);
            }
            if (((Entity) (Object) this) instanceof MobEntity && (boolean)  esp.getSettingByName("Mobs").getValue()) {
                cir.setReturnValue(true);
            }
            if(((Entity) (Object) this) instanceof ItemEntity && (boolean)  esp.getSettingByName("Items").getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getTeamColorValue", at = {@At("RETURN")}, cancellable = true)
    public void changeColorValue(CallbackInfoReturnable<Integer> cir) {
        Module esp = MainClient.MODULE_MANAGER.getModuleByName("esp");
        if (esp.isEnabled()) {
            if (((Entity)(Object)this) instanceof PlayerEntity) {
                cir.setReturnValue(0xFFF800F8);
            }
            if (((Entity)(Object)this) instanceof MobEntity) {
                cir.setReturnValue(0xFFD68542);
            }
            if (((Entity)(Object)this) instanceof ItemEntity) {
                cir.setReturnValue(0xFFFFFFFF);
            }
        }
    }
}