package net.voidarkana.yetanotherfishmod.common.block.custom;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.voidarkana.yetanotherfishmod.common.block.YAFMBlocks;

import java.util.Map;

public class DuckweedBlock extends Block implements IPlantable, BonemealableBlock {
    protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;

    protected static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter((p_52346_) -> {
        return p_52346_.getKey().getAxis().isHorizontal();
    }).collect(Util.toMap());

    public static final IntegerProperty GROWTH_STAGE = IntegerProperty.create("growth_stage", 0, 2);

    public DuckweedBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false))
                .setValue(GROWTH_STAGE, Integer.valueOf(0)));
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {

        ItemStack itemstack = pPlayer.getItemInHand(pHand);

        BlockState blockstate = pLevel.getBlockState(pPos);

        if (itemstack.is(YAFMBlocks.DUCKWEED.get().asItem()) && blockstate.getValue(GROWTH_STAGE) < 2) {
            this.usePlayerItem(pPlayer, pHand, itemstack);
            int prev = blockstate.getValue(GROWTH_STAGE);
            pLevel.setBlock(pPos, pState.setValue(GROWTH_STAGE, Integer.valueOf(prev + 1)), 2);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    protected void usePlayerItem(Player pPlayer, InteractionHand pHand, ItemStack pStack) {
        if (!pPlayer.getAbilities().instabuild) {
            pStack.shrink(1);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, WEST, SOUTH, GROWTH_STAGE);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockGetter blockgetter = pContext.getLevel();

        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = pContext.getLevel().getBlockState(blockpos);

        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockState blockstate1 = blockgetter.getBlockState(blockpos1);
        BlockState blockstate2 = blockgetter.getBlockState(blockpos2);
        BlockState blockstate3 = blockgetter.getBlockState(blockpos3);
        BlockState blockstate4 = blockgetter.getBlockState(blockpos4);

        return super.getStateForPlacement(pContext)
                .setValue(NORTH, Boolean.valueOf(this.connectsTo(blockstate1, blockstate, blockstate1.isFaceSturdy(blockgetter, blockpos1, Direction.SOUTH))))
                .setValue(EAST, Boolean.valueOf(this.connectsTo(blockstate2, blockstate, blockstate2.isFaceSturdy(blockgetter, blockpos1, Direction.WEST))))
                .setValue(SOUTH, Boolean.valueOf(this.connectsTo(blockstate3, blockstate, blockstate3.isFaceSturdy(blockgetter, blockpos1, Direction.NORTH))))
                .setValue(WEST, Boolean.valueOf(this.connectsTo(blockstate4, blockstate, blockstate4.isFaceSturdy(blockgetter, blockpos1, Direction.EAST))));
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return pState.getValue(GROWTH_STAGE)<2;
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {


        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState()
                : pFacing.getAxis().getPlane() == Direction.Plane.HORIZONTAL
                ? pState.setValue(PROPERTY_BY_DIRECTION.get(pFacing), Boolean.valueOf(this.connectsTo(pFacingState, pState, pFacingState.isFaceSturdy(pLevel, pFacingPos, pFacing.getOpposite()))))
                : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return AABB;
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return mayPlaceOn(pLevel, pPos.below());
    }

    private static boolean mayPlaceOn(BlockGetter pLevel, BlockPos pPos) {
        FluidState fluidstate = pLevel.getFluidState(pPos);
        FluidState fluidstate1 = pLevel.getFluidState(pPos.above());
        return fluidstate.getType() == Fluids.WATER && fluidstate1.getType() == Fluids.EMPTY;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState, boolean pIsClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel pLevel, RandomSource pRandom, BlockPos pPos, BlockState pState) {
        int i = pState.getValue(GROWTH_STAGE);
        if (i < 2) {
            pLevel.setBlock(pPos, pState.setValue(GROWTH_STAGE, Integer.valueOf(i + 1)), 2);
        } else {
            popResource(pLevel, pPos, new ItemStack(this));
        }

    }

    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        super.entityInside(pState, pLevel, pPos, pEntity);
        if (pLevel instanceof ServerLevel && pEntity instanceof Boat) {
            pLevel.destroyBlock(new BlockPos(pPos), true, pEntity);
        }

    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return true;
    }

    @Override
    public BlockState getPlant(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != this) return defaultBlockState();
        return state;
    }

    public boolean connectsTo(BlockState pState, BlockState thisState, boolean pIsSideSolid) {
        return !isExceptionForConnection(pState)
                && (pIsSideSolid || isDuckweedConnectable(pState, thisState));
    }

    private boolean isDuckweedConnectable(BlockState pState, BlockState thisState) {

        if (pState.is(YAFMBlocks.DUCKWEED.get()) && thisState.is(YAFMBlocks.DUCKWEED.get())){
            return pState.getValue(GROWTH_STAGE).equals(thisState.getValue(GROWTH_STAGE));
        }else {
            return false;
        }

    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.WATER;
    }

}
