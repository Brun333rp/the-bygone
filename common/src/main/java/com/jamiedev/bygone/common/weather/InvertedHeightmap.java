package com.jamiedev.bygone.common.weather;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

public class InvertedHeightmap extends Heightmap {
    public InvertedHeightmap(ChunkAccess chunk) {
        super(chunk, Types.MOTION_BLOCKING);
    }
}
