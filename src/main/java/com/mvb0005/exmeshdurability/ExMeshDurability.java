package com.mvb0005.exmeshdurability;

import com.mojang.logging.LogUtils;
import com.mvb0005.exmeshdurability.config.ModConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.slf4j.Logger;

@Mod(ExMeshDurability.MOD_ID)
public class ExMeshDurability {

    public static final String MOD_ID = "exmeshdurability";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ExMeshDurability() {
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC, MOD_ID + "-common.toml");
        LOGGER.info("ExMeshDurability loaded – sieve meshes will now wear out!");
    }
}
