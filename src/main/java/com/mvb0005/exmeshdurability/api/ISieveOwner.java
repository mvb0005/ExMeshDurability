package com.mvb0005.exmeshdurability.api;

import net.minecraft.world.item.ItemStack;

/**
 * Implemented on Ex Deorum sieve block entities via
 * {@link com.mvb0005.exmeshdurability.mixin.AbstractSieveBlockEntityMixin}.
 * Provides a way to drop item entities at the sieve's location, used when a
 * mesh depletes its durability.
 */
public interface ISieveOwner {

    /**
     * Spawns the given {@code stack} as an item entity directly above the sieve,
     * mimicking the same behaviour used for normal sieve result drops.
     *
     * @param stack the item to drop; must not be empty
     */
    void exmd$dropItem(ItemStack stack);
}
