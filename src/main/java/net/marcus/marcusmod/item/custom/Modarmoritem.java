package net.marcus.marcusmod.item.custom;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public class Modarmoritem extends ArmorItem {
    private static final Map<ArmorMaterial, MobEffectInstance> MATERIALS_TO_EFFECT =
            (new ImmutableMap.Builder<ArmorMaterial, MobEffectInstance>())
                    .put(ModArmorMaterials.SINGLE, new MobEffectInstance(MobEffects.NIGHT_VISION, 1000, 0,
                            true, false, true)).build();


    public Modarmoritem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level world, Player player, int slotIndex, int selectedIndex) {
        if(!world.isClientSide()) {
            if(hasSuitOfArmor(player)) {
                giveArmorEffect(player);
            }
        }
    }

    private void giveArmorEffect(Player player) {
        for (Map.Entry<ArmorMaterial, MobEffectInstance> entry :MATERIALS_TO_EFFECT.entrySet()) {
            ArmorMaterial armorMaterial = entry.getKey();
            MobEffectInstance mobEffectInstance = entry.getValue();

            if(hascorrectArmor(armorMaterial, player)) {
                addEffectForMaterials(player, armorMaterial, mobEffectInstance);
            }
        }
    }

    private void addEffectForMaterials(Player player, ArmorMaterial armorMaterial, MobEffectInstance mobEffectInstance) {
        boolean hasTheEffect = player.hasEffect(mobEffectInstance.getEffect());

        if(!hasTheEffect && hascorrectArmor(armorMaterial, player)) {
            player.addEffect(new MobEffectInstance(mobEffectInstance));
        }
    }

    private boolean hascorrectArmor(ArmorMaterial material, Player player) {
        for (ItemStack armorStack : player.getInventory().armor) {
            if(!(armorStack.getItem() instanceof ArmorItem)) {
                return false;
            }
            ArmorItem boots = ((ArmorItem)player.getInventory().getArmor(0).getItem());
            ArmorItem legging = ((ArmorItem)player.getInventory().getArmor(1).getItem());
            ArmorItem chestplate = ((ArmorItem)player.getInventory().getArmor(2).getItem());
            ArmorItem helmet = ((ArmorItem)player.getInventory().getArmor(3).getItem());

            return helmet.getMaterial() == material && chestplate.getMaterial() == material && legging.getMaterial() == material && boots.getMaterial() == material;
        }
        return false;
    }

    private boolean hasSuitOfArmor(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack legging = player.getInventory().getArmor(1);
        ItemStack chestplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !(boots.isEmpty() || legging.isEmpty() || chestplate.isEmpty() || helmet.isEmpty());
    }
}
