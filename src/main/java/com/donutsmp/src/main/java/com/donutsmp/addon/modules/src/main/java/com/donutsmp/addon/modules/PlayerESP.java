package com.donutsmp.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class PlayerESP extends Module {
    public PlayerESP() {
        super("player-esp", "Draws boxes and nametags on other players (Good for DonutSMP)");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> render = sgGeneral.add(BoolSetting.builder()
        .name("render")
        .defaultValue(true)
        .build());

    public final Setting<SettingColor> color = sgGeneral.add(ColorSetting.builder()
        .name("color")
        .defaultValue(new SettingColor(255, 50, 200))
        .build());

    public final Setting<Boolean> box = sgGeneral.add(BoolSetting.builder()
        .name("box")
        .description("Draw 3D box around players")
        .defaultValue(true)
        .build());

    public final Setting<Boolean> nametag = sgGeneral.add(BoolSetting.builder()
        .name("nametag")
        .description("Show player name above head")
        .defaultValue(true)
        .build());

    public final Setting<Double> thickness = sgGeneral.add(DoubleSetting.builder()
        .name("thickness")
        .defaultValue(1.8)
        .min(0.5)
        .max(4.0)
        .build());

    @Override
    public void onRender3D() {
        if (!render.get() || mc.world == null) return;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isInvisible()) continue;

            Box box3d = player.getBoundingBox().expand(0.1);

            if (box.get()) {
                RenderUtils.drawBoxOutline(box3d, color.get(), thickness.get().floatValue());
            }

            if (nametag.get()) {
                RenderUtils.drawText(
                    player.getName().getString(),
                    player.getX(),
                    player.getY() + 2.2,
                    player.getZ(),
                    color.get().getPacked(),
                    true
                );
            }
        }
    }
}
