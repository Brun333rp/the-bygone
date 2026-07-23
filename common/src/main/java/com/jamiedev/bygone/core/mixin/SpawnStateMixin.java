package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.common.weather.weather_types.HauntingsCategoryHolder;
import com.jamiedev.bygone.core.extension.LivingEntityExtension;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LocalMobCapCalculator;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.SpawnState.class)
public abstract class SpawnStateMixin {

    @Shadow
    @Final
    private LocalMobCapCalculator localMobCapCalculator;

    // PROBABLY fine to ignore for category since otherwise they spawn with other category???
    @Inject(method = "canSpawnForCategory", at = @At("RETURN"), cancellable = true)
    private void bygone$redirectCategory(MobCategory category, ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!category.equals(HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY)) return;
        // ignore local cap??? i dont know man this is annoying
        cir.setReturnValue(this.localMobCapCalculator.canSpawn(category, pos));
    }

    @Inject(method = "afterSpawn", at = @At("TAIL"))
    private void bygone$prepareHauntingMobRise(Mob mob, ChunkAccess chunk, CallbackInfo ci) {
        if (mob.getType().getCategory() != HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) return;
        if (!HauntingsCategoryHolder.checkHauntingsActive(mob.level())) return;

        // add a raycast downwards here tomorrow
        mob.moveTo(mob.getX(), mob.getY() - mob.getBbHeight(), mob.getZ(), mob.getYRot(), mob.getXRot());
        ((LivingEntityExtension) mob).bygone$startHauntingsRise();
    }
}
