package net.marcus.marcusmod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class modfoods {
    public static final FoodProperties SHIT = new FoodProperties.Builder().nutrition(2).fast()
            .saturationMod(0.9f).effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 200, 3), 0.9f).build();
}
