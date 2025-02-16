package ca.dsevvv.sevsuperblaster.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;

public class SparkParticle extends TextureSheetParticle {
    private final SpriteSet spriteSet;

    public SparkParticle(ClientLevel level, double x, double y, double z, double vX, double vY, double vZ, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.gravity = random.nextFloat() * 0.1F;
        this.lifetime = 30;
        this.hasPhysics = true;
        this.setParticleSpeed(vX, vY, vZ);
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(spriteSet);
        super.tick();
    }
}
