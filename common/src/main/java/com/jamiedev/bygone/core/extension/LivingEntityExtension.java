package com.jamiedev.bygone.core.extension;

public interface LivingEntityExtension {
    default void bygone$setHauntingsMob(boolean setTo) {};
    default boolean bygone$isHauntingsMob() { return false; }
}
