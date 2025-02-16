package ca.dsevvv.sevsuperblaster.entity.projectile;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import com.mojang.logging.LogUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;


public class Gunshot extends AbstractArrow {

    private LivingEntity currentTarget;
    private int damage;
    private int explosionSize;
    private int heal;
    private float homingSpeed;

    public Gunshot(EntityType<Gunshot> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
        currentTarget = findTarget();

        if(this.getOwner() != null){
//            damage = setDamage();
//            explosionSize = setExplosionSize();
//            heal = setHeal();
//            homingSpeed = setHomingSpeed();
            damage = 1;
            explosionSize = 1;
            heal = 1;
            homingSpeed = 0.2f;
        }
        else{
            damage = 1;
            explosionSize = 1;
            heal = 1;
            homingSpeed = 0.2f;
        }
    }

    @Override
    public void tick() {
        super.tick();
        trailParticles();

        if (tickCount == 100) {
            explode();
        }

        if (!level().isClientSide && this.getOwner() != null) {
            // Check for target first tick, and then subsequently every 5 ticks.
            // Will only attempt to get a target if currentTarget is null or has died
            if (tickCount == 1 || tickCount % 5 == 0
            && (currentTarget == null || !currentTarget.isAlive())) {
                currentTarget = findTarget();
            }

            if (currentTarget != null && currentTarget.isAlive()) {
                double xDiff = currentTarget.getX() - getX();
                double yDiff = currentTarget.getEyeY() - getY();
                double zDiff = currentTarget.getZ() - getZ();
                double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

                double speed = 0.6; // Base projectile speed factor

                double xMotion = getDeltaMovement().x() + ((xDiff / distance * speed - getDeltaMovement().x()) * homingSpeed);
                double yMotion = getDeltaMovement().y() + ((yDiff / distance * speed - getDeltaMovement().y()) * homingSpeed);
                double zMotion = getDeltaMovement().z() + ((zDiff / distance * speed - getDeltaMovement().z()) * homingSpeed);

                setDeltaMovement(xMotion, yMotion, zMotion);
            }
        }
    }

    private void explode() {
        level().explode(this.getOwner(), getX(), getY(), getZ(), 3.0f, Level.ExplosionInteraction.TNT);
        kill();
    }

    private void explosionParticles() {
        for (int ring = 0; ring < 3; ring++) { // Create 3 rings
            double ringRadius = (ring + 1) * 0.5; // Increase radius for each ring
            for (int i = 0; i < 12; i++) { // 12 particles per ring for a star effect
                double angle = Math.toRadians(i * 30); // Spread particles evenly in the ring
                double xRing = Math.cos(angle) * ringRadius;
                double zRing = Math.sin(angle) * ringRadius;

                // Add SPARK_PARTICLE in a ring
                level().addAlwaysVisibleParticle(SevSuperBlaster.BOOM_PARTICLE.get(),
                        getX() + xRing, getY() + (ring * 0.2), getZ() + zRing,
                        0f,0f,0f);
            }

            // Create a "star explosion" effect for each ring
            for (int j = 0; j < 6; j++) { // 6 points for the star
                double starAngle = Math.toRadians(j * 60); // Spread star points evenly
                double xStar = Math.cos(starAngle) * (ringRadius + 0.5);
                double zStar = Math.sin(starAngle) * (ringRadius + 0.5);

                level().addAlwaysVisibleParticle(SevSuperBlaster.SPARK_PARTICLE.get(),
                        getX() + xStar, getY() + randomOffset() + (ring * 0.2), getZ() + zStar,
                        0f,0f,0f);
            }
        }
    }

    private void trailParticles(){
        double angle = tickCount * 0.3; // Increment angle over time for spiral effect
        double radius = 0.4; // Radius of the spiral
        double xDirection = getDeltaMovement().x();
        double yDirection = getDeltaMovement().y();
        double zDirection = getDeltaMovement().z();
        double magnitude = Math.sqrt(xDirection * xDirection + yDirection * yDirection + zDirection * zDirection);

        // Normalize the direction vector
        xDirection /= magnitude;
        yDirection /= magnitude;
        zDirection /= magnitude;

        // Rotate the offsets around the direction of travel
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);

        double xOffset = radius * (yDirection * cosAngle - zDirection * sinAngle);
        double yOffset = radius * (zDirection * cosAngle - xDirection * sinAngle);
        double zOffset = radius * (xDirection * cosAngle - yDirection * sinAngle);

        level().addAlwaysVisibleParticle(SevSuperBlaster.TRAIL_PARTICLE.get(), getX() + xOffset, getY() + yOffset, getZ() + zOffset, 0, 0, 0);
        level().addAlwaysVisibleParticle(SevSuperBlaster.TRAIL_PARTICLE.get(), getX() - xOffset, getY() - yOffset, getZ() - zOffset, 0, 0, 0);
    }

    private double randomOffset() {
        return (random.nextDouble() - 0.5) * 5.0;//-2.5 to +2.5
    }

    private LivingEntity findTarget(){
        return level().getNearestEntity(Monster.class, TargetingConditions.forCombat(), this.getControllingPassenger(),
                getX(), getY(), getZ(), getBoundingBox().inflate(20));
    }

//    private int setDamage(){
//        Player owner = (Player) this.getOwner();
//        ItemStack superBlaster = owner.getMainHandItem();
//
//        if(superBlaster.getItem() instanceof SuperBlasterItem){
//            return superBlaster.;
//        }
//
//        return null;
//    }
//
//    private int setExplosionSize(){
//        Player owner = (Player) this.getOwner();
//        ItemStack superBlaster = owner.getMainHandItem();
//
//
//        return null;
//    }
//
//    private int setHeal(){
//        Player owner = (Player) this.getOwner();
//        ItemStack superBlaster = owner.getMainHandItem();
//
//
//        return null;
//    }
//
//    private float setHomingSpeed(){
//        Player owner = (Player) this.getOwner();
//        ItemStack superBlaster = owner.getMainHandItem();
//
//        return null;
//    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();

        if (target instanceof Monster) {
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

    @Override
    public void onClientRemoval() {
        explosionParticles();
        super.onClientRemoval();
    }
}
