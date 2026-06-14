package com.jamiedev.bygone.client.models;

import com.jamiedev.bygone.client.models.animations.HauntAnimations;
import com.jamiedev.bygone.common.entity.HauntEntity;
import com.jamiedev.bygone.common.entity.MoobooEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.VertexConsumer;


public class WallowModel<T extends Entity> extends HierarchicalModel<T> {

	private final ModelPart body;

	public WallowModel(ModelPart root) {
		this.body = root.getChild("body");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -13.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(0, 24).addBox(-4.0F, -16.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(32, 24).addBox(-3.0F, -20.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition BottomFlap3_r1 = body.addOrReplaceChild("BottomFlap3_r1", CubeListBuilder.create().texOffs(0, 39).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -1.0F, 0.0F, 0.0F, -1.5708F, -0.5236F));

		PartDefinition BottomFlap2_r1 = body.addOrReplaceChild("BottomFlap2_r1", CubeListBuilder.create().texOffs(24, 40).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.5236F));

		PartDefinition BottomFlap2_r2 = body.addOrReplaceChild("BottomFlap2_r2", CubeListBuilder.create().texOffs(24, 36).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, -6.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition BottomFlap1_r1 = body.addOrReplaceChild("BottomFlap1_r1", CubeListBuilder.create().texOffs(0, 35).addBox(-6.0F, 0.0F, 0.0F, 12.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 6.0F, 0.5236F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

		boolean flag = entity.onGround() && entity.getDeltaMovement().lengthSqr() < 1.0E-7;

		if (flag)
		{

		}

		else
		{

		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}

	@Override
	public ModelPart root() {
		return body;
	}
}