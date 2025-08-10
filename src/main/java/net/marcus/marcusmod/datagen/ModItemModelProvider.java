package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.item.moditems;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, marcusmod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(moditems.DETECTOR);
        simpleItem(moditems.CHARGER);
        simpleItem(moditems.SHIT);
        simpleItem(moditems.VACUUM_FUEL_STICK);
        simpleItem(moditems.SINGLE);
        simpleItem(moditems.SINGLE_CHESTPLATE);
        simpleItem(moditems.SINGLE_BOOTS);
        simpleItem(moditems.SINGLE_HELMET);
        simpleItem(moditems.SINGLE_LEGGING);

        simpleBlockItem(modblocks.REACTOR_DOOR);
        simpleBlockItem(modblocks.REACTOR_TRAPDOOR);

        simpleBlockItemNoPhoto(modblocks.REACTOR_STAIR);
        simpleBlockItemNoPhoto(modblocks.REACTOR_SLAB);
        simpleBlockItemNoPhoto(modblocks.REACTOR_PRESSURE_PLATE);
        simpleBlockItemNoPhoto(modblocks.REACTOR_FENCE_GATE);

        fenceItem(modblocks.REACTOR_FENCE, modblocks.REACTOR);
        buttonItem(modblocks.REACTOR_BUTTON, modblocks.REACTOR);
        //wallItem(modblocks.REACTOR_WALL, modblocks.REACTOR);
        wallInventory("reactor_wall", modLoc("block/reactor"));

        //registerTrimmedArmor(moditems.SINGLE_BOOTS);
        //registerTrimmedArmor(moditems.SINGLE_LEGGING);
        //registerTrimmedArmor(moditems.SINGLE_HELMET);
        //registerTrimmedArmor(moditems.SINGLE_CHESTPLATE);


    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> itemRegistryObject) {
        if (itemRegistryObject.getId() != null) {
            return withExistingParent(itemRegistryObject.getId().getPath(),
                    ResourceLocation.parse("item/generated")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "item/" + itemRegistryObject.getId().getPath()));
        }
        return null;
    }

    public void fenceItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/fence_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }
    public void buttonItem(RegistryObject<Block> block, RegistryObject<Block> baseBlock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/button_inventory"))
                .texture("texture", ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseBlock.get()).getPath()));
    }


    private ItemModelBuilder simpleBlockItem(RegistryObject<Block> block) {
        if (block.getId() != null) {
            return withExistingParent(block.getId().getPath(),
                    ResourceLocation.parse("item/generated")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "item/" + block.getId().getPath()));
        }
        return null;
    }

    private ItemModelBuilder simpleHandItem(RegistryObject<Item> item) {
        if (item.getId() != null) {
            return withExistingParent(item.getId().getPath(),
                    ResourceLocation.parse("item/handheld")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "item/" + item.getId().getPath()));
        }
        return null;
    }

    public void simpleBlockItemNoPhoto(RegistryObject<Block> block) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    private void registerTrimmedArmor(RegistryObject<Item> itemRegistryObject) {
        Item item = itemRegistryObject.get();
        if (!(item instanceof ArmorItem)) return;
        String itemPath = itemRegistryObject.getId().getPath();

        // base model：item/generated + layer0 指向 item纹理
        this.withExistingParent(itemPath, mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + itemPath));
        var baseBuilder = this.getBuilder(itemPath);

        // 构造 trimMaterials 映射：ResourceKey<TrimMaterial> -> float(predict value)
        Map<ResourceKey<TrimMaterial>, Float> trimMap = buildTrimMapFromTrimMaterials();

        // 遍历每个 trim，生成 override 模型
        for (Map.Entry<ResourceKey<TrimMaterial>, Float> e : trimMap.entrySet()) {
            ResourceKey<TrimMaterial> trimKey = e.getKey();
            float predicateIndex = e.getValue();

            ResourceLocation trimRL = trimKey.location(); // e.g. minecraft:netherite
            String trimName = trimRL.getPath();

            // armor type: helmet/chestplate/leggings/boots
            String armorType = switch (((ArmorItem) item).getEquipmentSlot()) {
                case HEAD -> "helmet";
                case CHEST -> "chestplate";
                case LEGS -> "leggings";
                case FEET -> "boots";
                default -> "unknown";
            };

            // vanilla 常用的 trim texture 路径约定
            ResourceLocation trimTexture = ResourceLocation.fromNamespaceAndPath(
                    trimRL.getNamespace(),
                    "trims/items/" + armorType + "_trim_" + trimName
            );

            // 告诉 datagen 这个纹理会存在（由 datapack 或其它 provider 提供）
            this.existingFileHelper.trackGenerated(trimTexture, PackType.CLIENT_RESOURCES, ".png", "textures");

            // 生成具体的 trim 模型文件：models/item/<item>_<trim>_trim.json
            String trimModelName = itemPath + "_" + trimName + "_trim";
            getBuilder(trimModelName)
                    .parent(new ModelFile.UncheckedModelFile(mcLoc("item/generated")))
                    .texture("layer0", modLoc("item/" + itemPath))
                    .texture("layer1", trimTexture);

            // 在 base 模型上添加 override（predicate = trim_type）
            baseBuilder.override()
                    .model(new ModelFile.UncheckedModelFile(modLoc(trimModelName)))
                    .predicate(mcLoc("trim_type"), predicateIndex)
                    .end();

            // debug 输出：方便在 runData 时查看
            System.out.println("[datagen] Prepared trim model for item=" + itemPath + " trim=" + trimRL + " idx=" + predicateIndex);
        }
    }

    private Map<ResourceKey<TrimMaterial>, Float> buildTrimMapFromTrimMaterials() {
        Map<ResourceKey<TrimMaterial>, Float> map = new LinkedHashMap<>();

        // 这些值直接对应于你贴出的 TrimMaterials.bootstrap(...) 的 pItemModelIndex
        map.put(TrimMaterials.QUARTZ, 0.1F);
        map.put(TrimMaterials.IRON, 0.2F);
        map.put(TrimMaterials.NETHERITE, 0.3F);
        map.put(TrimMaterials.REDSTONE, 0.4F);
        map.put(TrimMaterials.COPPER, 0.5F);
        map.put(TrimMaterials.GOLD, 0.6F);
        map.put(TrimMaterials.EMERALD, 0.7F);
        map.put(TrimMaterials.DIAMOND, 0.8F);
        map.put(TrimMaterials.LAPIS, 0.9F);
        map.put(TrimMaterials.AMETHYST, 1.0F);

        return map;
    }

}
