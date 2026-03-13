package com.vinzy.cataddons.features;

import com.vinzy.cataddons.MainClient;
import com.vinzy.cataddons.modules.misc.CapeModule;
import com.vinzy.cataddons.modules.Module;
import com.vinzy.cataddons.modules.settings.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.*;

public class ClickGui extends Screen {

    private static final int PW       = 112;  // panel width
    private static final int HEADER_H = 14;   // category header height
    private static final int MOD_H    = 11;   // module row height
    private static final int SET_H    = 11;   // setting label row height
    private static final int SLD_H    = 3;    // slider bar height
    private static final int PAD      = 5;    // inner horizontal padding


    private static final int C_BG        = 0xED111111;
    private static final int C_BORDER    = 0xFF252525;
    private static final int C_HDR_BG    = 0xFF1a1a1a;
    private static final int C_HDR_TXT   = 0xFFFFFFFF;
    private static final int C_HDR_BTN   = 0xFF888888;
    // Module rows
    private static final int C_ON        = 0xFF55FF55;
    private static final int C_OFF       = 0xFF888888;
    private static final int C_HOVER     = 0x0BFFFFFF;
    private static final int C_ACCENT    = 0xFF55FF55; // left bar when enabled
    // Settings
    private static final int C_SET_BG    = 0xCC0a0a0a;
    private static final int C_SET_LINE  = 0xFF1e1e1e;
    private static final int C_LBL       = 0xFF666666;
    private static final int C_GREEN     = 0xFF55FF55;
    private static final int C_RED       = 0xFFFF5555;
    private static final int C_BLUE      = 0xFF55CCFF;
    private static final int C_ORANGE    = 0xFFFFAA33;
    private static final int C_SLD_BG    = 0xFF2a2a2a;
    private static final int C_SLD_FG    = 0xFF55FF55;
    private static final int C_STR_FOCUS = 0xFF334433; // focused string row bg
    private static final int C_CURSOR    = 0xFF55FF55; // text cursor color

    private final List<Panel> panels = new ArrayList<>();
    private final Screen parent;

    private Panel  focusedPanel = null;
    private String focusedMod   = null;
    private String focusedSet   = null;

    private void clearFocus() {
        focusedPanel = null;
        focusedMod   = null;
        focusedSet   = null;
    }

    private void setFocus(Panel panel, String modName, String setName) {
        focusedPanel = panel;
        focusedMod   = modName;
        focusedSet   = setName;
    }

    private boolean isFocused(Panel panel, String modName, String setName) {
        return panel == focusedPanel
                && modName.equals(focusedMod)
                && setName.equals(focusedSet);
    }

    private class Panel {
        final String category;
        final List<Module> modules;
        int x, y;

        boolean dragging;
        int dox, doy; // drag offset

        final Set<String> expanded = new HashSet<>();

        String sliderMod, sliderSet;

        Panel(String category, List<Module> modules, int x, int y) {
            this.category = category;
            this.modules  = modules;
            this.x = x;
            this.y = y;
        }

        int height() {
            int h = HEADER_H;
            for (Module m : modules) {
                h += MOD_H;
                if (expanded.contains(m.getName()) && !m.getSettings().isEmpty())
                    h += settBlockH(m.getSettings());
            }
            return h;
        }

        int settBlockH(List<Setting<?>> ss) {
            int h = 4;
            for (Setting<?> s : ss)
                h += (s instanceof FloatSetting) ? SET_H + SLD_H + 3 : SET_H;
            return h;
        }

