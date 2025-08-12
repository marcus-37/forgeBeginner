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

        trimmedArmorItem(moditems.SINGLE_BOOTS);
        trimmedArmorItem(moditems.SINGLE_LEGGING);
        trimmedArmorItem(moditems.SINGLE_HELMET);
        trimmedArmorItem(moditems.SINGLE_CHESTPLATE);


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

    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    private void trimmedArmorItem(RegistryObject<Item> itemRegistryObject) {
        final String MOD_ID = marcusmod.MOD_ID; // Change this to your mod id

        if(itemRegistryObject.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                //ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);
                ResourceLocation armorItemResLoc = itemRegistryObject.getId();


                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemRegistryObject.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemRegistryObject.getId().getPath()));
            });
        }
    }

}
