package cn.fanziyun.doubledoor.mixin;

import cn.fanziyun.doubledoor.DoorPairFinder;
import cn.fanziyun.doubledoor.DoubledoorMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin into DoorBlock.
 * Injects sync logic after player interaction (useWithoutItem) and
 * neighbor updates (neighborChanged).
 * <p>
 * Injects at @RETURN so the door has already toggled before we sync the pair.
 */
@Mixin(DoorBlock.class)
public class DoorBlockMixin {

    /**
     * Inject after player right-clicks the door (empty hand or any item).
     * 1.21.1 Mojmap name; door state has already changed at this point.
     */
    @Inject(method = "useWithoutItem", at = @At("RETURN"))
    private void onUseWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (world.isClientSide()) return;
        if (player.isShiftKeyDown() && !DoubledoorMod.CONFIG.allowSneaking) return;
        syncPairDoor(world, pos);
    }

    /**
     * Inject after player right-clicks the door while holding an item.
     * Fallback for MC versions using the "use" signature instead of "useWithoutItem".
     * Harmless warning on 1.21.1 — kept for cross-version compatibility.
     */
    @Inject(method = "use", at = @At("RETURN"))
    private void onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (world.isClientSide()) return;
        if (player.isShiftKeyDown() && !DoubledoorMod.CONFIG.allowSneaking) return;
        syncPairDoor(world, pos);
    }

    /**
     * Inject after redstone / neighbor changes.
     * Controlled by config.enableRedstone (default off).
     */
    @Inject(method = "neighborChanged", at = @At("RETURN"))
    private void onNeighborUpdate(BlockState state, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
        if (world.isClientSide()) return;
        if (!DoubledoorMod.CONFIG.enableRedstone) return;
        syncPairDoor(world, pos);
    }

    /**
     * Core sync logic: find the paired door and mirror the open state.
     * Only operates on the LOWER half (upper half has no hinge data).
     *
     * @param world the world
     * @param pos   trigger position (may be upper or lower half)
     */
    private static void syncPairDoor(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof DoorBlock)) return;
        if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER) return;

        BlockPos pairPos = DoorPairFinder.findMatchingDoor(world, pos, state, DoubledoorMod.CONFIG.allowDifferentMaterials);
        if (pairPos == null) return;

        BlockState pairState = world.getBlockState(pairPos);
        boolean pairOpen = pairState.getValue(BlockStateProperties.OPEN);
        boolean thisOpen = state.getValue(BlockStateProperties.OPEN);

        if (pairOpen != thisOpen) {
            // flag 3 = UPDATE_NEIGHBORS | UPDATE_CLIENTS
            world.setBlock(pairPos, pairState.setValue(BlockStateProperties.OPEN, thisOpen), 3);
        }
    }
}