        void draw(DrawContext ctx, int mx, int my) {
            if (dragging) { x = mx - dox; y = my - doy; }

            int pw = PW, ph = height();

            for (int i = 4; i >= 1; i--) {
                int a = (int)(255 * 0.055f * i);
                ctx.fill(x - i, y - i, x + pw + i, y + ph + i, a << 24);
            }

            ctx.fill(x, y, x + pw, y + ph, C_BG);
            ctx.fill(x,        y,        x + pw,     y + 1,      C_BORDER);
            ctx.fill(x,        y + ph-1, x + pw,     y + ph,     C_BORDER);
            ctx.fill(x,        y,        x + 1,      y + ph,     C_BORDER);
            ctx.fill(x + pw-1, y,        x + pw,     y + ph,     C_BORDER);

            ctx.fill(x, y, x + pw, y + HEADER_H, C_HDR_BG);
            ctx.fill(x, y + HEADER_H - 1, x + pw, y + HEADER_H, C_BORDER);
            ctx.drawTextWithShadow(textRenderer, category, x + PAD, y + (HEADER_H - 7) / 2, C_HDR_TXT);
            ctx.drawText(textRenderer, "—", x + pw - 10, y + (HEADER_H - 7) / 2, C_HDR_BTN, false);

            int ry = y + HEADER_H;
            for (Module mod : modules) {
                boolean hov = mx >= x && mx < x + pw && my >= ry && my < ry + MOD_H;
                boolean on  = mod.isEnabled();
                boolean exp = expanded.contains(mod.getName());

                if (hov) ctx.fill(x, ry, x + pw, ry + MOD_H, C_HOVER);
                if (on)  ctx.fill(x, ry, x + 2,  ry + MOD_H, C_ACCENT);

                int tx = x + PAD + (on ? 2 : 0);
                if (on) ctx.drawTextWithShadow(textRenderer, mod.getName(), tx, ry + 2, C_ON);
                else    ctx.drawText(textRenderer, mod.getName(), tx, ry + 2, C_OFF, false);

                if (!mod.getSettings().isEmpty())
                    ctx.drawText(textRenderer, exp ? "▾" : "▸", x + pw - 9, ry + 2, C_LBL, false);

                ry += MOD_H;

                if (exp) {
                    List<Setting<?>> ss = mod.getSettings();
                    if (!ss.isEmpty()) {
                        int bh = settBlockH(ss);
                        ctx.fill(x, ry, x + pw, ry + bh, C_SET_BG);
                        ctx.fill(x,        ry,        x + 1,      ry + bh, C_SET_LINE);
                        ctx.fill(x + pw-1, ry,        x + pw,     ry + bh, C_SET_LINE);
                        ctx.fill(x,        ry + bh-1, x + pw,     ry + bh, C_SET_LINE);

                        int sy = ry + 3;
                        int sx = x + PAD + 2;
                        int sw = pw - PAD * 2 - 2;

                        for (Setting<?> s : ss) {
                            if (s instanceof FloatSetting fs) {
                                float val = fs.getValue();
                                String lbl = s.getName() + ": ";
                                String vs  = (val == (int) val) ? String.valueOf((int) val) : String.format("%.1f", val);
                                ctx.drawText(textRenderer, lbl, sx, sy, C_LBL, false);
                                ctx.drawText(textRenderer, vs, sx + textRenderer.getWidth(lbl), sy, C_BLUE, false);

                                int barY = sy + SET_H;
                                float pct = Math.max(0, Math.min(1, (val - fs.getMin()) / (fs.getMax() - fs.getMin())));
                                int fw = (int)(sw * pct);
                                ctx.fill(sx, barY, sx + sw, barY + SLD_H, C_SLD_BG);
                                if (fw > 0) ctx.fill(sx, barY, sx + fw, barY + SLD_H, C_SLD_FG);
                                sy += SET_H + SLD_H + 3;

                            } else if (s instanceof BooleanSetting bs) {
                                boolean v = bs.getValue();
                                String lbl = s.getName() + ": ";
                                ctx.drawText(textRenderer, lbl, sx, sy, C_LBL, false);
                                ctx.drawText(textRenderer, v ? "true" : "false",
                                        sx + textRenderer.getWidth(lbl), sy, v ? C_GREEN : C_RED, false);
                                sy += SET_H;

                            } else if (s instanceof ModeSetting ms) {
                                String lbl = s.getName() + ": ";
                                ctx.drawText(textRenderer, lbl, sx, sy, C_LBL, false);
                                ctx.drawText(textRenderer, ms.getValue(),
                                        sx + textRenderer.getWidth(lbl), sy, C_ORANGE, false);
                                sy += SET_H;

                            } else if (s instanceof StringSetting ss2) {
                                boolean focused = isFocused(this, mod.getName(), s.getName());
                                String lbl = s.getName() + ": ";
                                String val = ss2.getValue();

                                int valueX = sx + textRenderer.getWidth(lbl);
                                int textEndX = valueX + textRenderer.getWidth(val);

                                if (focused) {
                                    int bgRight = textEndX + (val.isEmpty() ? 4 : 2);
                                    ctx.fill(sx - 1, sy - 1, bgRight + 1, sy + SET_H - 1, C_STR_FOCUS);
                                }

                                ctx.drawText(textRenderer, lbl, sx, sy, C_LBL, false);

                                if (focused) {
                                    ctx.drawText(textRenderer, val, valueX, sy, C_HDR_TXT, false);
                                    long now = System.currentTimeMillis();
                                    if ((now / 500) % 2 == 0) {
                                        ctx.drawText(textRenderer, "|", textEndX, sy, C_CURSOR, false);
                                    }
                                } else {
                                    int clipRight = sx + sw;
                                    ctx.enableScissor(valueX, sy - 1, clipRight, sy + SET_H);
                                    ctx.drawText(textRenderer, val, valueX, sy, C_HDR_TXT, false);
                                    ctx.disableScissor();
                                }

                                sy += SET_H;
                            }
                        }
                        ry += bh;
                    }
                }
            }
        }

