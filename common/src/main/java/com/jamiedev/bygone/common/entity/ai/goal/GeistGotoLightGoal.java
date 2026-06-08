package com.jamiedev.bygone.common.entity.ai.goal;
import com.jamiedev.bygone.common.entity.GeistEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
public class GeistGotoLightGoal extends MoveToBlockGoal
{
    GeistEntity geist;

    public GeistGotoLightGoal(PathfinderMob mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange);
        geist = (GeistEntity)mob;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        this.geist.gotoPosition = null;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.geist.getNavigation().isDone()) {
            Vec3 vec3 = LandRandomPos.getPosTowards(this.geist, 10, 7, this.geist.gotoPosition);
            if (vec3 != null) {
                this.geist.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.6D);
            }
        }
    }
    
    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        return level.isEmptyBlock(pos.above()) && (this.geist.level().getBrightness(LightLayer.BLOCK, this.geist.blockPosition()) > this.geist.getLightThreshold()
                || this.geist.isOnFire());
    }
}
