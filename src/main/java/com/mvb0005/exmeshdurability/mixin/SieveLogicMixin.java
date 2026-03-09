package com.mvb0005.exmeshdurability.mixin;

import com.mvb0005.exmeshdurability.api.ISieveOwner;
import com.mvb0005.exmeshdurability.config.ModConfig;
import com.mvb0005.exmeshdurability.util.MeshDurabilityHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Hooks into {@code SieveLogic#sift} to damage the mesh after each completed
 * sieve operation.  When the mesh runs out of uses it is <em>dropped as an
 * item entity</em> at the sieve's position rather than silently removed.
 *
 * <p>The drop is delegated to {@link ISieveOwner#exmd$dropItem}, which is
 * injected onto {@code AbstractSieveBlockEntity} by
 * {@link AbstractSieveBlockEntityMixin}.</p>
 *
 * <p>The Unbreaking enchantment is respected: each level gives a
 * {@code level/(level+1)} chance to skip the durability decrement entirely,
 * matching vanilla tool behaviour.</p>
 */
@Pseudo
@Mixin(targets = "thedarkcolour.exdeorum.blockentity.logic.SieveLogic", remap = false)
public abstract class SieveLogicMixin {

    // ── Shadowed fields ──────────────────────────────────────────────────────

    /** The item currently being sifted (empty when idle). */
    @Shadow private ItemStack contents;

    /** The mesh installed in this sieve. */
    @Shadow protected ItemStack mesh;

    /**
     * Shadowed as {@code Object} to avoid a compile-time dependency on the
     * inner interface {@code SieveLogic.Owner}.  At runtime the concrete value
     * is always an {@code AbstractSieveBlockEntity}, which
     * {@link AbstractSieveBlockEntityMixin} makes implement {@link ISieveOwner}.
     */
    @Shadow @Final private Object owner;

    // ── Unique (injected) state ───────────────────────────────────────────────

    /** True if the sieve had contents at the start of the current {@code sift} call. */
    @Unique
    private boolean exmd$hadContents = false;

    // ── Injections ────────────────────────────────────────────────────────────

    /**
     * Captures whether the sieve was processing an item at the start of the
     * tick so we can detect a completed cycle on return.
     */
    @Inject(method = "sift", at = @At("HEAD"))
    private void exmd$captureSiftState(float incrementProgress, long time, CallbackInfo ci) {
        this.exmd$hadContents = !this.contents.isEmpty();
    }

    /**
     * After {@code sift} returns, checks whether the sieve just finished a
     * full cycle (contents cleared) and, if so, applies one unit of wear to
     * the mesh.  If the mesh is fully spent it is dropped above the sieve and
     * cleared from the slot.
     */
    @Inject(method = "sift", at = @At("RETURN"))
    private void exmd$onSiftReturn(float incrementProgress, long time, CallbackInfo ci) {
        if (!this.exmd$hadContents || !this.contents.isEmpty() || this.mesh.isEmpty()) {
            return;
        }
        if (!ModConfig.ENABLE.get()) {
            return;
        }

        // Respect the Unbreaking enchantment: skip damage with probability
        // level / (level + 1), matching vanilla tool / armour behaviour.
        int unbreakingLevel = this.mesh.getEnchantmentLevel(Enchantments.UNBREAKING);
        if (unbreakingLevel > 0) {
            float skipChance = (float) unbreakingLevel / (unbreakingLevel + 1);
            if (ThreadLocalRandom.current().nextFloat() < skipChance) {
                return;
            }
        }

        boolean meshBroke = MeshDurabilityHelper.incrementAndCheckBroken(this.mesh);
        if (meshBroke) {
            // Drop the depleted mesh as an item entity at the sieve's position.
            if (this.owner instanceof ISieveOwner sieveOwner) {
                sieveOwner.exmd$dropItem(this.mesh.copy());
            }
            // Clear the mesh slot.
            this.setMesh(ItemStack.EMPTY);
        }
    }

    // ── Shadowed methods ──────────────────────────────────────────────────────

    @Shadow
    public abstract void setMesh(ItemStack mesh);
}
