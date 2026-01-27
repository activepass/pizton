package com.redstoned.pizton;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.redstoned.pizton.machinery.PiztonModule;
import com.redstoned.pizton.machinery.ToggleableModule;
import com.redstoned.pizton.module.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class Pizton implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Pizton");
	//todo: maybe better to use singletons? idk
	private static final HashMap<String, PiztonModule> modules = new HashMap<>();

	static {
		Pizton.registerModule(new ThunderYeller());
		Pizton.registerModule(new MouseToggleCompat());
		Pizton.registerModule(new TradeCopier());
		Pizton.registerModule(new TabCopy());
		Pizton.registerModule(new AlwaysShowMapId());
		LOGGER.debug("done register");
	}

	@Override
	public void onInitializeClient() {
		registerCommand();
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> saveConfig());

		LOGGER.debug("init modules");
		Config config = Config.load();
        for (var mod_entry : modules.entrySet()) {
			PiztonModule module = mod_entry.getValue();
			module.init();

			if (module instanceof ToggleableModule && config.enabled_modules().contains(mod_entry.getKey())) {
				((ToggleableModule) module).enable();
			}
		}
	}

	public static void saveConfig() {
		new Config(
			modules
				.entrySet()
				.stream()
				.filter(e -> e.getValue() instanceof ToggleableModule && ((ToggleableModule) e.getValue()).enabled())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList())
		).save();
	}

	public static void registerModule(PiztonModule module) {
		LOGGER.info("Registered module: {}", module.getClass().getName());
		modules.put(module.getClass().getSimpleName(), module);
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
					.suggests((ctx, b) ->
						SharedSuggestionProvider.suggest(
							modules
								.entrySet()
								.stream()
								.filter(e ->
									e.getValue() instanceof ToggleableModule
									&& !((ToggleableModule) e.getValue()).enabled()
								)
								.map(Map.Entry::getKey)
								.collect(Collectors.toSet()), b))
					.executes(context -> {
						String module_name = StringArgumentType.getString(context, "module");
						LOGGER.info("Enabling module: {}", module_name);
						PiztonModule module = modules.get(module_name);
						if (module == null) {
							context.getSource().sendError(Component.literal("Not a known module"));
							return 0;
						} else if (!(module instanceof ToggleableModule)) {
							context.getSource().sendError(Component.literal("This module cannot be enabled"));
							return 0;
						}
						ToggleableModule tmod = (ToggleableModule) module;
						if (tmod.enabled()) {
							context.getSource().sendError(Component.literal("This module is already enabled"));
							return 0;
						}

						tmod.enable();
						context.getSource().sendFeedback(Component.literal("Enabled module: " + module_name));
						return Command.SINGLE_SUCCESS;
					})
				)
			)
			.then(literal("disable")
				.then(argument("module", StringArgumentType.word())
					.suggests((ctx, b) ->
						SharedSuggestionProvider.suggest(
							modules
							.entrySet()
							.stream()
							.filter(e ->
								e.getValue() instanceof ToggleableModule
								&& ((ToggleableModule) e.getValue()).enabled()
							)
							.map(Map.Entry::getKey)
							.collect(Collectors.toSet()), b))
					.executes(context -> {
						String module_name = StringArgumentType.getString(context, "module");
						LOGGER.info("Disabling module: {}", module_name);
						PiztonModule module = modules.get(module_name);
						if (module == null) {
							context.getSource().sendError(Component.literal("Not a known module"));
							return 0;
						} else if (!(module instanceof ToggleableModule)) {
							context.getSource().sendError(Component.literal("This module cannot be disabled"));
							return 0;
						}
						ToggleableModule tmod = (ToggleableModule) module;
						if (!tmod.enabled()) {
							context.getSource().sendError(Component.literal("This module is already disabled"));
							return 0;
						}

						tmod.disable();
						context.getSource().sendFeedback(Component.literal("Disabled module: " + module_name));
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
									.withHoverEvent(new HoverEvent.ShowText(Component.literal(
										String.format("%s [%s]", module.getValue().description(), module.getValue().kind)
									)))
									.withColor(module.getValue().color())
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