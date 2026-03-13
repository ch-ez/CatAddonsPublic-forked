package com.vinzy.cataddons.mixin.screen;

import com.vinzy.cataddons.features.CatAddonsScreen;
import com.vinzy.cataddons.features.ssidLogin.LoginScreen;
import com.vinzy.cataddons.features.ssidLogin.SessionAPI;
import com.vinzy.cataddons.features.ssidLogin.SessionManager;
import com.vinzy.cataddons.features.ssidLogin.TokenListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Unique
    private static Boolean isSessionValid = null;

    @Unique
    private static boolean hasValidationStarted = false;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addButton(CallbackInfo ci) {

        int centerX = this.width / 2;
        int y = this.height / 4;

        /*
        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("SSID Login"),
                        button -> {
                            assert this.client != null;
                            this.client.setScreen(new LoginScreen(MinecraftClient.getInstance().currentScreen));
                        }
                ).dimensions(centerX - 100, y, 200, 20).build()
        );
        */

        if (TokenListScreen.hasAccounts()) {
            this.addDrawableChild(
                    ButtonWidget.builder(
                            Text.literal("CatAddons"),
                            button -> {
                                assert this.client != null;
                                this.client.setScreen(new CatAddonsScreen(MinecraftClient.getInstance().currentScreen));
                            }
                    ).dimensions(centerX - 100, y, 200, 20).build()
            );
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        super.render(context, mouseX, mouseY, delta);

        String username = SessionManager.getUsername();

        if (isSessionValid == null && !hasValidationStarted) {
            hasValidationStarted = true;

            new Thread(() ->
                    isSessionValid = SessionAPI.validateSession(
                            this.client.getSession().getAccessToken()
                    ),
                    "SessionValidationThread"
            ).start();
        }

        Text status;

        if (isSessionValid == null) {
            status = Text.literal("[... Validating]")
                    .formatted(Formatting.GRAY);
        } else if (isSessionValid) {
            status = Text.literal("[Valid]")
                    .formatted(Formatting.GREEN);
        } else {
            status = Text.literal("[Invalid]")
                    .formatted(Formatting.RED);
        }

        Text display = Text.literal("User: ")
                .append(Text.literal(username).formatted(Formatting.AQUA))
                .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                .append(status);

        context.drawText(this.textRenderer, display, 5, 10, -1, false);
    }
}
