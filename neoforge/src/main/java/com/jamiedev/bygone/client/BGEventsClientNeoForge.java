package com.jamiedev.bygone.client;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.screen.PortalOverlay;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.event.entity.player.PlayerHeartTypeEvent;

@EventBusSubscriber(modid = Bygone.MOD_ID, value = Dist.CLIENT)
public class BGEventsClientNeoForge {
    @SubscribeEvent
    public static void onPlayerHeartTypeEvent(PlayerHeartTypeEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide() && player.hasEffect(BGMobEffects.HAUNTED.get())) {
             event.setType(BGEnumClientExtensionsNeoForge.BYGONE_HEART_HAUNTED.getValue());
        }
    }
}