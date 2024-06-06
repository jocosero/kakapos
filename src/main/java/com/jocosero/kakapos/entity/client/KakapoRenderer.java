package com.jocosero.kakapos.entity.client;

import com.jocosero.kakapos.Kakapos;
import com.jocosero.kakapos.entity.custom.KakapoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KakapoRenderer extends MobRenderer<KakapoEntity, KakapoModel<KakapoEntity>> {
    public KakapoRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new KakapoModel<>(pContext.bakeLayer(ModModelLayers.KAKAPO_LAYER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(KakapoEntity instance) {
        return new ResourceLocation(Kakapos.MOD_ID, "textures/entity/kakapo.png");
    }

    @Override
    public void render(KakapoEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }
}
