package net.marcus.marcusmod.item;

import net.marcus.marcusmod.item.custom.fuel_item;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class moditems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, marcusmod.MOD_ID);

    public static final RegistryObject<Item> SINGLE = ITEMS.register("single",
            () -> new Item( new Item.Properties()));
    public static final RegistryObject<Item> CHARGER = ITEMS.register("charger",
            () -> new Item( new Item.Properties()));

    public static final RegistryObject<Item> SHIT = ITEMS.register("shit",
            () -> new Item( new Item.Properties().food(modfoods.SHIT)));

    public static final RegistryObject<Item> VACUUM_FUEL_STICK = ITEMS.register("vacuum_fuel_stick",
            () -> new fuel_item( new Item.Properties(), 600));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
