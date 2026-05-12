package net.swill.autooakfarm;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FarmLogic {

    public static void tick(ServerWorld world, ServerPlayerEntity player) {
        if (player == null) return;

        BlockPos playerPos = player.getBlockPos();
        BlockPos startPos = playerPos.add(0, -1, 0);

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                BlockPos checkPos = startPos.add(x, 0, z);

                BlockState state = world.getBlockState(checkPos);

                // 1. Убрать листву мотыгой
                if (state.isOf(Blocks.OAK_LEAVES)) {
                    useHoeOnLeaves(world, player, checkPos);
                    return;
                }

                // 2. Срубить дуб топором
                if (isOakLog(state)) {
                            useAxeOnLog(world, player, checkPos);
                    return;
                }

                // 3. Если есть саженец и он не вырос -> костная мука
                if (state.getBlock() instanceof SaplingBlock && ((SaplingBlock)state.getBlock()).getType().getName().equals("oak")) {
                    if (!isFullyGrownTreeNearby(world, checkPos)) {
                        applyBoneMeal(world, player, checkPos);
                        return;
                    }
                    return;
                }

                // 4. Посадить саженец на землю
                if (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT) || state.isOf(Blocks.COARSE_DIRT)) {
                    plantSapling(world, player, checkPos.up());
                    return;
                }
            }
        }
    }

    private static boolean isOakLog(BlockState state) {
        return state.isOf(Blocks.OAK_LOG) || state.isOf(Blocks.STRIPPED_OAK_LOG);
    }

    private static boolean isFullyGrownTreeNearby(ServerWorld world, BlockPos saplingPos) {
        for (int y = 1; y <= 6; y++) {
            BlockPos check = saplingPos.up(y);
            if (world.getBlockState(check).isOf(Blocks.OAK_LOG)) {
                return true;
            }
        }
        return false;
    }

    private static void useHoeOnLeaves(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        ItemStack hoe = new ItemStack(Items.DIAMOND_HOE);
        player.setStackInHand(Hand.MAIN_HAND, hoe);
        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), player.getHorizontalFacing(), pos, false);
        hoe.useOnWorld(world, player, Hand.MAIN_HAND, hitResult);
        world.breakBlock(pos, true, player);
        player.getInventory().removeStack(player.getInventory().selectedSlot, 1);
        world.playSound(null, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1f, 1f);
    }

    private static void useAxeOnLog(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        player.setStackInHand(Hand.MAIN_HAND, axe);
        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), player.getHorizontalFacing(), pos, false);
        axe.useOnWorld(world, player, Hand.MAIN_HAND, hitResult);
        world.breakBlock(pos, true, player);
        player.getInventory().removeStack(player.getInventory().selectedSlot, 1);
        world.playSound(null, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1f, 1f);
    }

    private static void applyBoneMeal(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        ItemStack boneMeal = new ItemStack(Items.BONE_MEAL);
        player.setStackInHand(Hand.MAIN_HAND, boneMeal);
        if (BoneMealItem.useOnFertilizable(boneMeal, world, pos, player)) {
            boneMeal.decrement(1);
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 1f, 1f);
        }
        player.getInventory().removeStack(player.getInventory().selectedSlot, 1);
    }

    private static void plantSapling(ServerWorld world, ServerPlayerEntity player, BlockPos pos) {
        ItemStack sapling = new ItemStack(Items.OAK_SAPLING);
        player.setStackInHand(Hand.MAIN_HAND, sapling);
        BlockHitResult hitResult = new BlockHitResult(Vec3d.ofCenter(pos), player.getHorizontalFacing(), pos, false);
        sapling.useOnWorld(world, player, Hand.MAIN_HAND, hitResult);
        player.getInventory().removeStack(player.getInventory().selectedSlot, 1);
    }
}
