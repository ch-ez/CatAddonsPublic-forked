package com.vinzy.cataddons.features.ssidLogin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TokenListScreen extends Screen {
    private final Screen parent;
    private final List<AccountEntry> accounts = new ArrayList<>();
    private final List<AccountEntry> filteredAccounts = new ArrayList<>();
    private int scrollOffset = 0;
    private TextFieldWidget searchField;
    private Text statusMessage = Text.literal("");

    private static final int ROW_HEIGHT = 24;
    private static final int START_Y = 60;
    private static final int MAX_VISIBLE = 16;

    public TokenListScreen(Screen parent) {
        super(Text.literal("Session Token Login"));
        this.parent = parent;
        loadAccounts();
    }

    public static boolean hasAccounts() {
        TokenListScreen temp = new TokenListScreen(null);
        return !temp.accounts.isEmpty();
    }

    private int visibleRows() {
        return Math.min(MAX_VISIBLE, Math.max(1, (this.height - 100) / ROW_HEIGHT));
    }

    private void loadAccounts() {
        accounts.clear();

        String os = System.getProperty("os.name", "").toLowerCase();
        List<LauncherSource> sources = new ArrayList<>();

        // prism
        File prism = resolvePath(os, "PrismLauncher");
        if (prism != null && prism.exists()) sources.add(new LauncherSource("Prism", prism));

        // multimc
        File multimc = resolvePath(os, "MultiMC");
        if (multimc != null && multimc.exists()) sources.add(new LauncherSource("MultiMC", multimc));

        for (LauncherSource src : sources) {
            try (FileReader reader = new FileReader(src.file)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray arr = root.getAsJsonArray("accounts");
                if (arr == null) continue;

                for (JsonElement el : arr) {
                    JsonObject acc = el.getAsJsonObject();
                    JsonObject profile = acc.has("profile") ? acc.getAsJsonObject("profile") : null;
                    JsonObject ygg = acc.has("ygg") ? acc.getAsJsonObject("ygg") : null;

                    String name = (profile != null && profile.has("name"))
                            ? profile.get("name").getAsString() : "Unknown";
                    String token = (ygg != null && ygg.has("token"))
                            ? ygg.get("token").getAsString() : null;

                    if (token != null && !token.isEmpty()) {
                        accounts.add(new AccountEntry(name, token, src.name));
                    }
                }
            } catch (Exception ignored) {}
        }

        applyFilter();
    }

    private File resolvePath(String os, String launcherName) {
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            return appData != null ? new File(appData, launcherName + "/accounts.json") : null;
        } else if (os.contains("mac")) {
            return new File(System.getProperty("user.home"), "Library/Application Support/" + launcherName + "/accounts.json");
        } else {
            return new File(System.getProperty("user.home"), ".local/share/" + launcherName + "/accounts.json");
        }
    }

    private void applyFilter() {
        filteredAccounts.clear();
        String query = (searchField != null) ? searchField.getText().toLowerCase() : "";
        for (AccountEntry e : accounts) {
            if (query.isEmpty() || e.name.toLowerCase().contains(query) || e.launcher.toLowerCase().contains(query)) {
                filteredAccounts.add(e);
            }
        }
        scrollOffset = Math.min(scrollOffset, Math.max(0, filteredAccounts.size() - visibleRows()));
    }

    @Override
    protected void init() {
        int searchWidth = Math.min(200, this.width - 20);
        searchField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - searchWidth / 2, 36,
                searchWidth, 16,
                Text.literal("Search")
        );
        searchField.setMaxLength(100);
        searchField.setPlaceholder(Text.literal("search accounts...").formatted(Formatting.DARK_GRAY));
        this.addSelectableChild(searchField);

        rebuildList();
    }

    private void rebuildList() {
        this.clearChildren();
        this.addSelectableChild(searchField);

        int visibleRows = visibleRows();
        int loginBtnWidth = 50;

        for (int i = 0; i < visibleRows && (i + scrollOffset) < filteredAccounts.size(); i++) {
            int index = i + scrollOffset;
            AccountEntry entry = filteredAccounts.get(index);
            int y = START_Y + i * ROW_HEIGHT;

            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("Login"),
                    btn -> attemptLogin(entry, btn)
            ).dimensions(this.width - loginBtnWidth - 10, y, loginBtnWidth, 20).build());
        }

        // back button
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Back"),
                btn -> MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    private void attemptLogin(AccountEntry entry, ButtonWidget btn) {
        statusMessage = Text.literal("Checking token...").formatted(Formatting.YELLOW);
        btn.active = false;

        Thread.ofVirtual().start(() -> {
            try {
                String[] info = SessionAPI.getProfileInfo(entry.token);
                SessionManager.setSession(
                        SessionManager.createSession(info[0], info[1], entry.token)
                );
                MinecraftClient.getInstance().execute(() -> {
                    statusMessage = Text.literal("Logged in as: " + info[0]).formatted(Formatting.GREEN);
                    btn.active = true;
                });
            } catch (Exception e) {
                MinecraftClient.getInstance().execute(() -> {
                    statusMessage = Text.literal("Invalid token for " + entry.name).formatted(Formatting.RED);
                    btn.active = true;
                });
            }
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, filteredAccounts.size() - visibleRows());
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) verticalAmount));
        rebuildList();
        return true;
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (searchField != null && searchField.keyPressed(input)) {
            applyFilter();
            scrollOffset = 0;
            rebuildList();
            return true;
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        if (searchField != null && searchField.charTyped(input)) {
            applyFilter();
            scrollOffset = 0;
            rebuildList();
            return true;
        }
        return super.charTyped(input);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xFF101010);
        super.render(context, mouseX, mouseY, delta);
        searchField.render(context, mouseX, mouseY, delta);

        // title
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Session Token Login"), this.width / 2, 10, 0xFFFFFFFF);

        // subtitle
        if (!accounts.isEmpty()) {
            String sub = filteredAccounts.size() + "/" + accounts.size() + " accounts";
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(sub).formatted(Formatting.DARK_GRAY), this.width / 2, 22, 0xFFFFFFFF);
        }

        // status message
        if (!statusMessage.getString().isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, statusMessage, this.width / 2, this.height - 42, 0xFFFFFFFF);
        }

        if (filteredAccounts.isEmpty()) {
            String msg = accounts.isEmpty() ? "no accounts found" : "no matches for \"" + searchField.getText() + "\"";
            context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(msg).formatted(Formatting.RED), this.width / 2, this.height / 2, 0xFFFFFFFF);
            return;
        }

        int visibleRows = visibleRows();
        for (int i = 0; i < visibleRows && (i + scrollOffset) < filteredAccounts.size(); i++) {
            int index = i + scrollOffset;
            AccountEntry entry = filteredAccounts.get(index);
            int y = START_Y + i * ROW_HEIGHT;

            // row bg
            int rowColor = (index % 2 == 0) ? 0x30FFFFFF : 0x15FFFFFF;
            context.fill(5, y - 2, this.width - 5, y + 22, rowColor);

            // name
            context.drawTextWithShadow(this.textRenderer, Text.literal(entry.name).formatted(Formatting.AQUA), 15, y + 3, 0xFFFFFFFF);
            // launcher tag
            context.drawTextWithShadow(this.textRenderer, Text.literal("[" + entry.launcher + "]").formatted(Formatting.DARK_GRAY), 15, y + 13, 0xFFFFFFFF);
        }

        // scroll bar
        if (filteredAccounts.size() > visibleRows) {
            int totalHeight = visibleRows * ROW_HEIGHT;
            int barHeight = Math.max(10, totalHeight * visibleRows / filteredAccounts.size());
            int maxScroll = filteredAccounts.size() - visibleRows;
            int barY = START_Y + (totalHeight - barHeight) * scrollOffset / Math.max(1, maxScroll);
            context.fill(this.width - 4, START_Y, this.width - 1, START_Y + totalHeight, 0x40FFFFFF);
            context.fill(this.width - 4, barY, this.width - 1, barY + barHeight, 0xFFAAAAAA);
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    private record AccountEntry(String name, String token, String launcher) {}
    private record LauncherSource(String name, File file) {}
}