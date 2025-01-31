package me.steven.carrier;

import me.steven.carrier.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HolderInteractCallback {

    public static final HolderInteractCallback INSTANCE = new HolderInteractCallback();

    private HolderInteractCallback() {
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction hitDirection, boolean canPickup) {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;
        if (!world.canPlayerModifyAt(player, pos)) return ActionResult.PASS;
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getCarryingData();
        if (canPickup && carrying == null && CarriableRegistry.INSTANCE.contains(block) && blockState.getHardness(world, pos) > -1) {
            if (world.isClient && !Carrier.canCarry(Registries.BLOCK.getId(block))) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(block);
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(Registries.BLOCK.getId(block))) {
                ActionResult actionResult = carriable.tryPickup(carrier, world, pos, null);
                if (actionResult.isAccepted()) return actionResult;
            }
        }

        if (carrying != null) {
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
            if (!world.isClient && carriable != null && world.getBlockState(pos.offset(hitDirection)).getMaterial().isReplaceable()) {
                ActionResult actionResult = carriable.tryPlace(carrier, world, new CarriablePlacementContext(carrier, carriable, pos.offset(hitDirection), hitDirection, player.getHorizontalFacing()));
                if (actionResult.isAccepted()) return actionResult;
            }
        }

        return ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, boolean canPickup) {
        if (hand == Hand.OFF_HAND || !world.canPlayerModifyAt(player, entity.getBlockPos())) return ActionResult.PASS;
        BlockPos pos = entity.getBlockPos();
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getCarryingData();
        if (canPickup && carrying == null && CarriableRegistry.INSTANCE.contains(entity.getType())) {
            if (world.isClient && !Carrier.canCarry(Registries.ENTITY_TYPE.getId(entity.getType()))) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(entity.getType());
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(Registries.ENTITY_TYPE.getId(entity.getType()))) {
                ActionResult actionResult = carriable.tryPickup(carrier, world, pos, entity);
                if (actionResult.isAccepted()) return actionResult;
            }
        }
        if (carrying == null) return ActionResult.PASS;
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
        if (!world.isClient && carriable != null) {
            ActionResult actionResult = carriable.tryPlace(carrier, world, new CarriablePlacementContext(carrier, carriable, pos, player.getHorizontalFacing(), player.getHorizontalFacing()));
            if (actionResult.isAccepted()) return actionResult;
        }
        return ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return interact(player, world, hand, hitResult.getBlockPos(), hitResult.getSide(), !Carrier.CONFIG.doGlovesExist()
                && Carrier.isHoldingKey(player) && player.getStackInHand(hand).isEmpty());
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity) {
        return interact(player, world, hand, entity, !Carrier.CONFIG.doGlovesExist()
                && Carrier.isHoldingKey(player)
                && player.getStackInHand(hand).isEmpty());
    }

    public ActionResult interact(ItemUsageContext context) {
        return interact(context.getPlayer(), context.getWorld(), context.getHand(), context.getBlockPos(), context.getSide(), true);
    }
}