        boolean mouseClicked(int mx, int my, int btn) {
            if (mx < x || mx >= x + PW) return false;

            if (my >= y && my < y + HEADER_H && btn == 0) {
                dragging = true; dox = mx - x; doy = my - y;
                return true;
            }

            int ry = y + HEADER_H;
            for (Module mod : modules) {
                // Module row
                if (my >= ry && my < ry + MOD_H) {
                    if (btn == 0) { mod.toggle(); return true; }
                    if (btn == 1 && !mod.getSettings().isEmpty()) {
                        toggleExpand(mod.getName()); return true;
                    }
                    return false;
                }
                ry += MOD_H;

                // Settings block
                if (expanded.contains(mod.getName())) {
                    List<Setting<?>> ss = mod.getSettings();
                    if (!ss.isEmpty()) {
                        int bh = settBlockH(ss);
                        if (my >= ry && my < ry + bh) {
                            int sy = ry + 3;
                            int sx = x + PAD + 2;
                            int sw = PW - PAD * 2 - 2;
                            for (Setting<?> s : ss) {
                                if (s instanceof FloatSetting fs) {
                                    int barY = sy + SET_H;
                                    if (my >= sy && my <= barY + SLD_H + 2 && btn == 0) {
                                        float pct = (float)(mx - sx) / sw;
                                        fs.setValue(fs.getMin() + pct * (fs.getMax() - fs.getMin()));
                                        sliderMod = mod.getName(); sliderSet = fs.getName();
                                        clearFocus();
                                        return true;
                                    }
                                    sy += SET_H + SLD_H + 3;
                                } else if (s instanceof BooleanSetting bs) {
                                    if (my >= sy && my < sy + SET_H) {
                                        if (btn == 0 || btn == 1) { bs.toggle(); clearFocus(); return true; }
                                    }
                                    sy += SET_H;
                                } else if (s instanceof ModeSetting ms) {
                                    if (my >= sy && my < sy + SET_H) {
                                        if (btn == 0 || btn == 1) {
                                            List<String> opts = ms.getOptions();
                                            int idx = opts.indexOf(ms.getValue());
                                            if (btn == 0) idx = (idx + 1) % opts.size();
                                            else          idx = (idx - 1 + opts.size()) % opts.size();
                                            ms.setValue(opts.get(idx));
                                            clearFocus();
                                            return true;
                                        }
                                    }
                                    sy += SET_H;
                                } else if (s instanceof StringSetting) {
                                    if (my >= sy && my < sy + SET_H && btn == 0) {
                                        // toggle focus: clicking the same field again keeps it focused
                                        if (isFocused(this, mod.getName(), s.getName())) {
                                            clearFocus();
                                        } else {
                                            setFocus(this, mod.getName(), s.getName());
                                        }
                                        return true;
                                    }
                                    sy += SET_H;
                                }
                            }
                            clearFocus();
                            return true;
                        }
                        ry += bh;
                    }
                }
            }
            return false;
        }

