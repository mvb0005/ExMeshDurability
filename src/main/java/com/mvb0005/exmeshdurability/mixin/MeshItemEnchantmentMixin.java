package com.mvb0005.exmeshdurability.mixin;

import com.mvb0005.exmeshdurability.config.ModConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Extends {@code MeshItem#canApplyAtEnchantingTable} to allow the
 * <em>Unbreaking</em> enchantment when the durability feature is enabled.
 *
 * <p>Ex Deorum already permits Efficiency and Fortune on mesh items; this
 * simply adds Unbreaking so players can extend mesh lifetime.</p>
 */
@Pseudo
@Mixin(targets = "thedarkcolour.exdeorum.item.MeshItem", remap = false)
public abstract class MeshItemEnchantmentMixin {

    @Inject(method = "canApplyAtEnchantingTable", at = @At("RETURN"), cancellable = true)
    private void exmd$allowUnbreaking(ItemStack stack, Enchantment enchantment,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.ENABLE.get() && enchantment == Enchantments.UNBREAKING) {
            cir.setReturnValue(true);
        }
    }
}
