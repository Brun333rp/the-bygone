package com.jamiedev.bygone.common.weather.weather_types;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface WeatherProperty {
    StreamCodec<FriendlyByteBuf, ? extends WeatherProperty> codec();
}
