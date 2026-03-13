package com.vinzy.cataddons.features;

import com.vinzy.cataddons.SharedVariables;
import com.vinzy.cataddons.features.ssidLogin.LoginScreen;
import com.vinzy.cataddons.features.ssidLogin.TokenListScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class CatAddonsScreen extends Screen {
    private final Screen parent;
    private ButtonWidget ssidButton;
    private ButtonWidget clickGuiButton;
    private ButtonWidget accountsButton;
    private ButtonWidget backButton;
    private String quote;


    public CatAddonsScreen(Screen parent) {
        super(Text.literal("CatAddons"));
        this.parent = parent;
        this.quote = SharedVariables.randomQuote();
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        this.clickGuiButton = ButtonWidget.builder(Text.literal("Click GUI Config"), button -> {
            this.client.setScreen(new ClickGui(this.client.currentScreen));
        }).dimensions(centerX - 100, startY, 200, 20).build();

        this.addDrawableChild(clickGuiButton);

        if (this.client.player == null) {
            this.ssidButton = ButtonWidget.builder(Text.literal("SSID Login"), button -> {
                this.client.setScreen(new LoginScreen(this.client.currentScreen));
            }).dimensions(centerX - 100, startY + 30, 200, 20).build();

            this.addDrawableChild(ssidButton);

            if (TokenListScreen.hasAccounts()) {
                this.accountsButton = ButtonWidget.builder(Text.literal("Accounts"), button -> {
                    this.client.setScreen(new TokenListScreen(this.client.currentScreen));
                }).dimensions(centerX - 100, startY + 60, 200, 20).build();

                this.addDrawableChild(accountsButton);
            }
        }

        this.backButton = ButtonWidget.builder(Text.literal("Back"), button -> {
            this.client.setScreen(parent);
        }).dimensions(centerX - 100, startY + 90, 200, 20).build();

        this.addDrawableChild(backButton);

    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("CatAddons"),
                this.width / 2,
                40,
                0xFFFF00FF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal(quote),
                this.width / 2,
                this.height - 14,
                0xFFFFFFFF
        );
    }
}

