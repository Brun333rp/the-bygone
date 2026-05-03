package com.jamiedev.bygone.common.commands;

import com.jamiedev.bygone.common.weather.BygoneWeather;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;

public class BygoneWeatherCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("bygone_weather")
            .requires(stack -> stack.hasPermission(2))
            .then(
                Commands.argument("weather_type", ResourceArgument.resource(context, BygoneWeather.WEATHER_TYPE_REGISTRY_KEY))
                    .then(Commands.literal("start").executes(DreamCommand::startDream))
            )
        );
    }
}
