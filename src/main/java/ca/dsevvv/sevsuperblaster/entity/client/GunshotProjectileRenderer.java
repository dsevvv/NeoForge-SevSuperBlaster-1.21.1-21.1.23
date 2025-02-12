package ca.dsevvv.sevsuperblaster.entity.client;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.entity.projectile.GunshotProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GunshotProjectileRenderer<T extends GunshotProjectile> extends EntityRenderer<T> {


    public GunshotProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(T t) {
        return ResourceLocation.fromNamespaceAndPath(SevSuperBlaster.MODID, "textures/entity/gunshot.png");
    }

    @Override
    public void render(T p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1f,1f,1f);
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}