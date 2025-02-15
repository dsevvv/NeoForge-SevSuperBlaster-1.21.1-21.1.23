package ca.dsevvv.sevsuperblaster.entity.projectile;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Explosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jline.utils.Log;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;


public class Gunshot extends AbstractArrow {

    private LivingEntity currentTarget;

    public Gunshot(EntityType<Gunshot> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        currentTarget = findTarget();
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount > 100) {
            explode();
        }

        if (!level().isClientSide) {
            // Check for target every 5 ticks
            if (tickCount == 1 || tickCount % 5 == 0 && currentTarget == null) {
                currentTarget = findTarget();
            }

            if (currentTarget != null && currentTarget.isAlive()) {
                double xDiff = currentTarget.getX() - getX();
                double yDiff = currentTarget.getEyeY() - getY();
                double zDiff = currentTarget.getZ() - getZ();
                double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

                double speed = 0.6; // Base speed factor

                double smoothingFactor = 0.2; // Adjust this value for smoother motion
                double xMotion = getDeltaMovement().x() + ((xDiff / distance * speed - getDeltaMovement().x()) * smoothingFactor);
                double yMotion = getDeltaMovement().y() + ((yDiff / distance * speed - getDeltaMovement().y()) * smoothingFactor);
                double zMotion = getDeltaMovement().z() + ((zDiff / distance * speed - getDeltaMovement().z()) * smoothingFactor);

                setDeltaMovement(xMotion, yMotion, zMotion);

            }
        }
    }

    private void explode() {
        level().explode(this.getOwner(), getX(), getY(), getZ(), 3.0f, Level.ExplosionInteraction.TNT);
        remove(RemovalReason.DISCARDED);
    }

    private LivingEntity findTarget(){
        return level().getNearestEntity(Mob.class, TargetingConditions.forCombat(), this.getControllingPassenger(),
                getX(), getY(), getZ(), getBoundingBox().inflate(20));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        LogUtils.getLogger().info("Hit entity: " + result.getEntity());
        Entity target = result.getEntity();

        if (target instanceof Mob) {
            explode();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result){
        explode();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(SevSuperBlaster.GUNSHOT_ITEM.get());
    }


}
