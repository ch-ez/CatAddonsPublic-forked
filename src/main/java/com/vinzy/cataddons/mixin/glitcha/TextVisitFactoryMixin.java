package com.vinzy.cataddons.mixin.glitcha;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.glitcha.NickModule;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {
    @ModifyArg(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
            ordinal = 0),
            method = {
                    "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
            index = 0)
    private static String adjustText(String text) {
        if (MainClient.MODULE_MANAGER == null) return text;
        NickModule mod = (NickModule) MainClient.MODULE_MANAGER.getModuleByName("Nick");
        if (mod == null) return text;
        return mod.replaceName(text);
    }
}
