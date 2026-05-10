package com.jamiedev.bygone.client.screen;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.BygoneClient;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class PortalOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation TEXTURE_VIGNETTE = ResourceLocation.fromNamespaceAndPath(Bygone.MOD_ID, "textures/gui/overlay/vignette.png");
    private static final ResourceLocation TEXTURE_PORTAL = ResourceLocation.fromNamespaceAndPath(Bygone.MOD_ID, "textures/gui/overlay/portal.png");
    private float alpha = 0f;
    private boolean invert = false;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (BygoneClient.portalTimeout <= 0) invert = true;
        if (BygoneClient.portalTimeout > 0) invert = false;

        if (BygoneClient.portalOverlay > 0) {
            var mc = Minecraft.getInstance();
            if (mc.level == null || mc.player == null || mc.options.hideGui) return;
//
//            alpha = BygoneClient.portalOverlay;
//
//            if (invert) BygoneClient.portalOverlay = BygoneClient.portalOverlay - 0.01f ;

            alpha = alpha + 0.005f;
            if (invert) alpha = alpha - 0.01f ;

            alpha = Math.clamp(alpha, 0, 1);

            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();

            int portalWidth = 128;
            int portalHeight = 128;

            int tlX = (int) Mth.lerp(alpha, -portalWidth, 0);
            int tlY = (int) Mth.lerp(alpha, -portalHeight, 0);

            int trX = (int) Mth.lerp(alpha, screenWidth + portalWidth, screenWidth);
            int trY = (int) Mth.lerp(alpha, -portalHeight, 0);

            int blX = (int) Mth.lerp(alpha, -portalWidth, 0);
            int blY = (int) Mth.lerp(alpha, screenHeight + portalHeight, screenHeight);

            int brX = (int) Mth.lerp(alpha, screenWidth + portalWidth, screenWidth);
            int brY = (int) Mth.lerp(alpha, screenHeight + portalHeight, screenHeight);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            guiGraphics.setColor(1f, 1f, 1f, alpha);
            guiGraphics.blit(TEXTURE_VIGNETTE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

            guiGraphics.setColor(1f, 1f, 1f, 1f);

            guiGraphics.blit(TEXTURE_PORTAL, tlX, tlY, 0, 0, portalWidth, portalHeight, portalWidth, portalHeight);

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            poseStack.translate(trX, trY, 0);
            poseStack.mulPose(Axis.ZP.rotation((float) Math.PI/2f));
            guiGraphics.blit(TEXTURE_PORTAL, 0, 0, 0, 0, portalWidth, portalHeight, portalWidth, portalHeight);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(blX, blY, 0);
            poseStack.mulPose(Axis.ZN.rotation((float) Math.PI/2f));
            guiGraphics.blit(TEXTURE_PORTAL, 0, 0, 0, 0, portalWidth, portalHeight, portalWidth, portalHeight);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(brX, brY, 0);
            poseStack.mulPose(Axis.ZP.rotation((float) Math.PI));
            guiGraphics.blit(TEXTURE_PORTAL, 0, 0, 0, 0, portalWidth, portalHeight, portalWidth, portalHeight);
            poseStack.popPose();

            guiGraphics.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();

            if (BygoneClient.portalTimeout > 0)
                BygoneClient.portalTimeout = BygoneClient.portalTimeout-1;
        }
    }
}
