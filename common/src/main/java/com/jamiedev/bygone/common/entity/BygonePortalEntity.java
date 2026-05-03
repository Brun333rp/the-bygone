package com.jamiedev.bygone.common.entity;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import com.jamiedev.bygone.core.registry.BGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class BygonePortalEntity extends LivingEntity {
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(BygonePortalEntity .class, EntityDataSerializers.INT);
    private int triggerCooldown = 0;
    private boolean wasActivated = false;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState triggerAnimationState = new AnimationState();
    public AnimationState activeAnimationState = new AnimationState();

    public BygonePortalEntity(EntityType<? extends BygonePortalEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LIFETIME, 12000);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("LifeTime")) {
            this.setLifeTime(compound.getInt("LifeTime"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LifeTime", getLifeTime());
    }

    public void setLifeTime(int lifeTime) {
        this.getEntityData().set(DATA_LIFETIME, lifeTime);
    }

    public int getLifeTime() {
        return this.getEntityData().get(DATA_LIFETIME);
    }

    public ItemStack getPickResult() {
        return new ItemStack(BGItems.ARCANE_MECHANISM.get());
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            int lifetime = getLifeTime();
            if (lifetime <= 0) {
                this.discard();
                return;
            }
            setLifeTime(lifetime - 1);
        }

        if (triggerCooldown > 0) {
            if (triggerCooldown == 1) level().broadcastEntityEvent(this, (byte) 1);
            triggerCooldown--;
        }

        if (this.level().isClientSide) {
            updateAnimations();
        }

        if (!this.level().isClientSide && triggerCooldown == 0 && tickCount % 40 == 0) {
            checkForNearbyPlayers();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (this.level().isClientSide) {
            if (id == (byte) 0) {
                this.idleAnimationState.stop();
                this.activeAnimationState.stop();
                this.triggerAnimationState.start(this.tickCount);
                return;
            }
            if (id == (byte) 1) {
                this.idleAnimationState.stop();
                this.triggerAnimationState.stop();
                this.activeAnimationState.start(this.tickCount);
                return;
            }
            if (id == (byte) 2) {
                this.activeAnimationState.stop();
                this.triggerAnimationState.stop();
                this.idleAnimationState.start(this.tickCount);

                level().addParticle(ParticleTypes.EXPLOSION,
                        this.getX() + 0.5,
                        this.getY() + 0.5,
                        this.getZ() + 0.5,
                        0, 0.1, 0);

                level().playSound(this, this.blockPosition(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
                return;
            }
        }

        super.handleEntityEvent(id);
    }


    private void updateAnimations() {
        if (!triggerAnimationState.isStarted() && !activeAnimationState.isStarted()) {
            idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    private void checkForNearbyPlayers() {
        if (triggerCooldown > 0) return;

        double range = 6.0;
        AABB bounds = this.getBoundingBox().inflate(range);
        List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, bounds);

        boolean hasPlayerNearby = nearbyPlayers.stream().anyMatch(player -> player.distanceTo(this) <= range);

        if (hasPlayerNearby && !wasActivated) {
            level().broadcastEntityEvent(this, (byte) 0);
            wasActivated = true;
            triggerCooldown = 40;

            this.playSound(SoundEvents.VAULT_OPEN_SHUTTER, 1.0F, 1.0F);
            return;
        }

        if (!hasPlayerNearby && this.random.nextFloat() > 0.8) {
            if (wasActivated) {
                resetPortal();
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    private void resetPortal() {
        wasActivated = false;
        level().broadcastEntityEvent(this, (byte) 2);
    }

    private void spawnPortalParticles() {
        for (int i = 0; i < 3; i++) {
            double x = this.getX() + (this.random.nextDouble() - 0.5) * 1.5;
            double y = this.getY() + this.random.nextDouble() * 2.0;
            double z = this.getZ() + (this.random.nextDouble() - 0.5) * 1.5;

            this.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.PORTAL,
                    x, y, z,
                    (this.random.nextDouble() - 0.5) * 0.5,
                    this.random.nextDouble() * 0.5,
                    (this.random.nextDouble() - 0.5) * 0.5
            );
        }
    }


    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !this.isRemoved() && this.wasActivated) {

            ResourceKey<Level> bygone = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Bygone.MOD_ID,"bygone"));
            ResourceKey<Level> resourcekey = level().dimension() == bygone ? Level.OVERWORLD : bygone;

            ServerLevel serverlevel = level().getServer().getLevel(resourcekey);

            if (serverlevel != null) {

                WorldBorder worldborder = serverlevel.getWorldBorder();
                double d0 = DimensionType.getTeleportationScale(level().dimensionType(), serverlevel.dimensionType());
                BlockPos blockpos = worldborder.clampToBounds(player.getX() * d0, player.getY(), player.getZ() * d0);

                BlockPos finalBlockpos = findSafeTeleportPosition(serverlevel, blockpos);

                teleport(player, serverlevel, finalBlockpos);

                BygonePortalEntity newPortal = BGEntityTypes.BYGONE_PORTAL.get().create(serverlevel);

                if (newPortal != null) {
                  BlockPos spawnPos = findSafePortalSpawn(serverlevel, finalBlockpos);
                  newPortal.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                  newPortal.setLifeTime(6000);
                  serverlevel.addFreshEntity(newPortal);
                }

                player.playSound(SoundEvents.PORTAL_TRAVEL, 1.0F, 1.0F);
            }
        }
    }

    private static void teleport(Player player, ServerLevel serverlevel, BlockPos finalBlockpos) {
        player.teleportTo(serverlevel, finalBlockpos.getX() + 0.5, finalBlockpos.getY(), finalBlockpos.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot());
    }

    private BlockPos findSafeTeleportPosition(Level level, BlockPos pos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int y = pos.getY() + 50; y > pos.getY() - 50; y--) {
            mutablePos.set(pos.getX(), y, pos.getZ());
            if (level.getBlockState(mutablePos).isAir() && level.getBlockState(mutablePos.above()).isAir()) {
                return mutablePos.immutable();
            }
        }

        return pos;
    }

    private BlockPos findSafePortalSpawn(Level level, BlockPos nearPos) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int radius = 3; radius <= 10; radius++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    mutablePos.set(nearPos.getX() + x, nearPos.getY(), nearPos.getZ() + z);

                    boolean clear = true;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            BlockPos checkPos = mutablePos.offset(dx, 0, dz);
                            if (!level.getBlockState(checkPos).canBeReplaced()) {
                                clear = false;
                                break;
                            }
                            if (!level.getBlockState(checkPos.above()).canBeReplaced()) {
                                clear = false;
                                break;
                            }
                        }
                    }

                    if (clear) {
                        return mutablePos.immutable();
                    }
                }
            }
        }

        return nearPos;
    }

    private BlockPos findOrCreatePortalTarget(Level targetWorld, BlockPos origin) {
        BlockPos safeSpot = findSafeTeleportPosition(targetWorld, origin);
        if (safeSpot == null) return null;

        BygonePortalEntity targetPortal = BGEntityTypes.BYGONE_PORTAL.get().create(targetWorld);

        if (targetPortal != null) {
            targetPortal.setPos(safeSpot.getX() + 0.5, safeSpot.getY(), safeSpot.getZ() + 0.5);
            targetWorld.addFreshEntity(targetPortal);
        }

        return safeSpot;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {

    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }
}
