package ca.dsevvv.sevsuperblaster.particle.provider;

import ca.dsevvv.sevsuperblaster.particle.TrailParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class TrailParticleProvider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteSet;

    public TrailParticleProvider(SpriteSet spriteSet) {
        this.spriteSet = spriteSet;
    }

    @Override
    public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vX, double vY, double vZ) {
        return new TrailParticle(clientLevel, x, y, z, spriteSet);
    }
}
