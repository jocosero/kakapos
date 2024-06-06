package com.jocosero.kakapos.entity.client;

import com.jocosero.kakapos.entity.animations.KakapoAnimationDefinitions;
import com.jocosero.kakapos.entity.custom.KakapoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class KakapoModel<T extends Entity> extends HierarchicalModel<T> {

    private final ModelPart kakapo;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart tail;
    private final ModelPart wing_right;
    private final ModelPart wing_left;
    private final ModelPart leg_left;
    private final ModelPart leg_right;

    public KakapoModel(ModelPart root) {
        this.kakapo = root.getChild("kakapo");
        this.head = kakapo.getChild("head");
        this.body = kakapo.getChild("body");
        this.torso = kakapo.getChild("body").getChild("torso");
        this.tail = kakapo.getChild("body").getChild("torso").getChild("tail");
        this.wing_right = kakapo.getChild("body").getChild("torso").getChild("wing_right");
        this.wing_left = kakapo.getChild("body").getChild("torso").getChild("wing_left");
        this.leg_left = kakapo.getChild("body").getChild("leg_left");
        this.leg_right = kakapo.getChild("body").getChild("leg_right");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition kakapo = partdefinition.addOrReplaceChild("kakapo", CubeListBuilder.create(), PartPose.offset(0.0F, 17.5F, -0.25F));

        PartDefinition head = kakapo.addOrReplaceChild("head", CubeListBuilder.create().texOffs(17, 17).addBox(-2.5F, -1.0F, -4.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(25, 0).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 6).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, -4.0F));

        PartDefinition body = kakapo.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.5F, -4.5F, -2.75F, -0.3927F, 0.0F, 0.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.0F, -5.0F, 7.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 3.5F, 5.0F));

        PartDefinition tail = torso.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(13, 26).addBox(-2.5F, 0.0F, 0.0F, 5.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 29).addBox(-2.5F, 0.0F, 4.0F, 5.0F, 0.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, 5.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition wing_right = torso.addOrReplaceChild("wing_right", CubeListBuilder.create(), PartPose.offset(-3.5F, -2.5F, -3.25F));

        PartDefinition wing_r1 = wing_right.addOrReplaceChild("wing_r1", CubeListBuilder.create().texOffs(0, 17).mirror().addBox(-1.0F, 0.5F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -1.0F, -0.75F, 0.1309F, 0.0F, 0.0F));

        PartDefinition wing_left = torso.addOrReplaceChild("wing_left", CubeListBuilder.create(), PartPose.offset(3.5F, -2.5F, -3.25F));

        PartDefinition wing_r2 = wing_left.addOrReplaceChild("wing_r2", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, 0.5F, 0.0F, 1.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -0.75F, 0.1309F, 0.0F, 0.0F));

        PartDefinition leg_left = body.addOrReplaceChild("leg_left", CubeListBuilder.create(), PartPose.offset(1.0F, 6.0F, 8.0F));

        PartDefinition leg_r1 = leg_left.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(9, 17).addBox(0.5F, 2.35F, -3.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(13, 18).mirror().addBox(0.5F, 1.35F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(0.0F, -0.65F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition leg_right = body.addOrReplaceChild("leg_right", CubeListBuilder.create(), PartPose.offset(1.0F, 6.0F, 8.0F));

        PartDefinition leg_r2 = leg_right.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(9, 17).mirror().addBox(-3.5F, 2.35F, -3.0F, 2.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(13, 18).addBox(-2.5F, 1.35F, -1.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -0.65F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3927F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(KakapoAnimationDefinitions.KAKAPO_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animate(((KakapoEntity) entity).idleAnimationState, KakapoAnimationDefinitions.KAKAPO_IDLE, ageInTicks, 1f);
        this.animate(((KakapoEntity) entity).danceAnimationState, KakapoAnimationDefinitions.KAKAPO_DANCE, ageInTicks, 1f);
        this.animate(((KakapoEntity) entity).sitAnimationState, KakapoAnimationDefinitions.KAKAPO_SIT, ageInTicks, 1f);
        this.animate(((KakapoEntity) entity).preenAnimationState, KakapoAnimationDefinitions.KAKAPO_PREEN, ageInTicks, 1f);
        this.animate(((KakapoEntity) entity).fallAnimationState, KakapoAnimationDefinitions.KAKAPO_FALL, ageInTicks, 1f);
        this.animate(((KakapoEntity) entity).runAnimationState, KakapoAnimationDefinitions.KAKAPO_RUN, ageInTicks, 1f);

        this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
        this.head.xRot = headPitch / (180F / (float) Math.PI);
        this.wing_right.yRot = - 0.3F * limbSwingAmount;
        this.wing_left.yRot = 0.3F *limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        kakapo.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() { return kakapo;
    }
}
