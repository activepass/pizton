package com.redstoned.pizton.module;

import carpet.CarpetSettings;
import com.mojang.brigadier.Command;
import com.redstoned.pizton.Pizton;
import com.redstoned.pizton.machinery.StaticModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class ForceCarpetNoclip extends StaticModule {
    public static Logger LOGGER = Pizton.loggerFor(TradeCopier.class);

    @Override
    public void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> dispatcher.register(
            literal("cnoclip")
                .executes(ctx -> {
                    if (!FabricLoader.getInstance().isModLoaded("carpet")) {
                        ctx.getSource().sendError(Component.literal("Carpet is not loaded!"));
                        return 0;
                    }

                    ctx.getSource().sendFeedback(Component.literal(
                        (CarpetSettings.creativeNoClip ? "disabled" : "enabled")
                            + " carpet noclip"
                        ));
                    CarpetSettings.creativeNoClip = !CarpetSettings.creativeNoClip;


                    return Command.SINGLE_SUCCESS;
                })
        ));
    }

    @Override
    public String description() {
        return "Force enable carpet noclip (client only)";
    }
}
