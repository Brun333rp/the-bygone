package com.jamiedev.bygone.common.block.fluids;

import com.jamiedev.bygone.common.block.LithoFluidBlock;
import com.jamiedev.bygone.core.init.JamiesModTag;
import com.jamiedev.bygone.core.registry.BGBlocks;
import com.jamiedev.bygone.core.registry.BGFluids;
import com.jamiedev.bygone.core.registry.BGItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.*;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class LithoFluid extends FlowingFluid {

    public LithoFluid() {}

    @Nullable
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.DRIPPING_HONEY;
    }


    @Override
    public Fluid getFlowing() {
        return BGFluids.LITHO_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return BGFluids.LITHO_STILL.get();
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return level.getGameRules().getBoolean(GameRules.RULE_WATER_SOURCE_CONVERSION);
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockentity);
    }

    @Override
    protected int getSlopeFindDistance(LevelReader levelReader) {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader levelReader) {
        return 1;
    }

    @Override
    public Item getBucket() {
        return null;//BGItems.LITHO_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(JamiesModTag.LITHO);
    }

    @Override
    public int getTickDelay(LevelReader levelReader) {
        return 0;
    }

    @Override
    public int getAmount(FluidState fluidState) {
        return 0;
    }

    public BlockState createLegacyBlock(FluidState state) {
        return (BlockState) BGBlocks.LITHO.get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public boolean isSame(Fluid fluid) {
        return fluid == BGFluids.LITHO_STILL || fluid == BGFluids.LITHO_FLOWING;
    }

    protected boolean isRandomlyTicking() {
        return true;
    }

    protected float getExplosionResistance() {
        return 100.0F;
    }

    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_AXOLOTL);
    }

    public static class Flowing extends LithoFluid {
        public Flowing() {
        }
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }
        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends LithoFluid {
        public Source() {
        }
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
