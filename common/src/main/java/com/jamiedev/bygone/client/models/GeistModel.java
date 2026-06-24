package com.jamiedev.bygone.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

public class GeistModel<T extends Entity> extends EntityModel<T> {
	private final ModelPart Body;
	private final ModelPart ArmRight;
	private final ModelPart ArmLeft;
	private final ModelPart Mouth;

	public GeistModel(ModelPart root) {
		this.Body = root.getChild("Body");
		this.ArmRight = this.Body.getChild("ArmRight");
		this.ArmLeft = this.Body.getChild("ArmLeft");
		this.Mouth = root.getChild("Mouth");
	}

	public static LayerDefinition getTexturedModelData() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -20.0F, -4.0F, 8.0F, 18.0F, 8.0F, new CubeDeformation(0.0F))
				.texOffs(0, 48).addBox(-2.0F, -17.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

		PartDefinition LowerBody_r1 = Body.addOrReplaceChild("LowerBody_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-15.04F, -6.95F, -4.0F, 18.0F, 14.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, 4.1F, 0.0F, 0.0F, 0.0F, 0.2182F));

		PartDefinition Back_r1 = Body.addOrReplaceChild("Back_r1", CubeListBuilder.create().texOffs(32, 22).addBox(-4.0F, -14.0F, -4.0F, 7.0F, 13.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.5672F));

		PartDefinition ArmRight = Body.addOrReplaceChild("ArmRight", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, -4.0F));

		PartDefinition cube_r1 = ArmRight.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 44).addBox(-2.0F, -2.0F, -3.0F, 13.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, -1.1F, -0.0094F, 0.0426F, 0.3489F));

		PartDefinition ArmLeft = Body.addOrReplaceChild("ArmLeft", CubeListBuilder.create(), PartPose.offset(0.0F, -1.0F, 4.0F));

		PartDefinition cube_r2 = ArmLeft.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(28, 44).addBox(-2.0F, -2.0F, -3.0F, 13.0F, 13.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -2.0F, 3.1F, 0.0094F, -0.0426F, 0.3489F));

		PartDefinition Mouth = partdefinition.addOrReplaceChild("Mouth", CubeListBuilder.create(), PartPose.offsetAndRotation(4.0F, 10.0F, 0.0F, 0.0F, 1.5708F, 0.2182F));

		PartDefinition cube_r3 = Mouth.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 44).addBox(1.2F, -11.0F, -2.7F, 0.0F, 10.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8F, 1.0F, -3.0F, 0.2182F, 0.0F, 0.1745F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		Body.render(poseStack, vertexConsumer, light, overlay, color);
		Mouth.render(poseStack, vertexConsumer, light, overlay, color);
	}
}