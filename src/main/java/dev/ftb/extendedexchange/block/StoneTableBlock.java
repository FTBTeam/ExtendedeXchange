package dev.ftb.extendedexchange.block;

import dev.ftb.extendedexchange.menu.StoneTableMenu;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class StoneTableBlock extends Block {
    public static final VoxelShape[] SHAPE = new VoxelShape[]{
            box(0, 0, 0, 16, 4, 16),
            box(0, 12, 0, 16, 16, 16),
            box(0, 0, 0, 16, 16, 4),
            box(0, 0, 12, 16, 16, 16),
            box(0, 0, 0, 4, 16, 16),
            box(12, 0, 0, 16, 16, 16)
    };

    public StoneTableBlock() {
        super(Properties.of(Material.STONE).strength(1F).sound(SoundType.STONE).noOcclusion());
        registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.FACING, Direction.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> arg) {
        arg.add(BlockStateProperties.FACING);
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE[state.getValue(BlockStateProperties.FACING).ordinal()];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace().getOpposite());
    }

    @Override
    @Nonnull
    @Deprecated
    public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rtr) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openGui(serverPlayer, new ContainerProvider(pos), pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);
        list.add(new TranslatableComponent("block.extendedexchange.stone_table.tooltip").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    private record ContainerProvider(BlockPos pos) implements MenuProvider {
        @Override
        public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
            return new StoneTableMenu(windowId, playerInventory, pos);
        }

        @Override
        @Nonnull
        public Component getDisplayName() {
            return TextComponent.EMPTY;
        }
    }
}
