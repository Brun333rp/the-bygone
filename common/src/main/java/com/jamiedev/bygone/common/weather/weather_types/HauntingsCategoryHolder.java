package com.jamiedev.bygone.common.weather.weather_types;

import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class HauntingsCategoryHolder {
    public static final String CATEGORY_NAME = "bygone:hauntings_mobs";
    public static final int CATEGORY_MOB_CAP = 1;
    public static final boolean CATEGORY_FRIENDLY = false;
    public static final boolean CATEGORY_PERSISTENT = false;
    public static final int CATEGORY_DESPAWN_RANGE = 128;

    public static final MobCategory HAUNTING_MOB_CATEGORY = MobCategory.valueOf("BYGONE_HAUNTINGS_MOB");
}
