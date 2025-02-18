package ca.dsevvv.sevsuperblaster.item;


import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class SuperBlasterItem extends Item {
    public static final int DEFAULT_LEVEL = 1;
    public static final int DEFAULT_PROJECTILE_DAMAGE = 0;
    public static final int DEFAULT_EXPLOSION_SIZE = 0;
    public static final int DEFAULT_HEAL_ON_KILL = 0;
    public static final float DEFAULT_HOMING_SPEED = 0.0F;


    public SuperBlasterItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getDefaultMaxStackSize() {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Gunshot shot = new Gunshot(SevSuperBlaster.GUNSHOT.get(), level);
            shot.setDamage(getProjectileDamage(stack));
            shot.setExplosionSize(getExplosionSize(stack));
            shot.setHeal(getHealOnKill(stack));
            shot.setHomingSpeed(getHomingSpeed(stack));
            shot.setOwner(player);
            shot.setNoGravity(true);
            shot.setPos(player.getX(), player.getEyeY(), player.getZ());
            Vec3 lookDirection = player.getLookAngle();
            shot.shoot(lookDirection.x, lookDirection.y, lookDirection.z, 1F, 0.0F);
            level.addFreshEntity(shot);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static void setProjectileDamage(ItemStack stack, int projectileDamage) {
        stack.set(SevSuperBlaster.BLASTER_DMG, projectileDamage);
    }

    public static int getProjectileDamage(ItemStack stack) {
        return stack.get(SevSuperBlaster.BLASTER_DMG) >= 1 ? stack.get(SevSuperBlaster.BLASTER_DMG) : 1;
    }

    public static void setExplosionSize(ItemStack stack, int explosionSize){
        stack.set(SevSuperBlaster.BLASTER_EXPLOSION_SIZE, explosionSize);
    }

    public static int getExplosionSize(ItemStack stack){
        return stack.get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE) >= 1 ? stack.get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE) : 1;
    }

    public static void setHealOnKill(ItemStack stack, int healOnKill){
        stack.set(SevSuperBlaster.BLASTER_HEAL_ON_KILL, healOnKill);
    }

    public static int getHealOnKill(ItemStack stack){
        return stack.get(SevSuperBlaster.BLASTER_HEAL_ON_KILL) >= 1 ? stack.get(SevSuperBlaster.BLASTER_HEAL_ON_KILL) : 1;
    }

    public static void setHomingSpeed(ItemStack stack, float homingSpeed){
        stack.set(SevSuperBlaster.BLASTER_HOMING_SPEED, homingSpeed);
    }

    public static float getHomingSpeed(ItemStack stack){
        return stack.get(SevSuperBlaster.BLASTER_HOMING_SPEED) >= 0.1f ? stack.get(SevSuperBlaster.BLASTER_HOMING_SPEED) : 0.1f;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(stack.get(SevSuperBlaster.BLASTER_LVL) != null
        && stack.get(SevSuperBlaster.BLASTER_DMG) != null
        && stack.get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE) != null
        && stack.get(SevSuperBlaster.BLASTER_HEAL_ON_KILL) != null
        && stack.get(SevSuperBlaster.BLASTER_HOMING_SPEED) != null){
            tooltipComponents.add(Component.literal("Level: " + stack.get(SevSuperBlaster.BLASTER_LVL)).withColor(0x00ffff));
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.literal("Damage:            " + stack.get(SevSuperBlaster.BLASTER_DMG)).withColor(0xFF0000));
            tooltipComponents.add(Component.literal("Explosion Size:   " + stack.get(SevSuperBlaster.BLASTER_EXPLOSION_SIZE)).withColor(0xffd700));
            tooltipComponents.add(Component.literal("Heal on Kill:       " + stack.get(SevSuperBlaster.BLASTER_HEAL_ON_KILL)).withColor(0x00ff2e));
            tooltipComponents.add(Component.literal("Homing Speed:   " + stack.get(SevSuperBlaster.BLASTER_HOMING_SPEED)).withColor(0xe514e2));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
