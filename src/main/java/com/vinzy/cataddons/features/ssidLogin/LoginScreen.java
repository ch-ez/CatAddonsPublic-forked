package com.vinzy.cataddons.features.ssidLogin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class LoginScreen extends Screen {
    private TextFieldWidget sessionField;
    private ButtonWidget loginButton;
    private ButtonWidget restoreButton;
    private Text currentTitle;

    private final Screen parent;

    public LoginScreen(Screen parent) {
        super(Text.literal("SSID Login"));
        this.currentTitle = Text.literal("Input Session ID").formatted(Formatting.LIGHT_PURPLE);
        this.parent = parent;
    }


    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.sessionField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY,
                200,
                20,
                Text.literal("Session Input")
        );

        this.sessionField.setMaxLength(32767);
        this.sessionField.setText("");
        this.sessionField.setFocused(true);

        this.addSelectableChild(this.sessionField);

        this.loginButton = ButtonWidget.builder(
                Text.literal("Login"),
                button -> {
                    String sessionInput = this.sessionField.getText().trim();

                    if (!sessionInput.isEmpty()) {
                        try {
                            String[] sessionInfo = SessionAPI.getProfileInfo(sessionInput);
                            SessionManager.setSession(
                                    SessionManager.createSession(
                                            sessionInfo[0],
                                            sessionInfo[1],
                                            sessionInput
                                    )
                            );

                            this.currentTitle = Text.literal("Logged in as: " + sessionInfo[0])
                                    .formatted(Formatting.GREEN);

                            this.restoreButton.active = true;

                        } catch (IOException | RuntimeException e) {
                            this.currentTitle = Text.literal("Invalid Session ID")
                                    .formatted(Formatting.RED);
                        }
                    } else {
                        this.currentTitle = Text.literal("Session ID cannot be empty")
                                .formatted(Formatting.RED);
                    }
                }
        ).dimensions(centerX - 100, centerY + 25, 97, 20).build();

        this.addDrawableChild(this.loginButton);

        this.restoreButton = ButtonWidget.builder(
                Text.literal("Restore"),
                button -> {
                    SessionManager.restoreSession();
                    this.currentTitle = Text.literal("Restored original session")
                            .formatted(Formatting.GREEN);

                    this.loginButton.active = true;
                    this.restoreButton.active = false;
                }
        ).dimensions(centerX + 3, centerY + 25, 97, 20).build();

        this.addDrawableChild(this.restoreButton);

        ButtonWidget backButton = ButtonWidget.builder(
                Text.literal("Back"),
                button -> {
                    assert this.client != null;
                    this.client.setScreen(
                            parent
                    );
                }
        ).dimensions(centerX - 100, centerY + 50, 200, 20).build();

        this.addDrawableChild(backButton);

        if (SessionManager.currentSession.equals(SessionManager.originalSession)) {
            this.restoreButton.active = false;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.sessionField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.currentTitle,
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFFFF
        );
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.sessionField.keyPressed(input) || this.sessionField.isActive()) {
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.sessionField.charTyped(input)) {
            return true;
        }
        return super.charTyped(input);
    }
}
