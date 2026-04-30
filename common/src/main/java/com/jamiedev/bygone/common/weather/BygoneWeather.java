package com.jamiedev.bygone.common.weather;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

public class BygoneWeather {
    public static final ResourceKey<Registry<WeatherType>> WEATHER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Bygone.id("weather_types"));
    public static Registry<WeatherType> WEATHER_TYPES;

    public static void bootstrap(Consumer<WeatherType> consumer) {
        consumer.accept(new WeatherType("weather_type"));
        consumer.accept(new WeatherType("weather_type_tuah"));
    }
}
