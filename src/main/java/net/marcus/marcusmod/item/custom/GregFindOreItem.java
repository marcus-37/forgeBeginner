package net.marcus.marcusmod.item.custom;

import net.marcus.marcusmod.tags.ModTags;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import org.jetbrains.annotations.NotNull;

public class GregFindOreItem extends Item {
    public GregFindOreItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()) {
            BlockPos blockpos = BlockPos.containing(pContext.getClickLocation());
            Player player = pContext.getPlayer();
            boolean findOre = false;

            if(player == null) return InteractionResult.SUCCESS;

            for(int i = 0; i <= blockpos.getY(); i++) {
                BlockState state = pContext.getLevel().getBlockState(blockpos.below(i));
                if(isOreBlock(state)) {
                    if(player.isCrouching()) {
                        Breaking(pContext.getLevel(), blockpos.below(i));
                        TeleportPlayer(player, blockpos.below(i));
                    }
                    else {
                        OutPutPosition(player, state.getBlock(), blockpos.below(i));
                        playRightClickSound(pContext.getLevel(), player);
                    }
                    findOre = true;
                    break;
                }
            }

            if(!findOre) {
                player.sendSystemMessage(Component.literal("这里没矿石"));
            }
        }

        if (pContext.getPlayer() != null) {
            pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(),
                    player -> player.broadcastBreakEvent(player.getUsedItemHand()));
        }

        return InteractionResult.SUCCESS;
    }

    private void TeleportPlayer(Player player, BlockPos below) {
        player.teleportTo(below.getX(), below.getY()+1, below.getZ());
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 1));
    }

    private void Breaking(Level pLevel, BlockPos below) {
        BlockPos up1 =  new BlockPos(below.getX(), below.getY()+1, below.getZ());
        BlockPos up2 =  new BlockPos(below.getX(), below.getY()+2, below.getZ());
        pLevel.destroyBlock(up1, true);
        pLevel.destroyBlock(up2, true);
    }

    private void playRightClickSound(Level pLevel, Player pPlayer) {
        pLevel.playSound(
                null,
                pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.END_GATEWAY_SPAWN,
                SoundSource.PLAYERS,
                0.5F,
                0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }

    private void OutPutPosition(Player player, Block block, BlockPos below) {
        player.sendSystemMessage(Component.literal("发现" + I18n.get(block.getDescriptionId()) + "位于"
        + below.getX() + "," + below.getY() + "," + below.getZ()));
    }

    private boolean isOreBlock(BlockState state) {
        return state.is(ModTags.Blocks.TEST_ORES);
    }
}
