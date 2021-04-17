package com.github.takecx.remotecontrollermod.blocks;

import com.github.takecx.remotecontrollermod.AgentEntity;
import com.github.takecx.remotecontrollermod.Remotecontrollermod;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StarBlock extends Block {
    // property
    public static final IntegerProperty PROPERTY_POINT = IntegerProperty.create("point", 0, 10);;
    public static final BooleanProperty PROPERTY_ISCLEARED = BooleanProperty.create("iscleared");

    public StarBlock() {
        super(Block.Properties.create(Material.AIR)
                .hardnessAndResistance(2.0f, 3.0f)
                .setLightLevel((state) -> {
                    return 15;
                })
                .sound(SoundType.METAL));
        this.setRegistryName(new ResourceLocation(Remotecontrollermod.MODID, "star_block"));
        this.setDefaultState(this.stateContainer.getBaseState().with(PROPERTY_POINT, Integer.valueOf(1)).with(PROPERTY_ISCLEARED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(PROPERTY_POINT, PROPERTY_ISCLEARED);
    }

    // property
    public void SetPoint(BlockState state, int pointIn){
        state.with(PROPERTY_POINT,pointIn);
    }

    public void SetIsCleared(BlockState state, boolean isClearedIn){
        state.with(PROPERTY_ISCLEARED,isClearedIn);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityCollision(state, worldIn, pos, entityIn);

        if (entityIn instanceof AgentEntity){
            ((AgentEntity) entityIn).score += state.get(PROPERTY_POINT);
            worldIn.removeBlock(pos,false);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onBlockAdded(state,worldIn,pos,oldState,isMoving);

        this.SetPoint(state,1);
    }
}