        void mouseDragged(int mx, int my) {
            if (sliderMod != null) {
                Module mod = MainClient.MODULE_MANAGER.getModuleByName(sliderMod);
                if (mod != null) {
                    Setting<?> s = mod.getSettingByName(sliderSet);
                    if (s instanceof FloatSetting fs) {
                        int sx = x + PAD + 2, sw = PW - PAD * 2 - 2;
                        float pct = (float)(mx - sx) / sw;
                        fs.setValue(fs.getMin() + pct * (fs.getMax() - fs.getMin()));
                    }
                }
            }
        }

        void mouseReleased() { dragging = false; sliderMod = null; sliderSet = null; }

        void toggleExpand(String name) {
            if (!expanded.add(name)) expanded.remove(name);
        }
    }

    public ClickGui(Screen parent) {
        super(Text.literal("ClickGUI"));
        //this fix is like super duper stupid but i guess bro
        if (this.getClass().isInstance(parent)) {
            this.parent = null;
        } else {
            this.parent = parent;
        }
    }

    @Override
    protected void init() {
        panels.clear();

        Map<String, List<Module>> cats = new LinkedHashMap<>();
        for (Module m : MainClient.MODULE_MANAGER.getModules()) {
            cats.computeIfAbsent(m.getCategory(), k -> new ArrayList<>()).add(m);
        }

        int col = 0;
        for (Map.Entry<String, List<Module>> e : cats.entrySet()) {
            panels.add(new Panel(e.getKey(), e.getValue(), 6 + col * (PW + 3), 6));
            col++;
        }

        Module capeModule = MainClient.MODULE_MANAGER.getModuleByName("CustomCape");
        if (capeModule instanceof CapeModule cm) {
            var rm = MinecraftClient.getInstance().getResourceManager();
            CapeManager.reload(rm);
            cm.refreshOptions();
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, 0x99000000);
        Panel top = null;
        for (Panel p : panels) if (p.dragging) top = p;
        for (Panel p : panels) if (p != top) p.draw(ctx, mouseX, mouseY);
        if (top != null) top.draw(ctx, mouseX, mouseY);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        int mx = (int) click.x(), my = (int) click.y(), btn = click.button();
        for (int i = panels.size() - 1; i >= 0; i--) {
            if (panels.get(i).mouseClicked(mx, my, btn)) {
                panels.add(panels.remove(i)); // bring to front
                return true;
            }
        }
        clearFocus();
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        panels.forEach(p -> p.mouseDragged((int) click.x(), (int) click.y()));
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseReleased(Click click) {
        panels.forEach(Panel::mouseReleased);
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(net.minecraft.client.input.KeyInput input) {
        if (focusedMod != null && focusedSet != null) {
            if (input.isEscape()) {
                clearFocus();
                return true;
            }

            Module mod = MainClient.MODULE_MANAGER.getModuleByName(focusedMod);
            if (mod != null) {
                Setting<?> s = mod.getSettingByName(focusedSet);
                if (s instanceof StringSetting ss) {
                    String cur = ss.getValue();

                    if (input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE) {
                        if (!cur.isEmpty()) {
                            ss.setValue(cur.substring(0, cur.length() - 1));
                        }
                        return true;
                    }

                    if (input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
                            || input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER
                            || input.getKeycode() == org.lwjgl.glfw.GLFW.GLFW_KEY_TAB) {
                        clearFocus();
                        return true;
                    }

                    return true;
                }
            }
            clearFocus();
        }

        if (input.isEscape()) { this.close(); return true; }
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(net.minecraft.client.input.CharInput input) {
        // Route printable characters to the focused string setting
        if (focusedMod != null && focusedSet != null) {
            Module mod = MainClient.MODULE_MANAGER.getModuleByName(focusedMod);
            if (mod != null) {
                Setting<?> s = mod.getSettingByName(focusedSet);
                if (s instanceof StringSetting ss) {
                    if (input.isValidChar()) {
                        ss.setValue(ss.getValue() + input.asString());
                    }
                    return true;
                }
            }
            clearFocus();
        }
        return super.charTyped(input);
    }

    @Override
    public void close() {
        ConfigManager.save();
        assert this.client != null;
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() { return false; }
}