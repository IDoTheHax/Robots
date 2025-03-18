package net.idothehax.block;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.idothehax.block.entity.RobotBlockEntity;
import net.idothehax.RobotRegistries;
import org.jetbrains.annotations.Nullable;

public class RobotBlock extends BlockWithEntity implements PolymerTexturedBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final MapCodec<RobotBlock> CODEC = createCodec(RobotBlock::new);

    public RobotBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RobotBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : checkType(type, RobotRegistries.ROBOT_BLOCK_ENTITY,
                (world1, pos, blockState, be) -> ((RobotBlockEntity) be).tick(world1, pos, blockState));
    }

    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
            BlockEntityType<A> givenType,
            BlockEntityType<E> expectedType,
            BlockEntityTicker<? super E> ticker
    ) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected MapCodec<RobotBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.IRON_BLOCK.getDefaultState();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            RobotBlockEntity robotEntity = (RobotBlockEntity) world.getBlockEntity(pos);
            if (robotEntity != null) {
                // Toggle active state on right-click
                robotEntity.setActive(!robotEntity.isActive());

                // Send status message to player
                String status = robotEntity.isActive() ? "activated" : "deactivated";
                player.sendMessage(Text.literal("Robot " + status + "! Energy: " + robotEntity.getEnergy()), false);

                // Check both hands for redstone
                if (player.getMainHandStack().getItem() == Items.REDSTONE) {
                    robotEntity.setEnergy(robotEntity.getEnergy() + 100);
                    player.getMainHandStack().decrement(1);
                    player.sendMessage(Text.literal("Energy added! Current energy: " + robotEntity.getEnergy()), false);
                } else if (player.getOffHandStack().getItem() == Items.REDSTONE) {
                    robotEntity.setEnergy(robotEntity.getEnergy() + 100);
                    player.getOffHandStack().decrement(1);
                    player.sendMessage(Text.literal("Energy added! Current energy: " + robotEntity.getEnergy()), false);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}