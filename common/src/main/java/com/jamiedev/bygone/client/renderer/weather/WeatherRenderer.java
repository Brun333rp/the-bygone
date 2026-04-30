package com.jamiedev.bygone.client.renderer.weather;

import net.minecraft.client.renderer.LightTexture;

public interface WeatherRenderer {
    void render(LightTexture lightTexture, float partialTick, double camX, double camY, double camZ);
}
