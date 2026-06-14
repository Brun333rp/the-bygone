package com.jamiedev.bygone.common.entity.ai.goal;

import com.jamiedev.bygone.common.entity.GeistEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GeistGotoLightGoal extends MoveToBlockGoal {

    protected final GeistEntity geist;

    public GeistGotoLightGoal(GeistEntity geist, double speedModifier, int searchRange) {
        super(geist, speedModifier, searchRange);
        this.geist = geist;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        this.geist.getLookControl().setLookAt(
            this.blockPos.getX() + 0.5F,
            this.blockPos.getY() + 1,
            this.blockPos.getZ() + 0.5F,
            10,
            (float)this.geist.getMaxHeadXRot()
        );
//        Vec3 vec3 = LandRandomPos.getPosTowards(this.geist, 10, 7, this.geist.position());
//        this.geist.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.6D);
        if (this.isReachedTarget()) this.nextStartTick = 10;
//        if (this.geist.getNavigation().isDone()) {
//            Vec3 vec3 = LandRandomPos.getPosTowards(this.geist, 10, 7, this.geist.gotoPosition);
//            if (vec3 != null) {
//                this.geist.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.6D);
//            }
//        }
    }
    
    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
//        int lightLevel = this.geist.level().getBrightness(LightLayer.BLOCK, this.geist.blockPosition());
        int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        int threshold = this.geist.getLightThreshold();
        boolean aboveThreshold = lightLevel > threshold || this.geist.isOnFire();
        return level.isEmptyBlock(pos.above()) && aboveThreshold;
    }

}
