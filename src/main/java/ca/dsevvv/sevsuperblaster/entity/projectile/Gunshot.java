package ca.dsevvv.sevsuperblaster.entity.projectile;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;


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
                double homingScalar = 0.5;
                double xDiff = currentTarget.getX() - getX();
                double yDiff = currentTarget.getEyeY() - getY();
                double zDiff = currentTarget.getZ() - getZ();
                double distance = Math.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);

                double speed = 0.6; // Base projectile speed factor

                double xMotion = getDeltaMovement().x() + ((xDiff / distance * speed - getDeltaMovement().x()) * (homingSpeed * homingScalar));
                double yMotion = getDeltaMovement().y() + ((yDiff / distance * speed - getDeltaMovement().y()) * (homingSpeed * homingScalar));
                double zMotion = getDeltaMovement().z() + ((zDiff / distance * speed - getDeltaMovement().z()) * (homingSpeed * homingScalar));

                setDeltaMovement(xMotion, yMotion, zMotion);
            }
        }
    }

    private void explode() {
        final float SIZE_SCALAR = 2.0f;

        level().explode(this, null, new GunshotDamageCalculator(), getX(), getY(), getZ(), explosionSize * SIZE_SCALAR, false, Level.ExplosionInteraction.TNT);
        kill();
    }

    private void explosionParticles() {
        for (int ring = 0; ring < 3; ring++) {          // Create 3 rings
            double ringRadius = (ring + 1) * 0.5;       // Increase radius for each ring
            for (int i = 0; i < 12; i++) {              // 12 particles per ring for a star effect
                double angle = Math.toRadians(i * 30);  // Spread particles evenly in the ring
                double xRing = Math.cos(angle) * ringRadius;
                double zRing = Math.sin(angle) * ringRadius;

                                                        // Add BOOM_PARTICLE in a ring
                level().addAlwaysVisibleParticle(SevSuperBlaster.BOOM_PARTICLE.get(),
                        getX() + xRing, getY() + (ring * 0.2), getZ() + zRing,
                        0f,0f,0f);
            }

                                                            // Create a "star explosion" effect for each ring
            for (int j = 0; j < 6; j++) {                   // 6 points for the star
                double starAngle = Math.toRadians(j * 60);  // Spread star points evenly
                double xStar = Math.cos(starAngle) * (ringRadius + 0.5);
                double zStar = Math.sin(starAngle) * (ringRadius + 0.5);

                level().addAlwaysVisibleParticle(SevSuperBlaster.SPARK_PARTICLE.get(),
                        getX() + xStar, getY() + randomOffset() + (ring * 0.2), getZ() + zStar,
                        0f,0f,0f);
            }
        }
    }

    private void trailParticles(){
        double angle = tickCount * 0.3;             // Increment angle over time for spiral effect
        double radius = 0.4;                        // Radius of the spiral
        double xDirection = getDeltaMovement().x();
        double yDirection = getDeltaMovement().y();
        double zDirection = getDeltaMovement().z();
        double magnitude = Math.sqrt(xDirection * xDirection + yDirection * yDirection + zDirection * zDirection);

        // Normalize the direction vector
        xDirection /= magnitude;
        yDirection /= magnitude;
        zDirection /= magnitude;

        // Rotate the offsets around the direction of travel (somewhat bugged, certain edge cases not working correctly, but the bugged effect also looks cool, so I figured I'd move on to larger issues.)
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);

        double xOffset = radius * (yDirection * cosAngle - zDirection * sinAngle);
        double yOffset = radius * (zDirection * cosAngle - xDirection * sinAngle);
        double zOffset = radius * (xDirection * cosAngle - yDirection * sinAngle);

        level().addAlwaysVisibleParticle(SevSuperBlaster.TRAIL_PARTICLE.get(), getX() + xOffset, getY() + yOffset, getZ() + zOffset, 0, 0, 0);
        level().addAlwaysVisibleParticle(SevSuperBlaster.TRAIL_PARTICLE.get(), getX() - xOffset, getY() - yOffset, getZ() - zOffset, 0, 0, 0);
    }

    private double randomOffset() {
        return (random.nextDouble() - 0.5) * 5.0;
    }

    private LivingEntity findTarget(){
        return level().getNearestEntity(Monster.class, TargetingConditions.forCombat(), this.getControllingPassenger(),
                getX(), getY(), getZ(), getBoundingBox().inflate(20));
    }

    public void setDamage(int damage){
        this.damage = damage;
    }

    public void setExplosionSize(int explosionSize){
        this.explosionSize = explosionSize;
    }

    public void setHeal(int heal){
        this.heal = heal;
    }

    public void setHomingSpeed(float homingSpeed){
        this.homingSpeed = homingSpeed;
    }

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

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("Damage", damage);
        compound.putInt("ExplosionSize", explosionSize);
        compound.putInt("Heal", heal);
        compound.putFloat("HomingSpeed", homingSpeed);
        compound.putInt("Target", currentTarget.getId());
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        damage = compound.getInt("Damage");
        explosionSize = compound.getInt("ExplosionSize");
        heal = compound.getInt("Heal");
        homingSpeed = compound.getFloat("HomingSpeed");
        currentTarget = (LivingEntity) level().getEntity(compound.getInt("Target"));
        super.readAdditionalSaveData(compound);
    }

    private class GunshotDamageCalculator extends ExplosionDamageCalculator{
        private static final float DAMAGE_SCALAR = 2.0f;

        @Override
        public float getEntityDamageAmount(Explosion explosion, Entity entity) {
            float power = explosion.radius() * (damage * DAMAGE_SCALAR);
            Vec3 vec3 = explosion.center();
            double distance = Math.sqrt(entity.distanceToSqr(vec3)) / (double)power;
            double bodyExposed = ((double)1.0F - distance) * (double)Explosion.getSeenPercent(vec3, entity);
            return (float)((bodyExposed * bodyExposed + bodyExposed) / (double)2.0F * (double)7.0F * (double)power + (double)1.0F);
        }
    }
}
