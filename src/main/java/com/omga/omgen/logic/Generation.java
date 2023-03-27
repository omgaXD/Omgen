package com.omga.omgen.logic;

import com.omga.omgen.resources.OmgenReloadListener;
import com.omga.omgen.util.ItemOrTagKey;
import com.omga.omgen.util.RandomCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class Generation {
    public static final Direction[] xzAxisDirections = new Direction[]{Direction.EAST, Direction.SOUTH, Direction.NORTH, Direction.WEST};
    public static List<FluidState> getFluidStatesAroundAndAbove(LevelAccessor level, BlockPos origin) {
        ArrayList<FluidState> states = new ArrayList<>();
        // Going through all possible fluid flow directions as stated by vanilla LiquidBlock class.
        for (Direction dir : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
            // Getting the opposite of direction because fluids flow DOWN, so we wanna get direction of the UPPER guy. That's also how vanilla does it.
            BlockPos otherFluidBP = origin.relative(dir.getOpposite());
            FluidState fs = level.getFluidState(otherFluidBP);
            if (fs.isEmpty()) {
                continue;
            }
            states.add(fs);
        }
        return states;
    }
    public static List<FluidState> getFluidStatesAroundButNotAbove(LevelAccessor level, BlockPos origin) {
        ArrayList<FluidState> states = new ArrayList<>();
        // Going through all directions that are not up or down
        for (Direction dir : xzAxisDirections) {
            BlockPos otherFluidBP = origin.relative(dir);
            FluidState fs = level.getFluidState(otherFluidBP);
            if (fs.isEmpty()) {
                continue;
            }
            states.add(fs);
        }
        return states;
    }
    public static List<BlockState> getBlockStatesAround(LevelAccessor level, BlockPos origin) {
        ArrayList<BlockState> states = new ArrayList<>();
        // Going through all directions that are not up or down
        for (Direction dir : xzAxisDirections) {
            BlockPos neighbourBP = origin.relative(dir);
            BlockState bs = level.getBlockState(neighbourBP);
            states.add(bs);
        }
        return states;
    }


    public static BlockState generateAt(LevelAccessor level, BlockPos origin) {
        // if it's Neigh or Don't care
        for (var ge : OmgenReloadListener.entries.values()) {
            if (ge.condition.getContext().pos != GenerationCondition.Context.PositionOfTheOtherFluid.Replace) {
                if (ge.condition.getInitiatingFluid().test(level.getFluidState(origin)) && ge.condition.getCondition().test(level, origin)) {
                    return ge.pool.next();
                }
            }
            // If it's Replace or Don't care
            if (ge.condition.getContext().pos != GenerationCondition.Context.PositionOfTheOtherFluid.Neighbour) {
                if (ge.condition.getInitiatingFluid().test(level.getFluidState(origin.above())) && ge.condition.getCondition().test(level, origin)) {
                    return ge.pool.next();
                }
            }
        }
        return null;
    }
}
