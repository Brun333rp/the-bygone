package com.jamiedev.bygone.common.weather;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.renderer.weather.InvertedRainRenderer;
import com.jamiedev.bygone.client.renderer.weather.WeatherRenderer;
import com.jamiedev.bygone.common.weather.weather_types.InvertedRain;
import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import com.jamiedev.bygone.core.network.PacketHandler;
import com.jamiedev.bygone.core.network.SyncWeatherS2C;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public class BygoneWeather extends SavedData {
    public static final ResourceKey<Registry<WeatherType.Factory>> WEATHER_TYPE_REGISTRY_KEY
        = ResourceKey.createRegistryKey(Bygone.id("weather_types"));
    public static Registry<WeatherType.Factory> WEATHER_TYPES;

    public static void bootstrap(Consumer<WeatherType.Factory> consumer) {
        consumer.accept(new WeatherType.Factory<InvertedRain>("inverted_rain", InvertedRain::new, () -> InvertedRainRenderer::new));
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
            Tag weatherTag = tag.get(instance.getId());
            if (weatherTag == null) continue;
            if (weatherTag instanceof CompoundTag compoundTag) instance.load(compoundTag);
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

    public static class Client {
        private static Client INSTANCE;
        public static Client getInstance() {
            if (INSTANCE == null) INSTANCE = new Client();
            return INSTANCE;
        }

        private final BygoneWeather weatherContext = new BygoneWeather(null);
        public void updateContext(CompoundTag compoundTag) {
            weatherContext.load(compoundTag, null);
        }

        private final Collection<WeatherRenderer> instancedWeatherRenderers;
        public Stream<WeatherRenderer> stream() {
            return instancedWeatherRenderers.stream();
        }
        private Client() {
            instancedWeatherRenderers = WEATHER_TYPES.stream()
                .map(WeatherType.Factory::getRenderer)
                .collect(Collectors.toSet());
        }
    }
}