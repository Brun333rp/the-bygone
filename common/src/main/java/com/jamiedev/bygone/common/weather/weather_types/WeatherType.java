package com.jamiedev.bygone.common.weather.weather_types;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.renderer.weather.WeatherRenderer;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class WeatherType {

    private final String id;
    public String getId() {
        return this.id;
    }

    public WeatherType(String id) {
        this.id = id;
    }

    private boolean dirty = false;
    public void setDirty() {
        dirty = true;
    }
    public boolean isDirty() {
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    public void tick() {}

    public void load(CompoundTag tag) {}

    public CompoundTag save() {
        return new CompoundTag();
    }

    @SuppressWarnings("rawtypes")
    public static class Factory<T extends WeatherType> {
        private final String id;
        private final Function<String, T> supplier;
        private final Supplier<Supplier<WeatherRenderer<T>>> renderer;
        public Factory(
            String id,
            Function<String, T> supplier,
            Supplier<Supplier<WeatherRenderer<T>>> renderer
        ) {
            this.id = id;
            this.supplier = supplier;
            this.renderer = renderer;
        }

        public WeatherType construct() {
            Bygone.LOGGER.info("constructing weather of type {}", id);
            return supplier.apply(id);
        }

        public WeatherRenderer<T> getRenderer() {
            return renderer.get().get();
        }

        public ResourceKey<Factory> getKey() {
            return ResourceKey.create(
                BygoneWeather.WEATHER_TYPE_REGISTRY_KEY,
                Bygone.id(id)
            );
        }
    }
}
