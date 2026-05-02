package com.jamiedev.bygone.common.weather.weather_types;

import com.jamiedev.bygone.common.weather.weather_types.WeatherProperties.*;
import net.minecraft.server.level.ServerLevel;

@SuppressWarnings("rawtypes")
public class InvertedRain extends WeatherType {
    private static final String TIME = "time";
    private static final String IS_RAINING = "isRaining";
    private static final String RAIN_AMOUNT = "rainAmount";

    public InvertedRain(String id) {
        super(id);

        this.registerProperty(WeatherProperties::ofInt, TIME, 0);
        this.registerProperty(WeatherProperties::ofBool, IS_RAINING, false);
        this.registerProperty(WeatherProperties::ofFloat, RAIN_AMOUNT, 0.0f);
    }

    public float getRainAmount() {
        return (float) this.getProperty(RAIN_AMOUNT).getValue();
    }

    @Override
    public void tick(ServerLevel level) {
        WeatherProperty<Integer> time = this.getProperty(TIME);
        WeatherProperty<Boolean> isRaining = this.getProperty(IS_RAINING);
        WeatherProperty<Float> rain = this.getProperty(RAIN_AMOUNT);

        time.setValue(time.getValue() - 1);
        if (time.getValue() <= 0) {
            time.setValue(level.getRandom().nextInt());
        }

        float rainValue = rain.getValue() + (isRaining.getValue() ? 1 : -1) * .01f;
        rain.setValue(Math.clamp(rainValue, 0f, 1f));

        super.tick(level);
    }
}
