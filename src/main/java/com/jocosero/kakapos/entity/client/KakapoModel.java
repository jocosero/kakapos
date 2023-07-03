package com.jocosero.kakapos.entity.client;

import com.jocosero.kakapos.Kakapos;
import com.jocosero.kakapos.entity.custom.KakapoEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class KakapoModel extends AnimatedGeoModel<KakapoEntity> {
    @Override
    public ResourceLocation getModelResource(KakapoEntity object) {
        return new ResourceLocation(Kakapos.MOD_ID, "geo/kakapo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KakapoEntity object) {
        return new ResourceLocation(Kakapos.MOD_ID, "textures/entity/kakapo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KakapoEntity animatable) {
        return new ResourceLocation(Kakapos.MOD_ID, "animations/kakapo.animation.json");
    }

    @Override
    public void setCustomAnimations(KakapoEntity animatable, int instanceId, AnimationEvent animationEvent) {
        super.setCustomAnimations(animatable, instanceId, animationEvent);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone wing_right = this.getAnimationProcessor().getBone("wing_right");
        IBone wing_left = this.getAnimationProcessor().getBone("wing_left");
        EntityModelData extraData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * 0.010453292F);
        head.setRotationY(extraData.netHeadYaw * 0.015453292F);
        wing_right.setRotationZ(animationEvent.getLimbSwingAmount());
        wing_left.setRotationZ(-(animationEvent.getLimbSwingAmount()));

    }
}
