package com.mvb0005.exmeshdurability.util;

import com.mvb0005.exmeshdurability.ExMeshDurability;
import com.mvb0005.exmeshdurability.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class MeshDurabilityHelper {

    /** NBT key used to store the current use-count on a mesh ItemStack. */
    public static final String DAMAGE_TAG = "ExMDDamage";

    private static final String EXDEORUM_NS = "exdeorum";

    /**
     * Returns the configured maximum number of sieve uses for the given mesh
     * item, or {@code 0} if the item is not a recognised Ex Deorum mesh.
     */
    public static int getMaxDurability(ItemStack mesh) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(mesh.getItem());
        if (id == null || !EXDEORUM_NS.equals(id.getNamespace())) {
            return 0;
        }
        return switch (id.getPath()) {
            case "string_mesh"    -> ModConfig.STRING_MESH_DURABILITY.get();
            case "flint_mesh"     -> ModConfig.FLINT_MESH_DURABILITY.get();
            case "iron_mesh"      -> ModConfig.IRON_MESH_DURABILITY.get();
            case "golden_mesh"    -> ModConfig.GOLDEN_MESH_DURABILITY.get();
            case "diamond_mesh"   -> ModConfig.DIAMOND_MESH_DURABILITY.get();
            case "netherite_mesh" -> ModConfig.NETHERITE_MESH_DURABILITY.get();
            default               -> 0;
        };
    }

    /**
     * Increments the use-count stored on {@code mesh} by one.
     *
     * @return {@code true} if the mesh has now reached (or exceeded) its
     *         maximum durability and should be removed from the sieve and
     *         dropped as an item entity; {@code false} if it is still intact.
     */
    public static boolean incrementAndCheckBroken(ItemStack mesh) {
        int maxDurability = getMaxDurability(mesh);
        if (maxDurability <= 0) {
            return false;
        }

        CompoundTag tag = mesh.getOrCreateTag();
        int currentDamage = tag.getInt(DAMAGE_TAG);

        if (currentDamage + 1 >= maxDurability) {
            // Leave the damage tag as-is so the dropped item shows a nearly
            // depleted bar, making it obvious the mesh is spent.
            ExMeshDurability.LOGGER.debug(
                    "Mesh depleted: {} ({}/{})",
                    ForgeRegistries.ITEMS.getKey(mesh.getItem()), currentDamage + 1, maxDurability);
            return true;
        }

        tag.putInt(DAMAGE_TAG, currentDamage + 1);
        return false;
    }
}
