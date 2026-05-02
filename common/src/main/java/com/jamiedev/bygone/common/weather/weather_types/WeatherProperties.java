package com.jamiedev.bygone.common.weather.weather_types;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class WeatherProperties {
    public static class WeatherProperty<T> {
        private T value;
        private boolean isDirty = false;
        public boolean checkDirty() {
            if (isDirty) {
                isDirty = false;
                return true;
            }
            return false;
        }

        public void setValue(T newValue) {
            if (this.value.equals(newValue)) return;
            this.value = newValue;
            this.isDirty = true;
        }

        public T getValue() {
            return this.value;
        }

        private final Codec<T> codec;
        public Codec<T> codec() {
            return this.codec;
        }

        public Optional<Tag> encodeSelf() {
            return codec.encodeStart(NbtOps.INSTANCE, value).result();
        }

        public void appendToTag(CompoundTag compoundTag) {
            encodeSelf().ifPresent((tag) -> compoundTag.put(getIdentifier(), tag));
        }

        public void parseSelf(CompoundTag compoundTag) {
            if (compoundTag.get(this.getIdentifier()) == null) return;
            codec.parse(NbtOps.INSTANCE, compoundTag.get(this.getIdentifier()))
                .result().ifPresent(this::setValue);
        }

        private final String identifier;
        public String getIdentifier() {
            return this.identifier;
        }

        public record Receive<T>(T value, String owner) {}

        WeatherProperty(String identifier, T value, Codec<T> codec, String ownerIdentifier) {
            this.value = value;
            this.identifier = identifier;
            this.codec = codec;
        }
    }

    public static WeatherProperty<Boolean> ofBool(String identifier, Boolean defaultValue, String ownerIdentifier) {
        return new WeatherProperty<Boolean>(
            identifier, defaultValue,
            Codec.BOOL, ownerIdentifier
        );
    }

    public static WeatherProperty<Integer> ofInt(String identifier, Integer defaultValue, String ownerIdentifier) {
        return new WeatherProperty<Integer>(
            identifier, defaultValue,
            Codec.INT, ownerIdentifier
        );
    }

    public static WeatherProperty<Float> ofFloat(String identifier, Float defaultValue, String ownerIdentifier) {
        return new WeatherProperty<Float>(
            identifier, defaultValue,
            Codec.FLOAT, ownerIdentifier
        );
    }
}