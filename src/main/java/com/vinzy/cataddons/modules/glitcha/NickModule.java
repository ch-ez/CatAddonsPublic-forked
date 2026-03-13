package com.vinzy.cataddons.modules.glitcha;

import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.BooleanSetting;
import com.vinzy.cataddons.modules.settings.StringSetting;
import net.minecraft.client.MinecraftClient;

public class NickModule extends Module {
    private final StringSetting nickname = register(new StringSetting("Name", "larp"));
    public final BooleanSetting censor = register(new BooleanSetting("Censor", false));
    public String username;

    public NickModule() {
        super("Nick", "Glitcha");
    }

    @Override
    protected void onEnable() {
        username = MinecraftClient.getInstance().getSession().getUsername();
    }

    public String replaceName(String string) {
        if (string != null && isEnabled()) {
            if (censor.getValue()) {
                if (username.length() <= 2) return string;
                String censored = username.substring(0, 2) + "*".repeat(username.length() - 2);
                return string.replace(username, censored);
            }
            String nick = nickname.getValue().replace("&", "§");
            return string.replace(username, nick);
        }

        return string;
    }

}
