package com.redstoned.pizton;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.redstoned.pizton.module.MouseToggleCompat;
import com.redstoned.pizton.module.ThunderYeller;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class Pizton implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Pizton");
	private static final HashMap<String, PiztonModule> modules = new HashMap<>();

	@Override
	public void onInitializeClient() {
		registerCommand();

		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveConfig());

        Pizton.registerModule(new ThunderYeller());
		Pizton.registerModule(new MouseToggleCompat());

		Config config = Config.load();
		for (String m : config.enabled_modules()) {
			if (!modules.containsKey(m)) continue;
			modules.get(m).enable();
		}
	}

	public static void saveConfig() {
		new Config(
			modules
				.entrySet()
				.stream()
				.filter(e -> e.getValue().enabled())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList())
		).save();
	}

	public static void registerModule(PiztonModule module) {
		LOGGER.info("Registered module: {}", module.getClass().getName());
		modules.put(module.getClass().getSimpleName(), module);
		module.register();
	}

	public static Logger loggerFor(Class<?> clazz) {
		return LoggerFactory.getLogger(String.format("Pizton/%s", clazz.getSimpleName()));
	}

	public static PiztonModule fetchModule(Class<?> clazz) {
		return modules.get(clazz.getSimpleName());
	}

	public void registerCommand() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("pz")
			.then(literal("enable")
				.then(argument("module", StringArgumentType.word())
					.suggests((ctx, b) -> SharedSuggestionProvider.suggest(modules.entrySet().stream().filter(e -> !e.getValue().enabled()).map(Map.Entry::getKey).collect(Collectors.toSet()), b))
					.executes(context -> {
						String module_name = StringArgumentType.getString(context, "module");
						LOGGER.info("Enabling module: {}", module_name);
						PiztonModule module = modules.get(module_name);
						if (module == null) {
							context.getSource().sendError(Component.literal("Not a known module"));
							return 0;
						} else if (module.ENABLED) {
							context.getSource().sendError(Component.literal("This module is already enabled"));
							return 0;
						}

						module.enable();
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(literal("disable")
				.then(argument("module", StringArgumentType.word())
					.suggests((ctx, b) -> SharedSuggestionProvider.suggest(modules.entrySet().stream().filter(e -> e.getValue().enabled()).map(Map.Entry::getKey).collect(Collectors.toSet()), b))
					.executes(context -> {
						String module_name = StringArgumentType.getString(context, "module");
						LOGGER.info("Disabling module: {}", module_name);
						PiztonModule module = modules.get(module_name);
						if (module == null) {
							context.getSource().sendError(Component.literal("Not a known module"));
							return 0;
						} else if (!module.enabled()) {
							context.getSource().sendError(Component.literal("This module is already disabled"));
							return 0;
						}

						module.disable();
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(literal("list")
				.executes(ctx -> {
					MutableComponent base = Component.literal("Modules: ");
					for (var module : modules.entrySet()) {
						base.append(
							Component
								.literal(String.format("%s ", module.getKey()))
								.setStyle(
									Style.EMPTY
									.withHoverEvent(new HoverEvent.ShowText(Component.literal(module.getValue().description())))
									.withColor(module.getValue().enabled() ? ChatFormatting.GREEN : ChatFormatting.RED)
								)
						);
					}

					ctx.getSource().sendFeedback(base);
					return Command.SINGLE_SUCCESS;
				})
			)
		));
	}
}