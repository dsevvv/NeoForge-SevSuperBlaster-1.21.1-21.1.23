package ca.dsevvv.sevsuperblaster.event;

import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class GunshotKillEvent {

    @SubscribeEvent
    public void onGunshotKill(LivingDeathEvent event){
        LivingEntity deadEntity = event.getEntity();
        if(deadEntity.level().isClientSide)
            return;

        LogUtils.getLogger().info("Calling Event from Server");

        if(event.getSource().getDirectEntity() instanceof  Gunshot gunshot){
            if(gunshot.getOwner() instanceof Player player){
                player.heal(gunshot.getHeal());
            }
        }
    }
}
