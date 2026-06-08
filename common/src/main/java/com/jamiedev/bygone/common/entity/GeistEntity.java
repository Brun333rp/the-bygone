package com.jamiedev.bygone.common.entity;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.jamiedev.bygone.common.entity.ai.goal.GeistGotoLightGoal;
import com.jamiedev.bygone.core.init.JamiesModTag;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GeistEntity extends Monster implements FlyingAnimal
{
    public static final EntityDataAccessor<Integer> LIGHT_THRESHOLD = SynchedEntityData.defineId(GeistEntity.class, EntityDataSerializers.INT);
    EntityDimensions dimensions;
    public Vec3 gotoPosition;

    public GeistEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.dimensions = entityType.getDimensions();
        this.xpReward = 5;
        this.moveControl = new FlyingMoveControl(this, 35, false);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 18.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MAX_HEALTH, 16.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new GeistGotoLightGoal(this, 5, 8));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, HauntEntity.class, 16.0F, (double)1.0F,
                1.5));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(8, new WraithEntity.WraithWanderGoal(this, 0.6));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, WraithEntity.class).setAlertOthers());
        this.targetSelector.addGoal(
                2,
                new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300)
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LIGHT_THRESHOLD, 1);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("LightThreshold", this.getLightThreshold());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setLightThreshold(compoundTag.getInt("LightThreshold"));
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    public boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level) {
            @Override
            public void tick() {
                super.tick();
            }

            @Override
            public boolean isStableDestination(@NotNull BlockPos pos) {
                return this.level.getBlockState(pos).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void aiStep() {

            BlockPos blockPos = this.blockPosition();
            RandomSource randomSource = this.getRandom();
            BlockPos pos = blockPos.offset(randomSource.nextInt(20) - 10, randomSource.nextInt(6) - 3, randomSource.nextInt(20) - 10);

            if (this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) > this.getLightThreshold() || this.isOnFire()) {
                this.gotoPosition = Vec3.atBottomCenterOf(pos);
            }

        super.aiStep();
    }

    public int getLightThreshold() {
        return this.entityData.get(LIGHT_THRESHOLD);
    }

    public void setLightThreshold(int threshold) {
        this.entityData.set(LIGHT_THRESHOLD, threshold);
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    public static boolean canSpawn(EntityType<? extends Mob> type, LevelAccessor level, MobSpawnType reason, BlockPos blockPos, RandomSource random) {
        return level.getBlockState(blockPos.below()).is(JamiesModTag.WRAITH_SPAWNABLE_ON);
    }
}
