package com.jamiedev.bygone.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = Bygone.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BGCommonSetupEvents {
    @SubscribeEvent
    public static void addRegistries(final NewRegistryEvent event) {
        event.register(BygoneWeather.WEATHER_TYPES);
    }
}
