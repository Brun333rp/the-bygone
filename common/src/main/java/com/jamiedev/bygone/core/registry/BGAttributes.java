package com.jamiedev.bygone.core.registry;

import com.google.common.base.Suppliers;
import com.jamiedev.bygone.Bygone;
import com.kekecreations.jinxedlib.core.util.JinxedRegistryHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Supplier;

public class BGAttributes {

	public static Supplier<Holder<Attribute>> PHASING_DURATION = register("phasing_duration", () ->
		new RangedAttribute("attribute.name.phasing_duration", 0, 0, 1024).setSyncable(true)
	);

	private static <T extends Attribute> Supplier<Holder<Attribute>> register(String name, Supplier<T> supplier) {
		JinxedRegistryHelper.register(BuiltInRegistries.ATTRIBUTE, Bygone.MOD_ID, name, supplier);
		return Suppliers.memoize(() -> BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.fromNamespaceAndPath(Bygone.MOD_ID, name)).orElseThrow());
	}

	public static void init() {}

}
