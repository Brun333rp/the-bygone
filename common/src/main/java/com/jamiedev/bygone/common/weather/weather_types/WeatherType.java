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

//    public WeatherRenderer getRenderer() {
//        return renderer.get().get();
//    }

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

    public static class Factory {
        private final String id;
        private final Function<String, WeatherType> supplier;
        public Factory(String id, Function<String, WeatherType> supplier) {
            this.id = id;
            this.supplier = supplier;
        }

        public WeatherType construct() {
            return supplier.apply(id);
        }

        public ResourceKey<Factory> getKey() {
            return ResourceKey.create(
                BygoneWeather.WEATHER_TYPE_REGISTRY_KEY,
                Bygone.id(id)
            );
        }
    }
}
