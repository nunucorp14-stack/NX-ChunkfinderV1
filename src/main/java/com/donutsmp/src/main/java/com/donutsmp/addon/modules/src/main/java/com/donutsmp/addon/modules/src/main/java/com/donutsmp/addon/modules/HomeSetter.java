package com.donutsmp.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class HomeSetter extends Module {
    public HomeSetter() {
        super("home-setter", "Set homes & auto delete Home 1 when Page Up is pressed");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<String> defaultHomeName = sgGeneral.add(StringSetting.builder()
        .name("default-home-name")
        .description("Default name when setting home")
        .defaultValue("home1")
        .build());

    public final Setting<Boolean> renderHomes = sgGeneral.add(BoolSetting.builder()
        .name("render-homes")
        .description("Show home locations in world")
        .defaultValue(true)
        .build());

    public final Setting<SettingColor> homeColor = sgGeneral.add(ColorSetting.builder()
        .name("home-color")
        .defaultValue(new SettingColor(0, 255, 255))
        .build());

    private final Map<String, BlockPos> homes = new HashMap<>();

    @Override
    public void onActivate() {
        homes.clear();
    }

    @Override
    public void onTick() {
        // Delete Home 1 when Page Up (PGUP) is pressed
        if (mc.currentScreen == null) {
            if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_PAGE_UP) == GLFW.GLFW_PRESS) {
                if (homes.containsKey("home1")) {
                    homes.remove("home1");
                    ChatUtils.sendMsg("§c[HomeSetter] Home 1 has been deleted!");
                }
            }
        }
    }

    // You can call this from commands or keybinds later
    public void setHome(String name) {
        if (mc.player != null) {
            homes.put(name.toLowerCase(), mc.player.getBlockPos());
            ChatUtils.sendMsg("§a[HomeSetter] Home '" + name + "' set!");
        }
    }

    @Override
    public void onRender3D() {
        if (!renderHomes.get()) return;

        for (Map.Entry<String, BlockPos> entry : homes.entrySet()) {
            Vec3d pos = entry.getValue().toCenterPos();
            RenderUtils.drawText(
                "§b" + entry.getKey().toUpperCase(),
                pos.x, pos.y + 1.8, pos.z,
                homeColor.get().getPacked(),
                true
            );
        }
    }
}
