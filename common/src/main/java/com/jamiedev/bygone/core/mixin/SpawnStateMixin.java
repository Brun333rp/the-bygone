package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.weather.weather_types.HauntingsCategoryHolder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin {
    @Shadow
    public abstract boolean canSpawnForCategory(MobCategory category, ChunkPos pos);

    @Inject(method = "canSpawnForCategory", at = @At("RETURN"), cancellable = true)
    private void bygone$redirectCategory(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        // stacks the count with the one for monsters so its never higher than the monster cap
        if (category.equals(HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) && cir.getReturnValue())
            cir.setReturnValue(canSpawnForCategory(MobCategory.CREATURE, pos));
    }
}
