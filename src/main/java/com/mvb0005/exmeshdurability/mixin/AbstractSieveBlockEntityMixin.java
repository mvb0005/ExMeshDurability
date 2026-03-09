package com.mvb0005.exmeshdurability.mixin;

import com.mvb0005.exmeshdurability.api.ISieveOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Mixes {@link ISieveOwner} into {@code AbstractSieveBlockEntity} so that
 * {@link SieveLogicMixin} can drop broken mesh items at the sieve's position
 * without a compile-time dependency on Ex Deorum.
 *
 * <p>The drop physics mirror Ex Deorum's own result-item spawning in
 * {@code AbstractSieveBlockEntity#handleResultItem}.</p>
 */
@Pseudo
@Mixin(targets = "thedarkcolour.exdeorum.blockentity.AbstractSieveBlockEntity", remap = false)
public abstract class AbstractSieveBlockEntityMixin implements ISieveOwner {

    /**
     * Shadows {@code AbstractSieveBlockEntity#getServerLevel()}, which implements
     * {@code SieveLogic.Owner#getServerLevel()}.  The method is not obfuscated
     * (it is an Ex Deorum addition), so {@code remap = false} is correct here.
     */
    @Shadow
    public abstract ServerLevel getServerLevel();

    @Override
    public void exmd$dropItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ServerLevel level = getServerLevel();
        if (level == null) {
            return;
        }
        // getBlockPos() is a vanilla BlockEntity method; ForgeGradle remaps it.
        BlockPos pos = ((BlockEntity) (Object) this).getBlockPos();

        ItemEntity itemEntity = new ItemEntity(
                level,
                pos.getX() + 0.5,
                pos.getY() + 1.5,
                pos.getZ() + 0.5,
                stack
        );
        itemEntity.setDeltaMovement(
                level.random.nextGaussian() * 0.05,
                0.2,
                level.random.nextGaussian() * 0.05
        );
        level.addFreshEntity(itemEntity);
    }
}
