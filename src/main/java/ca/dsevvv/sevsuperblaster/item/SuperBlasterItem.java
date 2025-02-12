package ca.dsevvv.sevsuperblaster.item;


import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.entity.projectile.GunshotProjectile;
import com.mojang.logging.LogUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SuperBlasterItem extends Item {


    public SuperBlasterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            GunshotProjectile shot = new GunshotProjectile(SevSuperBlaster.GUNSHOT.get(), level);
            shot.setNoGravity(true);
            shot.setPos(player.getX(), player.getEyeY() - 0.10000000149011612D, player.getZ());
            Vec3 lookDirection = player.getLookAngle();
            shot.setDeltaMovement(lookDirection.scale(2)); // Set velocity to fly toward where the player is looking
            level.addFreshEntity(shot);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    public void setProjectileDamage(int projectileDamage) {
        
    }
    public int getProjectileDamage() {
        return 0;
    }
    
    public void setExplosionSize(int explosionSize){
        
    }
    public int getExplosionSize(){
        return 0;
    }

    public void setHealOnKill(int explosionSize){

    }
    public int getHealOnKill(){
        return 0;
    }

    public void setHomingSpeed(){

    }
    public int getHomingSpeed(){
        return 0;
    }
}
