package ca.dsevvv.sevsuperblaster.item;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;

public class GunshotItem implements ProjectileItem {
    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        return new Gunshot(SevSuperBlaster.GUNSHOT.get(), level);
    }
    public boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity livingEntity) {
        return true;
    }
}
