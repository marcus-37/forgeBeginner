package net.marcus.marcusmod.datagen;

import net.marcus.marcusmod.item.moditems;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

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
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> itemRegistryObject) {
        if (itemRegistryObject.getId() != null) {
            return withExistingParent(itemRegistryObject.getId().getPath(),
                    ResourceLocation.parse("item/generated")).texture("layer0",
                    ResourceLocation.fromNamespaceAndPath(marcusmod.MOD_ID, "item/" + itemRegistryObject.getId().getPath()));
        }
        return null;
    }
}
