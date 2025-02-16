package ca.dsevvv.sevsuperblaster.particle.provider;

import ca.dsevvv.sevsuperblaster.particle.BoomParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BoomParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public BoomParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vX, double vY, double vZ) {
        return new BoomParticle(clientLevel, x, y, z, spriteSet);
    }
}
