package com.jamiedev.bygone.common.weather;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.renderer.weather.InvertedRainRenderer;
import com.jamiedev.bygone.common.weather.weather_types.InvertedRain;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import com.jamiedev.bygone.core.network.PacketHandler;
import com.jamiedev.bygone.core.network.SyncWeatherS2C;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BygoneWeather extends SavedData {
    public static final ResourceKey<Registry<WeatherType.Factory>> WEATHER_TYPE_REGISTRY_KEY = ResourceKey.createRegistryKey(Bygone.id("weather_types"));
    public static Registry<WeatherType.Factory> WEATHER_TYPES;

    public static void bootstrap(Consumer<WeatherType.Factory> consumer) {
        consumer.accept(new WeatherType.Factory("inverted_rain", InvertedRain::new));
    }

    @Nullable private final ServerLevel level;
    public static final String NAME = "bygone_weather";

    private final Collection<WeatherType> instancedWeatherTypes;
    public BygoneWeather(@Nullable ServerLevel level) {
        this.level = level;
        instancedWeatherTypes = WEATHER_TYPES.stream()
            .map(WeatherType.Factory::construct)
            .collect(Collectors.toSet());
    }

    public static BygoneWeather getOrDefault(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                () -> new BygoneWeather(level),
                (tag, provider)
                    -> BygoneWeather.create(tag, provider, level),
                DataFixTypes.LEVEL
            ), BygoneWeather.NAME
        );
    }

    public static BygoneWeather create(CompoundTag tag, HolderLookup.Provider provider, ServerLevel serverLevel) {
        BygoneWeather weather = new BygoneWeather(serverLevel);
        return weather.load(tag, provider);
    }

    public BygoneWeather load(CompoundTag tag, HolderLookup.Provider provider) {
        for (WeatherType instance : instancedWeatherTypes) {
            if (tag.get(instance.getId()) instanceof CompoundTag compoundTag)
                instance.load(compoundTag);
        }
        return this;
    }

    @Override public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        instancedWeatherTypes.forEach((weather)
            -> compoundTag.put(weather.getId(), weather.save()));
        return compoundTag;
    }

    public void tick() {
        CompoundTag tag = new CompoundTag();
        instancedWeatherTypes.forEach(
        (weather) -> {
            weather.tick();
            if (weather.isDirty()) {
                tag.put(
                    weather.getId(),
                    weather.save()
                );
            }
        });
        if (!tag.isEmpty()) {
            PacketHandler.sendPacketToAllInLevel(
                level, new SyncWeatherS2C(tag)
            );
        }
    }

}