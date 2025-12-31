package cn.fanziyun.doubledoor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DoorPairFinder {

    public static BlockPos findMatchingDoor(Level world, BlockPos pos, BlockState state, boolean allowDifferentMaterials) {
        if (!(state.getBlock() instanceof DoorBlock)) return null;

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Enum<?> hinge = state.getValue(BlockStateProperties.DOOR_HINGE);

        /*
         * Hinge direction determines which side the pair is on:
         * LEFT hinge (ordinal 0) → pair is clockwise
         * RIGHT hinge (ordinal 1) → pair is counter-clockwise
         * This makes the two doors close toward each other.
         */
        Direction side = hinge.ordinal() == 0 ? facing.getClockWise() : facing.getCounterClockWise();
        BlockPos neighborPos = pos.relative(side);
        BlockState neighborState = world.getBlockState(neighborPos);

        if (!(neighborState.getBlock() instanceof DoorBlock)) return null;
        if (!neighborState.is(BlockTags.WOODEN_DOORS)) return null;
        if (!allowDifferentMaterials && neighborState.getBlock() != state.getBlock()) return null;

        Direction neighborFacing = neighborState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Enum<?> neighborHinge = neighborState.getValue(BlockStateProperties.DOOR_HINGE);

        if (neighborFacing == facing && neighborHinge != hinge) {
            return neighborPos;
        }

        return null;
    }
}
