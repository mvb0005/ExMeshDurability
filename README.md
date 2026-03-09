# ExMeshDurability

A [Minecraft Forge](https://minecraftforge.net/) addon for [Ex Deorum](https://github.com/thedarkcolour/ExDeorum) (Minecraft 1.20.1) that adds configurable durability to sieve meshes.

## Features

- Every completed sieve operation consumes one point of durability from the installed mesh.
- When a mesh runs out of uses it **drops as an item entity** above the sieve instead of vanishing — pick it up, repair it in an anvil, or discard it.
- A durability bar is displayed on mesh items in your inventory (green → red as uses decrease).
- The **Unbreaking** enchantment is now accepted by meshes at the enchanting table; each level gives a proportional chance to skip the durability damage (I = 50 %, II ≈ 67 %, III = 75 %).
- All durability values are fully configurable in `config/exmeshdurability-common.toml`.

## Default durability values

| Mesh         | Uses before breaking |
|--------------|----------------------|
| String       | 59                   |
| Flint        | 131                  |
| Iron         | 250                  |
| Golden       | 32                   |
| Diamond      | 1 561                |
| Netherite    | 2 031                |

Values mirror vanilla tool durability tiers so the progression feels natural.

## Requirements

- Minecraft **1.20.1**
- Minecraft Forge **47.x**
- [Ex Deorum](https://github.com/thedarkcolour/ExDeorum) (any 1.x build for 1.20.1)

## Download the latest build

Every push to the repository triggers a GitHub Actions build that produces the ready-to-use mod JAR.

1. Open the [**Actions** tab](https://github.com/mvb0005/ExMeshDurability/actions/workflows/build.yml) of this repository.
2. Click the most recent successful **Build** run.
3. Scroll to the **Artifacts** section at the bottom of the run summary.
4. Download the `ExMeshDurability-…` zip, unzip it, and copy the `.jar` file into your Minecraft `mods/` folder alongside Ex Deorum.

The artifact is retained for **90 days** per run.

## Building from source

```bash
# Clone the repository
git clone https://github.com/mvb0005/ExMeshDurability.git
cd ExMeshDurability

# Run the Gradle build (requires Java 17)
./gradlew build
```

The compiled JAR is placed in `build/libs/`.

> **Note:** The build does **not** require Ex Deorum on the compile classpath — all Ex Deorum references are resolved at runtime via string-based Mixin targets.  If you want code completion in your IDE while developing, add Ex Deorum to the `dependencies` block in `build.gradle` as a `compileOnly fg.deobf(...)` dependency.

## Configuration

After the first launch a file is created at:

```
<minecraft_dir>/config/exmeshdurability-common.toml
```

```toml
# Enable mesh durability. When true, sieve meshes will wear out over time and drop when broken.
enable = true

[durability]
    string_mesh    = 59
    flint_mesh     = 131
    iron_mesh      = 250
    golden_mesh    = 32
    diamond_mesh   = 1561
    netherite_mesh = 2031
```

Set `enable = false` to disable the mod without uninstalling it.

## How it works

The mod uses [Mixin](https://github.com/SpongePowered/Mixin) to hook into Ex Deorum internals without requiring Ex Deorum as a compile-time dependency:

| Mixin class | Target | Purpose |
|---|---|---|
| `SieveLogicMixin` | `SieveLogic#sift` | Detects sieve-cycle completion and decrements mesh durability |
| `AbstractSieveBlockEntityMixin` | `AbstractSieveBlockEntity` | Implements `ISieveOwner` to expose an item-drop helper |
| `MeshItemEnchantmentMixin` | `MeshItem#canApplyAtEnchantingTable` | Allows the Unbreaking enchantment on meshes |
| `ItemBarMixin` | `Item` (client-side) | Renders the custom durability bar in inventories |

## License

MIT
