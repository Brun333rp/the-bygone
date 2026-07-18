package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.jamiedev.bygone.common.weather.weather_types.HauntingsCategoryHolder;
import com.jamiedev.bygone.core.extension.LivingEntityExtension;
import com.jamiedev.bygone.core.init.JamiesModTag;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.NaturalSpawner.spawnCategoryForChunk;
import static net.minecraft.world.level.NaturalSpawner.spawnCategoryForPosition;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {

    private static final @Unique ResourceLocation HAUNTINGS_LOCATION = Bygone.id("hauntings");

    @Shadow
    private static BlockPos getRandomPosWithin(Level level, LevelChunk chunk) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

//    @Inject(method = "isValidSpawnPostitionForType", at = @At("HEAD"), cancellable = true)
//    private static void bygone$hauntingsAllowSpawning(
//        ServerLevel level, MobCategory category, StructureManager structureManager,
//        ChunkGenerator generator, MobSpawnSettings.SpawnerData data, BlockPos.MutableBlockPos pos,
//        double distance, CallbackInfoReturnable<Boolean> cir
//    ) {
//        if (!data.type.getCategory().equals(HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY)) return;
//
//        // only if the hauntings are actually active do mob spawns bypass required biome spawns
//        BygoneWeather weather = BygoneWeather.getOrDefault(level);
//        if (weather == null) return;
//
//        weather.getWeatherType(HAUNTINGS_LOCATION).ifPresent(hauntings -> {
//            boolean noCollision = level.noCollision(
//                data.type.getSpawnAABB(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F)
//            );
//            if (hauntings.isActive() && noCollision) cir.setReturnValue(true);
//        });
//    }

    @Inject(method = "getRandomSpawnMobAt", at = @At("HEAD"), cancellable = true)
    private static void bygone$injectMobPool(
        ServerLevel level, StructureManager structureManager, ChunkGenerator generator,
        MobCategory category, RandomSource random, BlockPos pos,
        CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> cir
    ) {
        if (!category.equals(HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY)) return;

        List<EntityType<?>> mobs = BuiltInRegistries.ENTITY_TYPE.stream()
            .filter(type -> type.getCategory() == category).toList();
        EntityType<?> chosen = mobs.get(random.nextInt(mobs.size()));

        cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
            chosen, 5, 1, 1
        )));
    }

    @Inject(method = "spawnForChunk", at = @At("TAIL"))
    private static void bygone$spawnForHauntings(
        ServerLevel level, LevelChunk chunk,
        NaturalSpawner.SpawnState spawnState,
        boolean spawnFriendlies, boolean spawnMonsters,
        boolean forcedDespawn, CallbackInfo ci
    ) {
        BygoneWeather weather = BygoneWeather.getOrDefault(level);
        if (weather == null) return;

        // deliberately spawn the mobs using the correct category during hauntings
        weather.getWeatherType(HAUNTINGS_LOCATION).ifPresent(hauntings -> {
           if (hauntings.isActive()
               && spawnState.canSpawnForCategory(HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY, chunk.getPos())
               && spawnState.canSpawnForCategory(MobCategory.CREATURE, chunk.getPos())
           ) {
               // naive currently but checks will come in later
               BlockPos.MutableBlockPos blockpos = getRandomPosWithin(level, chunk).mutable();
//               while (blockpos.getY() > level.getMinBuildHeight()) {
//                   BlockState state = level.getBlockState(blockpos);
//                   if (!state.getCollisionShape(level, blockpos).isEmpty()) {
//                       Bygone.LOGGER.info("phasing mob");
//                       break;
//                   }
//                   blockpos.setY(blockpos.getY() - 1);
//               }
//               if (blockpos.getY() <= level.getMinBuildHeight()) return;

               spawnCategoryForPosition(
                   HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY,
                   level, chunk, blockpos, (var1, var2, var3)
                       -> level.getRandom().nextDouble() < 0.24d,
                   (mob, var2) -> {}
               );
           }
        });
    }
}
