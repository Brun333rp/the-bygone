package com.jamiedev.bygone.client.renderer.weather;

import com.jamiedev.bygone.common.weather.weather_types.WeatherType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;

public interface WeatherRenderer<T extends WeatherType> {
    void updateWeatherInstance(T newInstance);
    void tick();
    void render(Level level, LightTexture lightTexture, float partialTick, double camX, double camY, double camZ);
}
