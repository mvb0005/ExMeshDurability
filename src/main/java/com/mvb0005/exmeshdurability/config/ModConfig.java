package com.mvb0005.exmeshdurability.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue ENABLE;

    public static final ForgeConfigSpec.IntValue STRING_MESH_DURABILITY;
    public static final ForgeConfigSpec.IntValue FLINT_MESH_DURABILITY;
    public static final ForgeConfigSpec.IntValue IRON_MESH_DURABILITY;
    public static final ForgeConfigSpec.IntValue GOLDEN_MESH_DURABILITY;
    public static final ForgeConfigSpec.IntValue DIAMOND_MESH_DURABILITY;
    public static final ForgeConfigSpec.IntValue NETHERITE_MESH_DURABILITY;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.comment("ExMeshDurability Configuration");

        ENABLE = BUILDER
                .comment("Enable mesh durability. When true, sieve meshes will wear out over time and drop when broken.")
                .define("enable", true);

        BUILDER.push("durability");

        STRING_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the string mesh breaks and drops.")
                .defineInRange("string_mesh", 59, 1, 100000);

        FLINT_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the flint mesh breaks and drops.")
                .defineInRange("flint_mesh", 131, 1, 100000);

        IRON_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the iron mesh breaks and drops.")
                .defineInRange("iron_mesh", 250, 1, 100000);

        GOLDEN_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the golden mesh breaks and drops.")
                .defineInRange("golden_mesh", 32, 1, 100000);

        DIAMOND_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the diamond mesh breaks and drops.")
                .defineInRange("diamond_mesh", 1561, 1, 100000);

        NETHERITE_MESH_DURABILITY = BUILDER
                .comment("Number of sieve uses before the netherite mesh breaks and drops.")
                .defineInRange("netherite_mesh", 2031, 1, 100000);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
