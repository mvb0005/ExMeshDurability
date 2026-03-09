package com.mvb0005.exmeshdurability.mixin;

import com.mvb0005.exmeshdurability.config.ModConfig;
import com.mvb0005.exmeshdurability.util.MeshDurabilityHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Overrides the durability-bar rendering methods on {@link Item} for all
 * items that carry the {@code exdeorum:sieve_meshes} tag so that the custom
 * use-count stored in the {@value MeshDurabilityHelper#DAMAGE_TAG} NBT key is
 * reflected as a coloured durability bar in inventories.
 *
 * <p>The three Forge-added methods ({@code isBarVisible}, {@code getBarWidth},
 * {@code getBarColor}) are not obfuscated, so {@code remap = false} is used
 * for each injection.</p>
 */
@Mixin(Item.class)
public abstract class ItemBarMixin {

    private static final TagKey<Item> SIEVE_MESHES =
            ItemTags.create(new ResourceLocation("exdeorum", "sieve_meshes"));

    /**
     * Shows the durability bar only when the mesh has at least one use recorded
     * and the feature is enabled.
     */
    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true, remap = false)
    private void exmd$isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.ENABLE.get() && stack.is(SIEVE_MESHES)) {
            CompoundTag tag = stack.getTag();
            cir.setReturnValue(tag != null && tag.getInt(MeshDurabilityHelper.DAMAGE_TAG) > 0);
        }
    }

    /**
     * Scales the bar width proportionally to remaining durability.
     */
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true, remap = false)
    private void exmd$getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.ENABLE.get() && stack.is(SIEVE_MESHES)) {
            CompoundTag tag = stack.getTag();
            int damage = tag != null ? tag.getInt(MeshDurabilityHelper.DAMAGE_TAG) : 0;
            int maxDamage = MeshDurabilityHelper.getMaxDurability(stack);
            if (maxDamage > 0) {
                cir.setReturnValue(Math.round(13.0f - 13.0f * damage / maxDamage));
            }
        }
    }

    /**
     * Colours the bar from green (full) through yellow to red (nearly spent),
     * using the same HSV ramp as vanilla tool durability.
     */
    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true, remap = false)
    private void exmd$getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.ENABLE.get() && stack.is(SIEVE_MESHES)) {
            CompoundTag tag = stack.getTag();
            int damage = tag != null ? tag.getInt(MeshDurabilityHelper.DAMAGE_TAG) : 0;
            int maxDamage = MeshDurabilityHelper.getMaxDurability(stack);
            if (maxDamage > 0) {
                float health = 1.0f - (float) damage / maxDamage;
                cir.setReturnValue(Mth.hsvToRgb(health / 3.0f, 1.0f, 1.0f));
            }
        }
    }
}
