package cantwe.alldisagree.CWADForge.blocks.modification;

import cantwe.alldisagree.CWADForge.blocks.modification.entity.EnderResonatorBlockEntity;
import cantwe.alldisagree.CWADForge.registry.BlocksRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderResonatorBlock extends BlockWithEntity {

    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public EnderResonatorBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.NORTH).with(LIT, false));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnderResonatorBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnderResonatorBlockEntity) {
            player.openHandledScreen((EnderResonatorBlockEntity)blockEntity);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        if(world.getBlockEntity(pos) instanceof EnderResonatorBlockEntity be)
        {
            Inventory inventory = be.inventory;

            ItemScatterer.spawn(world, pos, inventory);
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient? checkType(type, BlocksRegistry.ENDER_RESONATOR_ENTITY, EnderResonatorBlockEntity::clientTicker) : checkType(type, BlocksRegistry.ENDER_RESONATOR_ENTITY, EnderResonatorBlockEntity::serverTicker);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
