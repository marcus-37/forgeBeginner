package net.marcus.marcusmod.item;

import net.marcus.marcusmod.block.modblocks;
import net.marcus.marcusmod.marcusmod;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, marcusmod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MARCUS_TAB = CREATIVE_TABS.register("marcusmod",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(moditems.SINGLE.get()))
                    .title(Component.translatable("creative.marcus_mod"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(moditems.SINGLE.get());
                        output.accept(moditems.CHARGER.get());
                        output.accept(modblocks.REACTOR.get());
                        output.accept(modblocks.SINGLE_ORE.get());
                        output.accept(moditems.SHIT.get());
                        output.accept(moditems.VACUUM_FUEL_STICK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
