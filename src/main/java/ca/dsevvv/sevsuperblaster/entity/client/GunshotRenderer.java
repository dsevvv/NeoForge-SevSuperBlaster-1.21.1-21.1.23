package ca.dsevvv.sevsuperblaster.entity.client;

import ca.dsevvv.sevsuperblaster.SevSuperBlaster;
import ca.dsevvv.sevsuperblaster.entity.projectile.Gunshot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GunshotRenderer<T extends Gunshot> extends EntityRenderer<T> {
    private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(SevSuperBlaster.MODID, "textures/entity/gunshot.png");
    private final GunshotModel model;

    public GunshotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new GunshotModel(context.bakeLayer(GunshotModel.LAYER_LOCATION));
    }

    @Override
    public ResourceLocation getTextureLocation(Gunshot gunshot) {
        return TEXTURE_LOCATION;
    }

    @Override
    public void render(T p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.scale(1f,1f,1f);
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}