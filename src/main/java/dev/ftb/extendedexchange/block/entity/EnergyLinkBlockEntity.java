package dev.ftb.extendedexchange.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class EnergyLinkBlockEntity extends AbstractLinkBlockEntity {
    // trivial implementation; all logic is in the superclass

    public EnergyLinkBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.ENERGY_LINK.get(), blockPos, blockState);
    }
}
