package com.jamiedev.bygone.common.weather.weather_types;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import net.minecraft.resources.ResourceKey;

public record WeatherType(String identifier) {
    public ResourceKey<WeatherType> getKey() {
        return ResourceKey.create(
            BygoneWeather.WEATHER_TYPE_REGISTRY_KEY,
            Bygone.id(identifier)
        );
    }
}
