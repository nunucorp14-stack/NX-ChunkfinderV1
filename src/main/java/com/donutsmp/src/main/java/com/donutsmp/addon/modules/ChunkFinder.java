package com.donutsmp.addon.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class ChunkFinder extends Module {
    public ChunkFinder() {
        super("chunk-finder", "Highlights chunks with lots of storage blocks - Perfect for DonutSMP");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Settings
    public final Setting<Integer> radius = sgGeneral.add(IntSetting.builder()
        .name("radius")
        .description("How many chunks around you to scan")
        .defaultValue(8)
        .min(4)
        .max(20)
        .build());

    public final Setting<Integer> minBlocks = sgGeneral.add(IntSetting.builder()
        .name("min-blocks")
        .description("Minimum blocks to flag a chunk")
        .defaultValue(12)
        .min(5)
        .build());

    public final Setting<List<Block>> targetBlocks = sgGeneral.add(BlockListSetting.builder()
        .name("target-blocks")
        .description("Blocks to count in chunks")
        .defaultValue(Arrays.asList(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST,
            Blocks.SHULKER_BOX, Blocks.FURNACE, Blocks.BLAST_FURNACE,
            Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.ANVIL,
            Blocks.ENCHANTING_TABLE, Blocks.BEACON
        ))
        .build());

    public final Setting<Boolean> render = sgRender.add(BoolSetting.builder()
        .name("render")
        .defaultValue(true)
        .build());

    public final Setting<SettingColor> color = sgRender.add(ColorSetting.builder()
        .name("color")
        .defaultValue(new SettingColor(255, 0, 255, 100))
        .build());

    public final Setting<Boolean> filled = sgRender.add(BoolSetting.builder()
        .name("filled")
        .defaultValue(true)
        .build());

    private final Map<ChunkPos, Integer> flaggedChunks = new HashMap<>();

    @Override
    public void onActivate() {
        flaggedChunks.clear();
    }

    @Override
    public void onDeactivate() {
        flaggedChunks.clear();
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        ChunkPos playerChunk = mc.player.getChunkPos();
        flaggedChunks.clear();

        for (int x = -radius.get(); x <= radius.get(); x++) {
            for (int z = -radius.get(); z <= radius.get(); z++) {
                ChunkPos chunkPos = new ChunkPos(playerChunk.x + x, playerChunk.z + z);
                int count = countTargetBlocks(chunkPos);

                if (count >= minBlocks.get()) {
                    flaggedChunks.put(chunkPos, count);
                }
            }
        }
    }

    private int countTargetBlocks(ChunkPos chunkPos) {
        int count = 0;
        List<Block> targets = targetBlocks.get();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = mc.world.getBottomY(); y < mc.world.getTopY(); y += 3) { // faster scan
                    BlockPos pos = new BlockPos(chunkPos.getStartX() + x, y, chunkPos.getStartZ() + z);
                    if (targets.contains(mc.world.getBlockState(pos).getBlock())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void onRender3D() {
        if (!render.get()) return;

        for (ChunkPos chunk : flaggedChunks.keySet()) {
            Box box = new Box(
                chunk.getStartX(), mc.world.getBottomY(), chunk.getStartZ(),
                chunk.getStartX() + 16, mc.world.getTopY() + 4, chunk.getStartZ() + 16
            );

            RenderUtils.drawBoxSides(box, color.get(), filled.get());
            RenderUtils.drawBoxOutline(box, color.get(), 1.8f);
        }
    }

    @Override
    public String getInfoString() {
        return flaggedChunks.size() + " chunks";
    }
}
